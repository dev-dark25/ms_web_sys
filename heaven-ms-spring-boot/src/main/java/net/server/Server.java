/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Security;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import cn.nap.constant.NapPunc;
import cn.nap.utils.common.NapComUtils;
import cn.nap.utils.common.NapMapUtils;
import com.heavenMS.component.ApplicationContextUtil;
import com.heavenMS.websocket.WsServerEndpoint;
import config.CommonConfig;
import config.WorldConfig;
import net.server.audit.ThreadTracker;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;

import net.MapleServerHandler;
import net.mina.MapleCodecFactory;
import net.server.channel.Channel;
import net.server.coordinator.session.MapleSessionCoordinator;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import net.server.task.BossLogTask;
import net.server.task.CharacterDiseaseTask;
import net.server.task.CouponTask;
import net.server.task.EventRecallCoordinatorTask;
import net.server.task.DueyFredrickTask;
import net.server.task.InvitationTask;
import net.server.task.LoginCoordinatorTask;
import net.server.task.LoginStorageTask;
import net.server.task.RankingCommandTask;
import net.server.task.RankingLoginTask;
import net.server.task.ReleaseLockTask;
import net.server.task.RespawnTask;
import net.server.world.World;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import client.MapleClient;
import client.MapleFamily;
import client.MapleCharacter;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.manipulator.MapleCashidGenerator;
import client.newyear.NewYearCardRecord;
import constants.inventory.ItemConstants;
import constants.game.GameConstants;
import constants.net.OpcodeConstants;
import constants.net.ServerConstants;

import java.util.TimeZone;
import java.util.stream.Collectors;

import server.cashshop.CashShop.CashItemFactory;
import server.ThreadManager;
import server.TimerManager;
import server.expeditions.MapleExpeditionBossLog;
import server.life.MapleMonsterInformationProvider;
import server.life.MaplePlayerNPCFactory;
import server.quest.MapleQuest;
import tools.AutoJCE;
import tools.MysqlConnection;
import tools.FilePrinter;
import tools.Pair;
import tools.Log;
import tools.SqliteConnection;
import tools.StringUtil;
import tools.sql.SqlOperator;

public class Server {
    static {
        System.setProperty("wzpath", "wz");
        Security.setProperty("crypto.policy", "unlimited");
        AutoJCE.removeCryptographyRestrictions();
    }

    private static Server instance = null;

    public static Server getInstance() {
//        System.out.println("Server getInstance");
        if (instance == null) {
            System.out.println("new Server");
            instance = new Server();
        }
        return instance;
    }

    private static final Set<Integer> activeFly = new HashSet<>();
    private static final Map<Integer, Integer> couponRates = new HashMap<>(30);
    private static final List<Integer> activeCoupons = new LinkedList<>();

    private IoAcceptor acceptor;
    private List<Map<Integer, String>> channels = new LinkedList<>();
    private List<World> worlds = new ArrayList<>();
    private final Properties subnetInfo = new Properties();
    private final Map<Integer, Set<Integer>> accountChars = new HashMap<>();
    private final Map<Integer, Short> accountCharacterCount = new HashMap<>();
    private final Map<Integer, Integer> worldChars = new HashMap<>();
    private final Map<String, Integer> transitioningChars = new HashMap<>();
    private List<Pair<Integer, String>> worldRecommendedList = new LinkedList<>();
    private final Map<Integer, MapleGuild> guilds = new HashMap<>(100);
    private final Map<MapleClient, Long> inLoginState = new HashMap<>(100);

    private final PlayerBuffStorage buffStorage = new PlayerBuffStorage();
    private final Map<Integer, MapleAlliance> alliances = new HashMap<>(100);
    private final Map<Integer, NewYearCardRecord> newyears = new HashMap<>();
    private final List<MapleClient> processDiseaseAnnouncePlayers = new LinkedList<>();
    private final List<MapleClient> registeredDiseaseAnnouncePlayers = new LinkedList<>();

    private final List<List<Pair<String, Integer>>> playerRanking = new LinkedList<>();

    private final Lock srvLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER);
    private final Lock disLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER_DISEASES);

    private final MonitoredReentrantReadWriteLock wldLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_WORLDS, true);
    private final MonitoredReadLock wldRLock = MonitoredReadLockFactory.createLock(wldLock);
    private final MonitoredWriteLock wldWLock = MonitoredWriteLockFactory.createLock(wldLock);

    private final MonitoredReentrantReadWriteLock lgnLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_LOGIN, true);
    private final MonitoredReadLock lgnRLock = MonitoredReadLockFactory.createLock(lgnLock);
    private final MonitoredWriteLock lgnWLock = MonitoredWriteLockFactory.createLock(lgnLock);

    private final AtomicLong currentTime = new AtomicLong(0);
    private long serverCurrentTime = 0;

    private boolean availableDeveloperRoom = false;
    private boolean online = false;
    public static long uptime = System.currentTimeMillis();

    public int getCurrentTimestamp() {
        return (int) (Server.getInstance().getCurrentTime() - Server.uptime);
    }

    public long getCurrentTime() {  // 返回一个稍微延迟的时间值，频率为UPDATE_INTERVAL
        return serverCurrentTime;
    }

    public void updateCurrentTime() {
        serverCurrentTime = currentTime.addAndGet(CommonConfig.config.server.commonRefresh);
    }

    public long forceUpdateCurrentTime() {
        long timeNow = System.currentTimeMillis();
        serverCurrentTime = timeNow;
        currentTime.set(timeNow);

        return timeNow;
    }

    public boolean isOnline() {
        return online;
    }

    public List<Pair<Integer, String>> worldRecommendedList() {
        return worldRecommendedList;
    }

    public void setNewYearCard(NewYearCardRecord nyc) {
        newyears.put(nyc.getId(), nyc);
    }

    public NewYearCardRecord getNewYearCard(int cardid) {
        return newyears.get(cardid);
    }

    public NewYearCardRecord removeNewYearCard(int cardid) {
        return newyears.remove(cardid);
    }

    public void setAvailableDeveloperRoom() {
        availableDeveloperRoom = true;
    }

    public boolean canEnterDeveloperRoom() {
        return availableDeveloperRoom;
    }

    private void loadPlayerNpcMapStepFromDb() {
        try {
            List<World> wlist = this.getWorlds();

            Connection con = MysqlConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM playernpcs_field");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int world = rs.getInt("world"), map = rs.getInt("map"), step = rs.getInt("step"), podium = rs.getInt("podium");

                World w = wlist.get(world);
                if (w != null) w.setPlayerNpcMapData(map, step, podium);
            }

            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public World getWorld(int id) {
        wldRLock.lock();
        try {
            try {
                return worlds.get(id);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        } finally {
            wldRLock.unlock();
        }
    }

    public List<World> getWorlds() {
        wldRLock.lock();
        try {
            return Collections.unmodifiableList(worlds);
        } finally {
            wldRLock.unlock();
        }
    }

    public int getWorldsSize() {
        wldRLock.lock();
        try {
            return worlds.size();
        } finally {
            wldRLock.unlock();
        }
    }

    public Channel getChannel(int world, int channel) {
        try {
            return this.getWorld(world).getChannel(channel);
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public List<Channel> getChannelsFromWorld(int world) {
        try {
            return this.getWorld(world).getChannels();
        } catch (NullPointerException npe) {
            return new ArrayList<>(0);
        }
    }

    public List<Channel> getAllChannels() {
        try {
            List<Channel> channelz = new ArrayList<>();
            for (World world : this.getWorlds()) {
                channelz.addAll(world.getChannels());
            }
            return channelz;
        } catch (NullPointerException npe) {
            return new ArrayList<>(0);
        }
    }

    public Set<Integer> getOpenChannels(int world) {
        wldRLock.lock();
        try {
            return new HashSet<>(channels.get(world).keySet());
        } finally {
            wldRLock.unlock();
        }
    }

    private String getIP(int world, int channel) {
        wldRLock.lock();
        try {
            return channels.get(world).get(channel);
        } finally {
            wldRLock.unlock();
        }
    }

    public String[] getInetSocket(int world, int channel) {
        try {
            return getIP(world, channel).split(":");
        } catch (Exception e) {
            return null;
        }
    }


    private void dumpData() {
        wldRLock.lock();
        try {
            System.out.println(worlds);
            System.out.println(channels);
            System.out.println(worldRecommendedList);
            System.out.println();
            System.out.println("---------------------");
        } finally {
            wldRLock.unlock();
        }
    }

    public int addChannel(int worldid) {
        World world;
        Map<Integer, String> channelInfo;
        int channelid;

        wldRLock.lock();
        try {
            if (worldid >= worlds.size()) {
                return -3;
            }

            channelInfo = channels.get(worldid);
            if (channelInfo == null) {
                return -3;
            }

            channelid = channelInfo.size();
            channelid++;
            world = this.getWorld(worldid);
        } finally {
            wldRLock.unlock();
        }

        Channel channel = new Channel(worldid, channelid, getCurrentTime());
        channel.setServerMessage(CommonConfig.config.worlds.get(worldid).recommendedMessage);

        if (world.addChannel(channel)) {
            wldWLock.lock();
            try {
                channelInfo.put(channelid, channel.getIP());
            } finally {
                wldWLock.unlock();
            }
        }

        return channelid;
    }

    public int addWorld() {
        int newWorld = initWorld();
        if (newWorld > -1) {
            installWorldPlayerRanking(newWorld);

            Set<Integer> accounts;
            lgnRLock.lock();
            try {
                accounts = new HashSet<>(accountChars.keySet());
            } finally {
                lgnRLock.unlock();
            }

            for (Integer accId : accounts) {
                loadAccountCharactersView(accId, 0, newWorld);
            }
        }

        return newWorld;
    }

    private int initWorld() {
        int i = worlds.size();

        wldRLock.lock();
        try {
            if (i > CommonConfig.config.worlds.size()) {
                return -1;
            }
        } finally {
            wldRLock.unlock();
        }
        WorldConfig worldConfig = CommonConfig.config.worlds.get(i);
        wse.sendMessageAll("正在启动大区 " + worldConfig.worldId);
        System.out.println("正在启动大区 " + worldConfig.worldId);

        int exprate = worldConfig.expRate;
        int mesorate = worldConfig.mesoRate;
        int droprate = worldConfig.dropRate;
        int bossdroprate = worldConfig.bossDropRate;
        int questrate = worldConfig.questRate;
        int travelrate = worldConfig.travelRate;
        int fishingrate = worldConfig.fishingRate;

        int flag = worldConfig.worldId;
        String event_message = worldConfig.eventMessage;
        String why_am_i_recommended = worldConfig.recommendedMessage;

        World world = new World(i,
                flag,
                event_message,
                exprate, droprate, bossdroprate, mesorate, questrate, travelrate, fishingrate);

        Map<Integer, String> channelInfo = new HashMap<>();
        long bootTime = getCurrentTime();
        String[] channelIds = worldConfig.channelIds.split(NapPunc.COMMA);
        for (String id : channelIds) {
            int channelId = Integer.parseInt(id.trim());
            Channel channel = new Channel(i, channelId, bootTime);

            world.addChannel(channel);
            channelInfo.put(channelId, channel.getIP());
        }

        boolean canDeploy;

        wldWLock.lock();    // thanks Ashen for noticing a deadlock issue when trying to deploy a channel
        try {
            canDeploy = world.getId() == worlds.size();
            if (canDeploy) {
                worldRecommendedList.add(new Pair<>(i, why_am_i_recommended));
                worlds.add(world);
                channels.add(i, channelInfo);
            }
        } finally {
            wldWLock.unlock();
        }

        if (canDeploy) {
            world.setServerMessage(worldConfig.serverMessage);

            wse.sendMessageAll("大区 " + worldConfig.worldId + " 加载完毕\r\n");
            System.out.println("大区 " + worldConfig.worldId + " 加载完毕\r\n");
            return i;
        } else {
            System.out.println("加载大区 " + worldConfig.worldId + " 失败...\r\n");
            world.shutdown();
            return -2;
        }
    }

    public boolean removeChannel(int worldid) {   //lol don't!
        World world;

        wldRLock.lock();
        try {
            if (worldid >= worlds.size()) return false;
            world = worlds.get(worldid);
        } finally {
            wldRLock.unlock();
        }

        if (world != null) {
            int channel = world.removeChannel();
            wldWLock.lock();
            try {
                Map<Integer, String> m = channels.get(worldid);
                if (m != null) m.remove(channel);
            } finally {
                wldWLock.unlock();
            }

            return channel > -1;
        }

        return false;
    }

    public boolean removeWorld() {   //lol don't!
        World w;
        int worldid;

        wldRLock.lock();
        try {
            worldid = worlds.size() - 1;
            if (worldid < 0) {
                return false;
            }

            w = worlds.get(worldid);
        } finally {
            wldRLock.unlock();
        }

        if (w == null || !w.canUninstall()) {
            return false;
        }

        removeWorldPlayerRanking();
        w.shutdown();

        wldWLock.lock();
        try {
            if (worldid == worlds.size() - 1) {
                worlds.remove(worldid);
                channels.remove(worldid);
                worldRecommendedList.remove(worldid);
            }
        } finally {
            wldWLock.unlock();
        }

        return true;
    }

    private void resetServerWorlds() {  // thanks maple006 for noticing proprietary lists assigned to null
        wldWLock.lock();
        try {
            worlds.clear();
            channels.clear();
            worldRecommendedList.clear();
        } finally {
            wldWLock.unlock();
        }
    }

    private static long getTimeLeftForNextHour() {
        Calendar nextHour = Calendar.getInstance();
        nextHour.add(Calendar.HOUR, 1);
        nextHour.set(Calendar.MINUTE, 0);
        nextHour.set(Calendar.SECOND, 0);

        return Math.max(0, nextHour.getTimeInMillis() - System.currentTimeMillis());
    }

    public static long getTimeLeftForNextDay() {
        Calendar nextDay = Calendar.getInstance();
        nextDay.add(Calendar.DAY_OF_MONTH, 1);
        nextDay.set(Calendar.HOUR_OF_DAY, 0);
        nextDay.set(Calendar.MINUTE, 0);
        nextDay.set(Calendar.SECOND, 0);

        return Math.max(0, nextDay.getTimeInMillis() - System.currentTimeMillis());
    }

    public Map<Integer, Integer> getCouponRates() {
        return couponRates;
    }

    public void updateDatabase() {
        try {
            // 检查当前数据库版本
            List<Map<String, Object>> showTableResult = MysqlConnection.select("show tables like 'db_update_log'");
            // 表不存在则建表
            if (NapComUtils.isEmpty(showTableResult)) {
                SqlOperator.execute(MysqlConnection.SOURCE_MYSQL, "sql/db_update_log.sql");
                showTableResult = MysqlConnection.select("show tables like 'db_update_log'");
                // 创建失败直接跳过
                if (NapComUtils.isEmpty(showTableResult)) {
                    return;
                }
            }
            System.out.println("正在检查数据库更新");
            long start = System.currentTimeMillis();
            // 查询待更新列表
            File updateDir = new File(ServerConstants.RESOURCE_DIR + "update/");
            // 没有更新
            if (!updateDir.exists()) {
                System.out.println("没有发现数据库更新");
                return;
            }
            File[] listFiles = updateDir.listFiles();
            if (null == listFiles || listFiles.length == 0) {
                System.out.println("没有发现数据库更新");
                return;
            }
            // 查询已更新列表
            List<Map<String, Object>> updateList = MysqlConnection.select("select * from db_update_log");
            List<String> fileNameList = updateList.stream()
                    .map(map -> NapMapUtils.getString(map, "file_name"))
                    .filter(NapComUtils::isNotEmpty)
                    .distinct()
                    .collect(Collectors.toList());
            for (File listFile : listFiles) {
                String fileName = listFile.getName();
                // 文件是否满足命名规范，详见resources/update/ReadMe.txt
                if ("ReadMe.txt".equals(fileName)) {
                    continue;
                }
                if (!fileName.matches("^\\d+\\.\\d{2}\\.\\d{4}-.*\\.sql$")) {
                    Log.warn("文件" + fileName + "命名不规范，跳过该更新");
                    continue;
                }
                if (fileNameList.contains(fileName)) {
                    continue;
                }
                int lineIndex = fileName.indexOf(NapPunc.LINE);
                if (lineIndex <= 0) {
                    continue;
                }
                String sufFileName = fileName.substring(0, lineIndex);
                String[] fileVersion = sufFileName.split(NapPunc.REGULAR_DOT);
                if (fileVersion.length != 3) {
                    continue;
                }
                String[] currVersion = ServerConstants.BUILD_VERSION.split(NapPunc.REGULAR_DOT);
                // 大版本是否匹配
                if (!fileVersion[0].equals(currVersion[0])) {
                    continue;
                }
                // 小版本是否低于程序版本
                if (Integer.parseInt(fileVersion[1] + fileVersion[2]) > Integer.parseInt(currVersion[1] + currVersion[2])) {
                    continue;
                }
                // 综上，更新的脚本最高也不会超过当前程序的版本，也就是可以预留后续版本的脚本，但是不执行
                System.out.println("发现更新：" + fileName + "，正在执行更新");
                if (SqlOperator.execute(MysqlConnection.SOURCE_MYSQL, listFile.getAbsolutePath())) {
                    MysqlConnection.insert("insert into db_update_log(version, file_name, update_time) values (?, ?, ?)",
                            sufFileName, fileName, new Date());
                }
            }
            System.out.println("数据库更新完毕，耗时 " + StringUtil.timeTake(start) + " 秒");
        } catch (Exception e) {
            System.out.println("数据库升级的过程出现异常");
        }
    }

    private void initAccount() {
        MysqlConnection.update("UPDATE accounts SET loggedin = 0");
        MysqlConnection.update("UPDATE characters SET HasMerchant = 0");
    }

    private void cleanNxCodeCoupons() {
        if (!CommonConfig.config.server.useClearOutdatedCoupons) {
            return;
        }

        long timeClear = System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000;
        List<Map<String, Object>> nxCodeList = MysqlConnection.select("SELECT id FROM nxcode WHERE expiration <= ?", timeClear);
        if (NapComUtils.isEmpty(nxCodeList)) {
            return;
        }
        Object[][] params = new Object[nxCodeList.size()][1];
        for (int i = 0; i < nxCodeList.size(); i++) {
            params[i] = new Object[]{nxCodeList.get(i).get("id")};
        }
        MysqlConnection.batchDelete("DELETE FROM nxcode_items WHERE codeid = ?", params);
        MysqlConnection.delete("DELETE FROM nxcode WHERE expiration <= ?", timeClear);
    }

    private void loadCouponRates() {
        List<Map<String, Object>> selectList = MysqlConnection.select("SELECT couponid, rate FROM nxcoupons");
        selectList.forEach(map -> couponRates.put(NapMapUtils.getInteger(map, "couponid"), NapMapUtils.getInteger(map, "rate")));
    }

    public List<Integer> getActiveCoupons() {
        synchronized (activeCoupons) {
            return activeCoupons;
        }
    }

    public void commitActiveCoupons() {
        for (World world : getWorlds()) {
            for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
                if (!chr.isLoggedin()) continue;

                chr.updateCouponRates();
            }
        }
    }

    public void toggleCoupon(Integer couponId) {
        if (ItemConstants.isRateCoupon(couponId)) {
            synchronized (activeCoupons) {
                if (activeCoupons.contains(couponId)) {
                    activeCoupons.remove(couponId);
                } else {
                    activeCoupons.add(couponId);
                }

                commitActiveCoupons();
            }
        }
    }

    public void updateActiveCoupons() {
        synchronized (activeCoupons) {
            activeCoupons.clear();
            Calendar c = Calendar.getInstance();
            int weekDay = c.get(Calendar.DAY_OF_WEEK);
            int hourDay = c.get(Calendar.HOUR_OF_DAY);
            int weekdayMask = (1 << weekDay);
            List<Map<String, Object>> selectList = MysqlConnection.select(
                    "SELECT couponid FROM nxcoupons WHERE (activeday & ?) = ? AND starthour <= ? AND endhour > ?",
                    weekdayMask, weekdayMask, hourDay, hourDay);
            selectList.forEach(map -> activeCoupons.add(NapMapUtils.getInteger(map, "couponid")));
        }
    }

    public void runAnnouncePlayerDiseasesSchedule() {
        List<MapleClient> processDiseaseAnnounceClients;
        disLock.lock();
        try {
            processDiseaseAnnounceClients = new LinkedList<>(processDiseaseAnnouncePlayers);
            processDiseaseAnnouncePlayers.clear();
        } finally {
            disLock.unlock();
        }

        while (!processDiseaseAnnounceClients.isEmpty()) {
            MapleClient c = processDiseaseAnnounceClients.remove(0);
            MapleCharacter player = c.getPlayer();
            if (player != null && player.isLoggedinWorld()) {
                player.announceDiseases();
                player.collectDiseases();
            }
        }

        disLock.lock();
        try {
            // this is to force the system to wait for at least one complete tick before releasing disease info for the registered clients
            while (!registeredDiseaseAnnouncePlayers.isEmpty()) {
                MapleClient c = registeredDiseaseAnnouncePlayers.remove(0);
                processDiseaseAnnouncePlayers.add(c);
            }
        } finally {
            disLock.unlock();
        }
    }

    public void registerAnnouncePlayerDiseases(MapleClient c) {
        disLock.lock();
        try {
            registeredDiseaseAnnouncePlayers.add(c);
        } finally {
            disLock.unlock();
        }
    }

    public List<Pair<String, Integer>> getWorldPlayerRanking(int worldid) {
        wldRLock.lock();
        try {
            return new ArrayList<>(playerRanking.get(!CommonConfig.config.server.useWholeServerRanking ? worldid : 0));
        } finally {
            wldRLock.unlock();
        }
    }

    private void installWorldPlayerRanking(int worldid) {
        List<Pair<Integer, List<Pair<String, Integer>>>> ranking = updatePlayerRankingFromDB(worldid);
        if (!ranking.isEmpty()) {
            wldWLock.lock();
            try {
                if (!CommonConfig.config.server.useWholeServerRanking) {
                    for (int i = playerRanking.size(); i <= worldid; i++) {
                        playerRanking.add(new ArrayList<Pair<String, Integer>>(0));
                    }

                    playerRanking.add(worldid, ranking.get(0).getRight());
                } else {
                    playerRanking.add(0, ranking.get(0).getRight());
                }
            } finally {
                wldWLock.unlock();
            }
        }
    }

    private void removeWorldPlayerRanking() {
        if (!CommonConfig.config.server.useWholeServerRanking) {
            wldWLock.lock();
            try {
                if (playerRanking.size() < worlds.size()) {
                    return;
                }

                playerRanking.remove(playerRanking.size() - 1);
            } finally {
                wldWLock.unlock();
            }
        } else {
            List<Pair<Integer, List<Pair<String, Integer>>>> ranking = updatePlayerRankingFromDB(-1 * (this.getWorldsSize() - 2));  // update ranking list

            wldWLock.lock();
            try {
                playerRanking.add(0, ranking.get(0).getRight());
            } finally {
                wldWLock.unlock();
            }
        }
    }

    public void updateWorldPlayerRanking() {
        List<Pair<Integer, List<Pair<String, Integer>>>> rankUpdates = updatePlayerRankingFromDB(-1 * (this.getWorldsSize() - 1));
        if (!rankUpdates.isEmpty()) {
            wldWLock.lock();
            try {
                if (!CommonConfig.config.server.useWholeServerRanking) {
                    for (int i = playerRanking.size(); i <= rankUpdates.get(rankUpdates.size() - 1).getLeft(); i++) {
                        playerRanking.add(new ArrayList<Pair<String, Integer>>(0));
                    }

                    for (Pair<Integer, List<Pair<String, Integer>>> wranks : rankUpdates) {
                        playerRanking.set(wranks.getLeft(), wranks.getRight());
                    }
                } else {
                    playerRanking.set(0, rankUpdates.get(0).getRight());
                }
            } finally {
                wldWLock.unlock();
            }
        }
    }

    private void initWorldPlayerRanking() {
        if (CommonConfig.config.server.useWholeServerRanking) {
            playerRanking.add(new ArrayList<Pair<String, Integer>>(0));
        }
        updateWorldPlayerRanking();
    }

    private static List<Pair<Integer, List<Pair<String, Integer>>>> updatePlayerRankingFromDB(int worldid) {
        List<Pair<Integer, List<Pair<String, Integer>>>> rankSystem = new ArrayList<>();
        List<Pair<String, Integer>> rankUpdate = new ArrayList<>(0);

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = MysqlConnection.getConnection();

            String worldQuery;
            if (!CommonConfig.config.server.useWholeServerRanking) {
                if (worldid >= 0) {
                    worldQuery = (" AND `characters`.`world` = " + worldid);
                } else {
                    worldQuery = (" AND `characters`.`world` >= 0 AND `characters`.`world` <= " + -worldid);
                }
            } else {
                worldQuery = (" AND `characters`.`world` >= 0 AND `characters`.`world` <= " + Math.abs(worldid));
            }

            ps = con.prepareStatement("SELECT `characters`.`name`, `characters`.`level`, `characters`.`world` FROM `characters` LEFT JOIN accounts ON accounts.id = characters.accountid WHERE `characters`.`gm` < 2 AND `accounts`.`banned` = '0'" + worldQuery + " ORDER BY " + (!CommonConfig.config.server.useWholeServerRanking ? "world, " : "") + "level DESC, exp DESC, lastExpGainTime ASC LIMIT 50");
            rs = ps.executeQuery();

            if (!CommonConfig.config.server.useWholeServerRanking) {
                int currentWorld = -1;
                while (rs.next()) {
                    int rsWorld = rs.getInt("world");
                    if (currentWorld < rsWorld) {
                        currentWorld = rsWorld;
                        rankUpdate = new ArrayList<>(50);
                        rankSystem.add(new Pair<>(rsWorld, rankUpdate));
                    }

                    rankUpdate.add(new Pair<>(rs.getString("name"), rs.getInt("level")));
                }
            } else {
                rankUpdate = new ArrayList<>(50);
                rankSystem.add(new Pair<>(0, rankUpdate));

                while (rs.next()) {
                    rankUpdate.add(new Pair<>(rs.getString("name"), rs.getInt("level")));
                }
            }

            ps.close();
            rs.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rankSystem;
    }

    private static WsServerEndpoint wse = (WsServerEndpoint) ApplicationContextUtil.getBean("wsServerEndpoint");

    public synchronized void init() {
        if (isOnline()) {
            return;
        }

        try {
            wse.sendMessageTo("1", "HeavenMS v" + ServerConstants.VERSION + " 正在启动中...\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("HeavenMS v" + ServerConstants.VERSION + " 正在启动中...\r\n");
        TimeZone.setDefault(TimeZone.getTimeZone(CommonConfig.config.server.timezone));

        // 初始化数据库连接
        MysqlConnection.init();
        // 检查数据库更新
//        updateDatabase();
        long timeToTake = System.currentTimeMillis();
        // 初始化账户信息
        initAccount();
        // 清空过期点券
        cleanNxCodeCoupons();
        // 载入点券比例
        loadCouponRates();
        // 载入未失效点券
        updateActiveCoupons();
        // 接受所有改名，关联参数INSTANT_NAME_CHANGE
        applyAllNameChanges();
        // 接受转区
        applyAllWorldTransfers();
        //MaplePet.clearMissingPetsFromDb();    // thanks Optimist for noticing this taking too long to run
        // 加载最大的现金id
        MapleCashidGenerator.loadExistentCashIdsFromDb();
        wse.sendMessageAll("数据  加载耗时 " + StringUtil.timeTake(timeToTake) + " 秒");
        System.out.println("数据  加载耗时 " + StringUtil.timeTake(timeToTake) + " 秒");

        // 创建线程池
        ThreadManager.getInstance().start();
        // 创建定时任务线程池，并添加定时任务
        initializeTimelyTasks();    // aggregated method for timely tasks thanks to lxconan

        timeToTake = System.currentTimeMillis();
        SkillFactory.loadAllSkills();
        wse.sendMessageAll("技能  加载耗时 " + StringUtil.timeTake(timeToTake) + " 秒");
        System.out.println("技能  加载耗时 " + StringUtil.timeTake(timeToTake) + " 秒");

        timeToTake = System.currentTimeMillis();
        CashItemFactory.loadSpecialCashItems();
        wse.sendMessageAll("物品  加载耗时 " + StringUtil.timeTake(timeToTake) + " 秒");
        System.out.println("物品  加载耗时 " + StringUtil.timeTake(timeToTake) + " 秒");

        timeToTake = System.currentTimeMillis();
        MapleQuest.loadAllQuest();
        wse.sendMessageAll("任务  加载耗时 " + StringUtil.timeTake(timeToTake) + " 秒\r\n");
        System.out.println("任务  加载耗时 " + StringUtil.timeTake(timeToTake) + " 秒\r\n");

        // 发送新年贺卡通知
        NewYearCardRecord.startPendingNewYearCardRequests();

        if (CommonConfig.config.server.useThreadTracker) ThreadTracker.getInstance().registerThreadTrackerTask();

        try {
            int worldCount = Math.min(GameConstants.WORLD_NAMES.length, CommonConfig.config.worlds.size());

            for (int i = 0; i < worldCount; i++) {
                initWorld();
            }
            initWorldPlayerRanking();

            MaplePlayerNPCFactory.loadFactoryMetadata();
            loadPlayerNpcMapStepFromDb();
        } catch (Exception e) {
            System.out.println("[SEVERE] world配置错误： " + e.getMessage());
            System.exit(0);
        }

        if (CommonConfig.config.server.useFamilySystem) {
            timeToTake = System.currentTimeMillis();
            MapleFamily.loadAllFamilies();
            wse.sendMessageAll("家族  加载耗时 " + StringUtil.timeTake(timeToTake) + " 秒\r\n");
            System.out.println("家族  加载耗时 " + StringUtil.timeTake(timeToTake) + " 秒\r\n");
        }

        IoBuffer.setUseDirectBuffer(false);     // join IO operations performed by lxconan
        IoBuffer.setAllocator(new SimpleBufferAllocator());
        acceptor = new NioSocketAcceptor();
        // 定义加密器和解密器
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
        // 定义空闲连接时间
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
        // 定义处理器
        acceptor.setHandler(new MapleServerHandler());
        try {
            acceptor.bind(new InetSocketAddress(8484));
        } catch (IOException e) {
            System.out.println("无法绑定端口 8484" + e.getMessage());
        }

        wse.sendMessageAll("客户端登录端口 8484");
        wse.sendMessageAll("HeavenMS v" + ServerConstants.VERSION + " 启动完毕！");
        System.out.println("客户端登录端口 8484");
        System.out.println("HeavenMS v" + ServerConstants.VERSION + " 启动完毕！");
//        System.out.println("发布版本: " + ServerConstants.BUILD_VERSION);
        online = true;

        OpcodeConstants.generateOpcodeNames();

        ThreadManager.getInstance().newTask(() -> {
            wse.sendMessageAll("后台初始化事件中...");
            System.out.println("后台初始化事件中...");
            for (Channel ch : this.getAllChannels()) {
                ch.reloadEventScriptManager();
            }
            wse.sendMessageAll("事件初始化完成！");
            System.out.println("事件初始化完成！");
        });
    }

    private void initializeTimelyTasks() {
        TimerManager tMan = TimerManager.getInstance();
        tMan.start();
        // 强制更新时间，并清除已被取消的任务
        tMan.register(tMan.purge(), CommonConfig.config.server.timeTaskPurge);//Purging ftw...
        // 释放已经登录或长时间未登录的客户端
        disconnectIdlesOnLoginTask();

        long timeLeft = getTimeLeftForNextHour();
        tMan.register(new CharacterDiseaseTask(), CommonConfig.config.server.commonRefresh, CommonConfig.config.server.commonRefresh);
        tMan.register(new ReleaseLockTask(), 2 * 60 * 1000, 2 * 60 * 1000);
        tMan.register(new CouponTask(), CommonConfig.config.server.couponRefresh, timeLeft);
        tMan.register(new RankingCommandTask(), 5 * 60 * 1000, 5 * 60 * 1000);
        tMan.register(new RankingLoginTask(), CommonConfig.config.server.rankRefresh, timeLeft);
        tMan.register(new LoginCoordinatorTask(), 60 * 60 * 1000, timeLeft);
        tMan.register(new EventRecallCoordinatorTask(), 60 * 60 * 1000, timeLeft);
        tMan.register(new LoginStorageTask(), 2 * 60 * 1000, 2 * 60 * 1000);
        tMan.register(new DueyFredrickTask(), 60 * 60 * 1000, timeLeft);
        tMan.register(new InvitationTask(), 30 * 1000, 30 * 1000);
        tMan.register(new RespawnTask(), CommonConfig.config.server.monsterRespawn, CommonConfig.config.server.monsterRespawn);

        timeLeft = getTimeLeftForNextDay();
        MapleExpeditionBossLog.resetBossLogTable();
        tMan.register(new BossLogTask(), 24 * 60 * 60 * 1000, timeLeft);
    }

    public static void main(String[] args) {
        Server server = Server.getInstance();
        server.init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdownInternal(false);
        }));
    }

    public Properties getSubnetInfo() {
        return subnetInfo;
    }

    public MapleAlliance getAlliance(int id) {
        synchronized (alliances) {
            if (alliances.containsKey(id)) {
                return alliances.get(id);
            }
            return null;
        }
    }

    public void addAlliance(int id, MapleAlliance alliance) {
        synchronized (alliances) {
            if (!alliances.containsKey(id)) {
                alliances.put(id, alliance);
            }
        }
    }

    public void disbandAlliance(int id) {
        synchronized (alliances) {
            MapleAlliance alliance = alliances.get(id);
            if (alliance != null) {
                for (Integer gid : alliance.getGuilds()) {
                    guilds.get(gid).setAllianceId(0);
                }
                alliances.remove(id);
            }
        }
    }

    public void allianceMessage(int id, final byte[] packet, int exception, int guildex) {
        MapleAlliance alliance = alliances.get(id);
        if (alliance != null) {
            for (Integer gid : alliance.getGuilds()) {
                if (guildex == gid) {
                    continue;
                }
                MapleGuild guild = guilds.get(gid);
                if (guild != null) {
                    guild.broadcast(packet, exception);
                }
            }
        }
    }

    public boolean addGuildtoAlliance(int aId, int guildId) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.addGuild(guildId);
            guilds.get(guildId).setAllianceId(aId);
            return true;
        }
        return false;
    }

    public boolean removeGuildFromAlliance(int aId, int guildId) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.removeGuild(guildId);
            guilds.get(guildId).setAllianceId(0);
            return true;
        }
        return false;
    }

    public boolean setAllianceRanks(int aId, String[] ranks) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.setRankTitle(ranks);
            return true;
        }
        return false;
    }

    public boolean setAllianceNotice(int aId, String notice) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.setNotice(notice);
            return true;
        }
        return false;
    }

    public boolean increaseAllianceCapacity(int aId, int inc) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.increaseCapacity(inc);
            return true;
        }
        return false;
    }

    public int createGuild(int leaderId, String name) {
        return MapleGuild.createGuild(leaderId, name);
    }

    public MapleGuild getGuildByName(String name) {
        synchronized (guilds) {
            for (MapleGuild mg : guilds.values()) {
                if (mg.getName().equalsIgnoreCase(name)) {
                    return mg;
                }
            }

            return null;
        }
    }

    public MapleGuild getGuild(int id) {
        synchronized (guilds) {
            if (guilds.get(id) != null) {
                return guilds.get(id);
            }

            return null;
        }
    }

    public MapleGuild getGuild(int id, int world) {
        return getGuild(id, world, null);
    }

    public MapleGuild getGuild(int id, int world, MapleCharacter mc) {
        synchronized (guilds) {
            MapleGuild g = guilds.get(id);
            if (g != null) {
                return g;
            }

            g = new MapleGuild(id, world);
            if (g.getId() == -1) {
                return null;
            }

            if (mc != null) {
                MapleGuildCharacter mgc = g.getMGC(mc.getId());
                if (mgc != null) {
                    mc.setMGC(mgc);
                    mgc.setCharacter(mc);
                } else {
                    FilePrinter.printError(FilePrinter.GUILD_CHAR_ERROR, "Could not find " + mc.getName() + " when loading guild " + id + ".");
                }

                g.setOnline(mc.getId(), true, mc.getClient().getChannel());
            }

            guilds.put(id, g);
            return g;
        }
    }

    public void setGuildMemberOnline(MapleCharacter mc, boolean bOnline, int channel) {
        MapleGuild g = getGuild(mc.getGuildId(), mc.getWorld(), mc);
        g.setOnline(mc.getId(), bOnline, channel);
    }

    public int addGuildMember(MapleGuildCharacter mgc, MapleCharacter chr) {
        MapleGuild g = guilds.get(mgc.getGuildId());
        if (g != null) {
            return g.addGuildMember(mgc, chr);
        }
        return 0;
    }

    public boolean setGuildAllianceId(int gId, int aId) {
        MapleGuild guild = guilds.get(gId);
        if (guild != null) {
            guild.setAllianceId(aId);
            return true;
        }
        return false;
    }

    public void resetAllianceGuildPlayersRank(int gId) {
        guilds.get(gId).resetAllianceGuildPlayersRank();
    }

    public void leaveGuild(MapleGuildCharacter mgc) {
        MapleGuild g = guilds.get(mgc.getGuildId());
        if (g != null) {
            g.leaveGuild(mgc);
        }
    }

    public void guildChat(int gid, String name, int cid, String msg) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.guildChat(name, cid, msg);
        }
    }

    public void changeRank(int gid, int cid, int newRank) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.changeRank(cid, newRank);
        }
    }

    public void expelMember(MapleGuildCharacter initiator, String name, int cid) {
        MapleGuild g = guilds.get(initiator.getGuildId());
        if (g != null) {
            g.expelMember(initiator, name, cid);
        }
    }

    public void setGuildNotice(int gid, String notice) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.setGuildNotice(notice);
        }
    }

    public void memberLevelJobUpdate(MapleGuildCharacter mgc) {
        MapleGuild g = guilds.get(mgc.getGuildId());
        if (g != null) {
            g.memberLevelJobUpdate(mgc);
        }
    }

    public void changeRankTitle(int gid, String[] ranks) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.changeRankTitle(ranks);
        }
    }

    public void setGuildEmblem(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.setGuildEmblem(bg, bgcolor, logo, logocolor);
        }
    }

    public void disbandGuild(int gid) {
        synchronized (guilds) {
            MapleGuild g = guilds.get(gid);
            g.disbandGuild();
            guilds.remove(gid);
        }
    }

    public boolean increaseGuildCapacity(int gid) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            return g.increaseCapacity();
        }
        return false;
    }

    public void gainGP(int gid, int amount) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.gainGP(amount);
        }
    }

    public void guildMessage(int gid, byte[] packet) {
        guildMessage(gid, packet, -1);
    }

    public void guildMessage(int gid, byte[] packet, int exception) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.broadcast(packet, exception);
        }
    }

    public PlayerBuffStorage getPlayerBuffStorage() {
        return buffStorage;
    }

    public void deleteGuildCharacter(MapleCharacter mc) {
        setGuildMemberOnline(mc, false, (byte) -1);
        if (mc.getMGC().getGuildRank() > 1) {
            leaveGuild(mc.getMGC());
        } else {
            disbandGuild(mc.getMGC().getGuildId());
        }
    }

    public void deleteGuildCharacter(MapleGuildCharacter mgc) {
        if (mgc.getCharacter() != null) setGuildMemberOnline(mgc.getCharacter(), false, (byte) -1);
        if (mgc.getGuildRank() > 1) {
            leaveGuild(mgc);
        } else {
            disbandGuild(mgc.getGuildId());
        }
    }

    public void reloadGuildCharacters(int world) {
        World worlda = getWorld(world);
        for (MapleCharacter mc : worlda.getPlayerStorage().getAllCharacters()) {
            if (mc.getGuildId() > 0) {
                setGuildMemberOnline(mc, true, worlda.getId());
                memberLevelJobUpdate(mc.getMGC());
            }
        }
        worlda.reloadGuildSummary();
    }

    public void broadcastMessage(int world, final byte[] packet) {
        for (Channel ch : getChannelsFromWorld(world)) {
            ch.broadcastPacket(packet);
        }
    }

    public void broadcastGMMessage(int world, final byte[] packet) {
        for (Channel ch : getChannelsFromWorld(world)) {
            ch.broadcastGMPacket(packet);
        }
    }

    public boolean isGmOnline(int world) {
        for (Channel ch : getChannelsFromWorld(world)) {
            for (MapleCharacter player : ch.getPlayerStorage().getAllCharacters()) {
                if (player.isGM()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void changeFly(Integer accountid, boolean canFly) {
        if (canFly) {
            activeFly.add(accountid);
        } else {
            activeFly.remove(accountid);
        }
    }

    public boolean canFly(Integer accountid) {
        return activeFly.contains(accountid);
    }

    public int getCharacterWorld(Integer chrid) {
        lgnRLock.lock();
        try {
            Integer worldid = worldChars.get(chrid);
            return worldid != null ? worldid : -1;
        } finally {
            lgnRLock.unlock();
        }
    }

    public boolean haveCharacterEntry(Integer accountid, Integer chrid) {
        lgnRLock.lock();
        try {
            Set<Integer> accChars = accountChars.get(accountid);
            return accChars.contains(chrid);
        } finally {
            lgnRLock.unlock();
        }
    }

    public short getAccountCharacterCount(Integer accountid) {
        lgnRLock.lock();
        try {
            return accountCharacterCount.get(accountid);
        } finally {
            lgnRLock.unlock();
        }
    }

    public short getAccountWorldCharacterCount(Integer accountid, Integer worldid) {
        lgnRLock.lock();
        try {
            short count = 0;

            for (Integer chr : accountChars.get(accountid)) {
                if (worldChars.get(chr).equals(worldid)) {
                    count++;
                }
            }

            return count;
        } finally {
            lgnRLock.unlock();
        }
    }

    private Set<Integer> getAccountCharacterEntries(Integer accountid) {
        lgnRLock.lock();
        try {
            return new HashSet<>(accountChars.get(accountid));
        } finally {
            lgnRLock.unlock();
        }
    }

    public void updateCharacterEntry(MapleCharacter chr) {
        MapleCharacter chrView = chr.generateCharacterEntry();

        lgnWLock.lock();
        try {
            World wserv = this.getWorld(chrView.getWorld());
            if (wserv != null) wserv.registerAccountCharacterView(chrView.getAccountID(), chrView);
        } finally {
            lgnWLock.unlock();
        }
    }

    public void createCharacterEntry(MapleCharacter chr) {
        Integer accountid = chr.getAccountID(), chrid = chr.getId(), world = chr.getWorld();

        lgnWLock.lock();
        try {
            accountCharacterCount.put(accountid, (short) (accountCharacterCount.get(accountid) + 1));

            Set<Integer> accChars = accountChars.get(accountid);
            accChars.add(chrid);

            worldChars.put(chrid, world);

            MapleCharacter chrView = chr.generateCharacterEntry();

            World wserv = this.getWorld(chrView.getWorld());
            if (wserv != null) wserv.registerAccountCharacterView(chrView.getAccountID(), chrView);
        } finally {
            lgnWLock.unlock();
        }
    }

    public void deleteCharacterEntry(Integer accountid, Integer chrid) {
        lgnWLock.lock();
        try {
            accountCharacterCount.put(accountid, (short) (accountCharacterCount.get(accountid) - 1));

            Set<Integer> accChars = accountChars.get(accountid);
            accChars.remove(chrid);

            Integer world = worldChars.remove(chrid);
            if (world != null) {
                World wserv = this.getWorld(world);
                if (wserv != null) wserv.unregisterAccountCharacterView(accountid, chrid);
            }
        } finally {
            lgnWLock.unlock();
        }
    }

    public void transferWorldCharacterEntry(MapleCharacter chr, Integer toWorld) { // used before setting the new worldid on the character object
        lgnWLock.lock();
        try {
            Integer chrid = chr.getId(), accountid = chr.getAccountID(), world = worldChars.get(chr.getId());
            if (world != null) {
                World wserv = this.getWorld(world);
                if (wserv != null) wserv.unregisterAccountCharacterView(accountid, chrid);
            }

            worldChars.put(chrid, toWorld);

            MapleCharacter chrView = chr.generateCharacterEntry();

            World wserv = this.getWorld(toWorld);
            if (wserv != null) wserv.registerAccountCharacterView(chrView.getAccountID(), chrView);
        } finally {
            lgnWLock.unlock();
        }
    }
    
    /*
    public void deleteAccountEntry(Integer accountid) { is this even a thing?
        lgnWLock.lock();
        try {
            accountCharacterCount.remove(accountid);
            accountChars.remove(accountid);
        } finally {
            lgnWLock.unlock();
        }
    
        for (World wserv : this.getWorlds()) {
            wserv.clearAccountCharacterView(accountid);
            wserv.unregisterAccountStorage(accountid);
        }
    }
    */

    public Pair<Pair<Integer, List<MapleCharacter>>, List<Pair<Integer, List<MapleCharacter>>>> loadAccountCharlist(Integer accountId, int visibleWorlds) {
        List<World> wlist = this.getWorlds();
        if (wlist.size() > visibleWorlds) wlist = wlist.subList(0, visibleWorlds);

        List<Pair<Integer, List<MapleCharacter>>> accChars = new ArrayList<>(wlist.size() + 1);
        int chrTotal = 0;
        List<MapleCharacter> lastwchars = null;

        lgnRLock.lock();
        try {
            for (World w : wlist) {
                List<MapleCharacter> wchars = w.getAccountCharactersView(accountId);
                if (wchars == null) {
                    if (!accountChars.containsKey(accountId)) {
                        accountCharacterCount.put(accountId, (short) 0);
                        accountChars.put(accountId, new HashSet<Integer>());    // not advisable at all to write on the map on a read-protected environment
                    }                                                           // yet it's known there's no problem since no other point in the source does
                } else if (!wchars.isEmpty()) {                                  // this action.
                    lastwchars = wchars;

                    accChars.add(new Pair<>(w.getId(), wchars));
                    chrTotal += wchars.size();
                }
            }
        } finally {
            lgnRLock.unlock();
        }

        return new Pair<>(new Pair<>(chrTotal, lastwchars), accChars);
    }

    private static Pair<Short, List<List<MapleCharacter>>> loadAccountCharactersViewFromDb(int accId, int wlen) {
        short characterCount = 0;
        List<List<MapleCharacter>> wchars = new ArrayList<>(wlen);
        for (int i = 0; i < wlen; i++) wchars.add(i, new LinkedList<MapleCharacter>());

        List<MapleCharacter> chars = new LinkedList<>();
        int curWorld = 0;
        try {
            List<Pair<Item, Integer>> accEquips = ItemFactory.loadEquippedItems(accId, true, true);
            Map<Integer, List<Item>> accPlayerEquips = new HashMap<>();

            for (Pair<Item, Integer> ae : accEquips) {
                List<Item> playerEquips = accPlayerEquips.get(ae.getRight());
                if (playerEquips == null) {
                    playerEquips = new LinkedList<>();
                    accPlayerEquips.put(ae.getRight(), playerEquips);
                }

                playerEquips.add(ae.getLeft());
            }

            Connection con = MysqlConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = ? ORDER BY world, id")) {
                ps.setInt(1, accId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        characterCount++;

                        int cworld = rs.getByte("world");
                        if (cworld >= wlen) continue;

                        if (cworld > curWorld) {
                            wchars.add(curWorld, chars);

                            curWorld = cworld;
                            chars = new LinkedList<>();
                        }

                        Integer cid = rs.getInt("id");
                        chars.add(MapleCharacter.loadCharacterEntryFromDB(rs, accPlayerEquips.get(cid)));
                    }
                }
            }
            con.close();

            wchars.add(curWorld, chars);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return new Pair<>(characterCount, wchars);
    }

    public void loadAllAccountsCharactersView() {
        try {
            Connection con = MysqlConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id FROM accounts");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int accountId = rs.getInt("id");
                if (isFirstAccountLogin(accountId)) {
                    loadAccountCharactersView(accountId, 0, 0);
                }
            }

            rs.close();
            ps.close();
            con.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private boolean isFirstAccountLogin(Integer accId) {
        lgnRLock.lock();
        try {
            return !accountChars.containsKey(accId);
        } finally {
            lgnRLock.unlock();
        }
    }

    private void applyAllNameChanges() {
        MysqlConnection.getConnectionAndClose(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM namechanges WHERE completionTime IS NULL")) {
                ResultSet rs = ps.executeQuery();
                List<Pair<String, String>> changedNames = new LinkedList<Pair<String, String>>(); //logging only
                while (rs.next()) {
                    conn.setAutoCommit(false);
                    int nameChangeId = rs.getInt("id");
                    int characterId = rs.getInt("characterId");
                    String oldName = rs.getString("old");
                    String newName = rs.getString("new");
                    boolean success = MapleCharacter.doNameChange(conn, characterId, oldName, newName, nameChangeId);
                    if (!success) conn.rollback(); //discard changes
                    else changedNames.add(new Pair<String, String>(oldName, newName));
                    conn.setAutoCommit(true);
                }
                //log
                for (Pair<String, String> namePair : changedNames) {
                    FilePrinter.print(FilePrinter.CHANGE_CHARACTER_NAME, "Name change applied : from \"" + namePair.getLeft() + "\" to \"" + namePair.getRight() + "\" at " + Calendar.getInstance().getTime().toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Failed to retrieve list of pending name changes.");
            }
        });
    }

    private void applyAllWorldTransfers() {
        MysqlConnection.getConnectionAndClose(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM worldtransfers WHERE completionTime IS NULL")) {
                ResultSet rs = ps.executeQuery();
                List<Integer> removedTransfers = new LinkedList<Integer>();
                while (rs.next()) {
                    int nameChangeId = rs.getInt("id");
                    int characterId = rs.getInt("characterId");
                    int oldWorld = rs.getInt("from");
                    int newWorld = rs.getInt("to");
                    String reason = MapleCharacter.checkWorldTransferEligibility(conn, characterId, oldWorld, newWorld); //check if character is still eligible
                    if (reason != null) {
                        removedTransfers.add(nameChangeId);
                        FilePrinter.print(FilePrinter.WORLD_TRANSFER, "World transfer cancelled : Character ID " + characterId + " at " + Calendar.getInstance().getTime().toString() + ", Reason : " + reason);
                        try (PreparedStatement delPs = conn.prepareStatement("DELETE FROM worldtransfers WHERE id = ?")) {
                            delPs.setInt(1, nameChangeId);
                            delPs.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            FilePrinter.printError(FilePrinter.WORLD_TRANSFER, e, "Failed to delete world transfer for character ID " + characterId);
                        }
                    }
                }
                rs.beforeFirst();
                List<Pair<Integer, Pair<Integer, Integer>>> worldTransfers = new LinkedList<Pair<Integer, Pair<Integer, Integer>>>(); //logging only <charid, <oldWorld, newWorld>>
                while (rs.next()) {
                    conn.setAutoCommit(false);
                    int nameChangeId = rs.getInt("id");
                    if (removedTransfers.contains(nameChangeId)) continue;
                    int characterId = rs.getInt("characterId");
                    int oldWorld = rs.getInt("from");
                    int newWorld = rs.getInt("to");
                    boolean success = MapleCharacter.doWorldTransfer(conn, characterId, oldWorld, newWorld, nameChangeId);
                    if (!success) conn.rollback();
                    else
                        worldTransfers.add(new Pair<Integer, Pair<Integer, Integer>>(characterId, new Pair<Integer, Integer>(oldWorld, newWorld)));
                    conn.setAutoCommit(true);
                }
                //log
                for (Pair<Integer, Pair<Integer, Integer>> worldTransferPair : worldTransfers) {
                    int charId = worldTransferPair.getLeft();
                    int oldWorld = worldTransferPair.getRight().getLeft();
                    int newWorld = worldTransferPair.getRight().getRight();
                    FilePrinter.print(FilePrinter.WORLD_TRANSFER, "World transfer applied : Character ID " + charId + " from World " + oldWorld + " to World " + newWorld + " at " + Calendar.getInstance().getTime().toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FilePrinter.printError(FilePrinter.WORLD_TRANSFER, e, "Failed to retrieve list of pending world transfers.");
            }
        });
    }

    public void loadAccountCharacters(MapleClient c) {
        Integer accId = c.getAccID();
        if (!isFirstAccountLogin(accId)) {
            Set<Integer> accWorlds = new HashSet<>();

            lgnRLock.lock();
            try {
                for (Integer chrid : getAccountCharacterEntries(accId)) {
                    accWorlds.add(worldChars.get(chrid));
                }
            } finally {
                lgnRLock.unlock();
            }

            int gmLevel = 0;
            for (Integer aw : accWorlds) {
                World wserv = this.getWorld(aw);

                if (wserv != null) {
                    for (MapleCharacter chr : wserv.getAllCharactersView()) {
                        if (gmLevel < chr.gmLevel()) gmLevel = chr.gmLevel();
                    }
                }
            }

            c.setGMLevel(gmLevel);
            return;
        }

        int gmLevel = loadAccountCharactersView(c.getAccID(), 0, 0);
        c.setGMLevel(gmLevel);
    }

    private int loadAccountCharactersView(Integer accId, int gmLevel, int fromWorldid) {    // returns the maximum gmLevel found
        List<World> wlist = this.getWorlds();
        Pair<Short, List<List<MapleCharacter>>> accCharacters = loadAccountCharactersViewFromDb(accId, wlist.size());

        lgnWLock.lock();
        try {
            List<List<MapleCharacter>> accChars = accCharacters.getRight();
            accountCharacterCount.put(accId, accCharacters.getLeft());

            Set<Integer> chars = accountChars.get(accId);
            if (chars == null) {
                chars = new HashSet<>(5);
            }

            for (int wid = fromWorldid; wid < wlist.size(); wid++) {
                World w = wlist.get(wid);
                List<MapleCharacter> wchars = accChars.get(wid);
                w.loadAccountCharactersView(accId, wchars);

                for (MapleCharacter chr : wchars) {
                    int cid = chr.getId();
                    if (gmLevel < chr.gmLevel()) gmLevel = chr.gmLevel();

                    chars.add(cid);
                    worldChars.put(cid, wid);
                }
            }

            accountChars.put(accId, chars);
        } finally {
            lgnWLock.unlock();
        }

        return gmLevel;
    }

    public void loadAccountStorages(MapleClient c) {
        int accountId = c.getAccID();
        Set<Integer> accWorlds = new HashSet<>();
        lgnWLock.lock();
        try {
            Set<Integer> chars = accountChars.get(accountId);

            for (Integer cid : chars) {
                Integer worldid = worldChars.get(cid);
                if (worldid != null) {
                    accWorlds.add(worldid);
                }
            }
        } finally {
            lgnWLock.unlock();
        }

        List<World> worldList = this.getWorlds();
        for (Integer worldid : accWorlds) {
            if (worldid < worldList.size()) {
                World wserv = worldList.get(worldid);
                wserv.loadAccountStorage(accountId);
            }
        }
    }

    private static String getRemoteHost(MapleClient client) {
        return MapleSessionCoordinator.getSessionRemoteHost(client.getSession());
    }

    public void setCharacteridInTransition(MapleClient client, int charId) {
        String remoteIp = getRemoteHost(client);

        lgnWLock.lock();
        try {
            transitioningChars.put(remoteIp, charId);
        } finally {
            lgnWLock.unlock();
        }
    }

    public boolean validateCharacteridInTransition(MapleClient client, int charId) {
        if (!CommonConfig.config.server.useIpValidation) {
            return true;
        }

        String remoteIp = getRemoteHost(client);

        lgnWLock.lock();
        try {
            Integer cid = transitioningChars.remove(remoteIp);
            return cid != null && cid.equals(charId);
        } finally {
            lgnWLock.unlock();
        }
    }

    public Integer freeCharacteridInTransition(MapleClient client) {
        if (!CommonConfig.config.server.useIpValidation) {
            return null;
        }

        String remoteIp = getRemoteHost(client);

        lgnWLock.lock();
        try {
            return transitioningChars.remove(remoteIp);
        } finally {
            lgnWLock.unlock();
        }
    }

    public boolean hasCharacteridInTransition(MapleClient client) {
        if (!CommonConfig.config.server.useIpValidation) {
            return true;
        }

        String remoteIp = getRemoteHost(client);

        lgnRLock.lock();
        try {
            return transitioningChars.containsKey(remoteIp);
        } finally {
            lgnRLock.unlock();
        }
    }

    public void registerLoginState(MapleClient c) {
        srvLock.lock();
        try {
            inLoginState.put(c, System.currentTimeMillis() + 600000);
        } finally {
            srvLock.unlock();
        }
    }

    public void unregisterLoginState(MapleClient c) {
        srvLock.lock();
        try {
            inLoginState.remove(c);
        } finally {
            srvLock.unlock();
        }
    }

    private void disconnectIdlesOnLoginState() {
        List<MapleClient> toDisconnect = new LinkedList<>();

        srvLock.lock();
        try {
            long timeNow = System.currentTimeMillis();

            for (Entry<MapleClient, Long> mc : inLoginState.entrySet()) {
                if (timeNow > mc.getValue()) {
                    toDisconnect.add(mc.getKey());
                }
            }

            for (MapleClient c : toDisconnect) {
                inLoginState.remove(c);
            }
        } finally {
            srvLock.unlock();
        }

        for (MapleClient c : toDisconnect) {    // thanks Lei for pointing a deadlock issue with srvLock
            if (c.isLoggedIn()) {
                c.disconnect(false, false);
            } else {
                MapleSessionCoordinator.getInstance().closeSession(c.getSession(), true);
            }
        }
    }

    private void disconnectIdlesOnLoginTask() {
        TimerManager.getInstance().register(this::disconnectIdlesOnLoginState, 300000);
    }

    public synchronized void shutdownInternal(boolean restart) {
        if (isOnline()) {
            wse.sendMessageAll("正在 " + (restart ? "重启" : "关闭") + " 服务！");
            System.out.println("正在 " + (restart ? "重启" : "关闭") + " 服务！");
            if (getWorlds() == null) return;//already shutdown
            for (World w : getWorlds()) {
                w.shutdown();
            }
            List<Channel> allChannels = getAllChannels();

            if (CommonConfig.config.server.useThreadTracker) ThreadTracker.getInstance().cancelThreadTrackerTask();

            for (Channel ch : allChannels) {
                while (!ch.finishedShutdown()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            resetServerWorlds();
            wse.sendMessageAll("所有大区和频道已关闭");
            System.out.println("所有大区和频道已关闭");

            wse.sendMessageAll("正在关闭后台任务...");
            System.out.println("正在关闭后台任务...");
            ThreadManager.getInstance().stop();
            TimerManager.getInstance().purge();
            TimerManager.getInstance().stop();
            wse.sendMessageAll("后台任务已关闭");
            System.out.println("后台任务已关闭");

            wse.sendMessageAll("正在关闭客户端连接");
            System.out.println("正在关闭客户端连接");
            acceptor.unbind();
            acceptor = null;
            // 关闭连接池中的数据库连接
            MysqlConnection.closeConnection();
            SqliteConnection.closeConnection();
            wse.sendMessageAll("客户端连接已关闭");
            System.out.println("客户端连接已关闭");

            // 客户端断开连接后做，避免产生不必要的报错
            MapleMonsterInformationProvider.getInstance().close();
            // 重启再登录报空指针的问题
            accountChars.clear();

            online = false;
            wse.sendMessageAll("服务已停止");
            System.out.println("服务已停止");
        } else if (!restart) {
            return;
        }
        if (restart) {
            instance = null;
            System.gc();
            getInstance().init();
        }
    }

    public Runnable shutdown(boolean b) {
        return () -> {
            shutdownInternal(b);
        };
    }
}
