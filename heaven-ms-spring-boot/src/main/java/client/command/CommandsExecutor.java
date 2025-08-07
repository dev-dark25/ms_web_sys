/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2019 RonanLana

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

/*
   @Author: Arthur L - Refactored command content into modules
*/
package client.command;

import client.command.commands.gm0.*;
import client.command.commands.gm1.*;
import client.command.commands.gm2.*;
import client.command.commands.gm3.*;
import client.command.commands.gm3.HideCommand;
import client.command.commands.gm3.UnHideCommand;
import client.command.commands.gm4.*;
import client.command.commands.gm5.*;
import client.command.commands.gm6.*;

import client.MapleClient;

import client.command.commands.gm7.*;
import cn.nap.utils.common.NapComUtils;
import tools.FilePrinter;
import tools.Pair;
import ui.model.GmCommand;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;

public class CommandsExecutor {

    public static CommandsExecutor instance = new CommandsExecutor();

    public static CommandsExecutor getInstance() {
        return instance;
    }

    // 普通指令
    private static final char USER_HEADING = '@';
    // GM指令
    private static final char GM_HEADING = '!';

    public static boolean isCommand(MapleClient client, String content) {
        char heading = content.charAt(0);
        if (client.getPlayer().isGM()) {
            return heading == USER_HEADING || heading == GM_HEADING;
        }
        return heading == USER_HEADING;
    }

    private final HashMap<String, Command> registeredCommands = new HashMap<>();
    private Pair<List<String>, List<String>> levelCommandsCursor;
    private final List<Pair<List<String>, List<String>>> commandsNameDesc = new ArrayList<>();

    private CommandsExecutor() {
        System.out.println("new CommandsExecutor");
        registerCommands();
        //registerLv0Commands();
        //registerLv1Commands();
        //registerLv2Commands();
        //registerLv3Commands();
        //registerLv4Commands();
        //registerLv5Commands();
        //registerLv6Commands();
        registerLv7Commands();
    }

    public List<Pair<List<String>, List<String>>> getGmCommands() {
        return commandsNameDesc;
    }

    /**
     * 处理指令
     *
     * @param client  客户端对象
     * @param message 指令内容
     */
    public void handle(MapleClient client, String message) {
        if (client.tryacquireClient()) {
            try {
                handleInternal(client, message);
            } finally {
                client.releaseClient();
            }
        } else {
            client.getPlayer().dropMessage(5, "请稍后再试，上一条指令正在执行中。。。");
        }
    }

    private void handleInternal(MapleClient client, String message) {
        if (client.getPlayer().getMapId() == 300000012) {
            client.getPlayer().yellowMessage("监狱里不允许使用指令");
            return;
        }
        final String splitRegex = " ";
        String[] splitedMessage = message.substring(1).split(splitRegex, 2);
        if (splitedMessage.length < 2) {
            splitedMessage = new String[]{splitedMessage[0], ""};
        }

        client.getPlayer().setLastCommandMessage(splitedMessage[1]);    // thanks Tochi & Nulliphite for noticing string messages being marshalled lowercase
        final String commandName = splitedMessage[0].toLowerCase();//指令名称
        final String[] lowercaseParams = splitedMessage[1].toLowerCase().split(splitRegex);

        final Command command = registeredCommands.get(commandName);//根据名字获取指令
        if (command == null) {
            client.getPlayer().yellowMessage("指令 '" + commandName + "' 无效！输入 @commands 查看可用的指令。");
            return;
        }
        if (client.getPlayer().gmLevel() < command.getRank()) {
            System.out.println("play gmLevel: " + client.getPlayer().gmLevel());
            System.out.println("command getRank: " + command.getRank());
            client.getPlayer().yellowMessage("你没有权限使用这个指令");
            return;
        }
        String[] params;
        if (lowercaseParams.length > 0 && !lowercaseParams[0].isEmpty()) {
            params = Arrays.copyOfRange(lowercaseParams, 0, lowercaseParams.length);
        } else {
            params = new String[]{};
        }

        command.execute(client, params);//执行指令,抽象方法(接口)，根据不同实例执行不同类方法
        writeLog(client, message);
    }

    private void writeLog(MapleClient client, String command) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        FilePrinter.print(FilePrinter.USED_COMMANDS, client.getPlayer().getName() + " used: " + command + " on "
                + sdf.format(Calendar.getInstance().getTime()));
    }

    private void addCommandInfo(String name, int rank, Class<? extends Command> commandClass) {
        try {
            levelCommandsCursor.getRight().add(rank + "." + commandClass.newInstance().getDescription());
            levelCommandsCursor.getLeft().add(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addCommand(String[] syntaxs, Class<? extends Command> commandClass) {
        for (String syntax : syntaxs) {
            addCommand(syntax, 0, commandClass);
        }
    }

    private void addCommand(String syntax, Class<? extends Command> commandClass) {
        //for (String syntax : syntaxs){
        addCommand(syntax, 0, commandClass);
        //}
    }

    private void addCommand(String[] surtaxes, int rank, Class<? extends Command> commandClass) {
        for (String syntax : surtaxes) {
            addCommand(syntax, rank, commandClass);
        }
    }

    private void addCommand(String syntax, int rank, Class<? extends Command> commandClass) {
        if (registeredCommands.containsKey(syntax.toLowerCase())) {
            System.out.println("注册指令: " + syntax + " 失败，该指令已存在。");
            return;
        }

        String commandName = syntax.toLowerCase();
        addCommandInfo(commandName, rank, commandClass);

        try {
            Command commandInstance = commandClass.newInstance();     // thanks Halcyon for noticing commands getting reinstanced every call
            commandInstance.setRank(rank);
            registeredCommands.put(commandName, commandInstance);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        // 预制level0-level6的commandsNameDesc
        for (int i = 0; i <= 6; i++) {
            commandsNameDesc.add(new Pair<>(new ArrayList<>(), new ArrayList<>()));
        }
        List<GmCommand> gmCommands = CommandResolver.getInstance().getgmCommandList();
        if (NapComUtils.isEmpty(gmCommands)) {
            System.out.println("GM指令表数据为空");
            return;
        }

        for (GmCommand gmCommand : gmCommands) {
            if (!gmCommand.getIsWork()) {
                continue;
            }
            Class<? extends Command> commandClass = gmCommand.getCommandClass();
            if (commandClass == null) {
                System.out.println("GM指令表数据错误: " + gmCommand.getCommand() + " 指令类为空");
                continue;
            }
            Command command;
            try {
                command = commandClass.newInstance();
            } catch (Exception e) {
                System.out.println("GM指令表数据错误: " + gmCommand.getCommand() + " 指令类无法实例化");
                continue;
            }
            String commandName = gmCommand.getCommand().toLowerCase();
            if (registeredCommands.containsKey(commandName)) {
                System.out.println("注册指令: " + gmCommand.getCommand() + " 失败，该指令已存在。");
                continue;
            }
            int level = Optional.ofNullable(gmCommand.getGmLevel()).orElse(gmCommand.getGmLevelPrimitive());
            if (level < 0) {
                level = 0;
            } else if (level > 6) {
                level = 6;
            }
            command.setRank(level);
            Pair<List<String>, List<String>> pair = commandsNameDesc.get(level);
            pair.getLeft().add(commandName);
            pair.getRight().add(level + "." + command.getDescription());
            registeredCommands.put(commandName, command);
        }
    }

    public void resetAll() {
        commandsNameDesc.clear();
        registeredCommands.clear();
        registerCommands();
    }

    public void reloadGmCommands(List<GmCommand> gmCommands) {
        for (GmCommand gmCommand : gmCommands) {
            Class<? extends Command> commandClass = gmCommand.getCommandClass();
            if (commandClass == null) {
                System.out.println("GM指令表数据错误: " + gmCommand.getCommand() + " 指令类为空");
                continue;
            }
            Command command;
            try {
                command = commandClass.newInstance();
            } catch (Exception e) {
                System.out.println("GM指令表数据错误: " + gmCommand.getCommand() + " 指令类无法实例化");
                continue;
            }
            String commandName = gmCommand.getCommand().toLowerCase();
            Command oriCommand = registeredCommands.get(commandName);
            if (oriCommand != null) {
                int oriLevel = oriCommand.getRank();
                Pair<List<String>, List<String>> oriNameDesc = commandsNameDesc.get(oriLevel);
                int oriIndex = oriNameDesc.getLeft().indexOf(commandName);
                oriNameDesc.getLeft().remove(oriIndex);
                oriNameDesc.getRight().remove(oriIndex);
                registeredCommands.remove(commandName);
            }
            if (gmCommand.getIsWork()) {
                int level = Optional.ofNullable(gmCommand.getGmLevel()).orElse(gmCommand.getGmLevelPrimitive());
                if (level < 0) {
                    level = 0;
                } else if (level > 6) {
                    level = 6;
                }
                Pair<List<String>, List<String>> currNameDesc = commandsNameDesc.get(level);
                currNameDesc.getLeft().add(commandName);
                currNameDesc.getRight().add(level + "." + command.getDescription());
                registeredCommands.put(commandName, command);
            }
        }
    }

    private void registerLv0Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());

        addCommand(new String[]{"help", "commands"}, HelpCommand.class);
        addCommand("droplimit", DropLimitCommand.class);
        addCommand("time", TimeCommand.class);
        addCommand("buyback", BuyBackCommand.class);
        addCommand("gacha", GachaCommand.class);
        addCommand("dispose", DisposeCommand.class);
        addCommand("equiplv", EquipLvCommand.class);
        addCommand("rates", ShowRatesCommand.class);
        addCommand("online", OnlineCommand.class);
        addCommand("bug", ReportBugCommand.class);
        addCommand("points", ReadPointsCommand.class);
        addCommand("event", JoinEventCommand.class);
        addCommand("ranks", RanksCommand.class);
        addCommand("autoadd", AutoAddCommand.class);
        addCommand("toggleexp", ToggleExpCommand.class);
        addCommand("mapowner", MapOwnerClaimCommand.class);
        addCommand("map", MapIdCommand.class);
        addCommand("buyfame", BuyFameCommand.class);
        addCommand("pfp", PfpCommand.class);
        addCommand("shtj", DamageStatisticsCommand.class);
        commandsNameDesc.add(levelCommandsCursor);
    }


    private void registerLv1Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());

        addCommand("bosshp", 1, BossHpCommand.class);
        addCommand("mobhp", 1, MobHpCommand.class);
        addCommand("whatdropsfrom", 1, WhatDropsFromCommand.class);
        addCommand("whodrops", 1, WhoDropsCommand.class);
        addCommand("buffme", 1, BuffMeCommand.class);
        addCommand("goto", 1, GotoCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }


    private void registerLv2Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());

        addCommand("recharge", 2, RechargeCommand.class);
        addCommand("whereami", 2, WhereaMiCommand.class);
        addCommand("hide", 2, HideCommand.class);
        addCommand("unhide", 2, UnHideCommand.class);
        addCommand("sp", 2, SpCommand.class);
        addCommand("ap", 2, ApCommand.class);
        addCommand("empowerme", 2, EmpowerMeCommand.class);
        addCommand("buffmap", 2, BuffMapCommand.class);
        addCommand("buff", 2, BuffCommand.class);
        addCommand("bomb", 2, BombCommand.class);
        addCommand("dc", 2, DcCommand.class);
        addCommand("cleardrops", 2, ClearDropsCommand.class);
        addCommand("clearslot", 2, ClearSlotCommand.class);
        addCommand("clearsavelocs", 2, ClearSavedLocationsCommand.class);
        addCommand("warp", 2, WarpCommand.class);
        addCommand(new String[]{"warphere", "summon"}, 2, SummonCommand.class);
        addCommand(new String[]{"warpto", "reach", "follow"}, 2, ReachCommand.class);
        addCommand("gmshop", 2, GmShopCommand.class);
        addCommand("heal", 2, HealCommand.class);
        addCommand("item", 2, ItemCommand.class);
        addCommand("drop", 2, ItemDropCommand.class);
        addCommand("level", 2, LevelCommand.class);
        addCommand("levelpro", 2, LevelProCommand.class);
        addCommand("setslot", 2, SetSlotCommand.class);
        addCommand("setstat", 2, SetStatCommand.class);
        addCommand("maxstat", 2, MaxStatCommand.class);
        addCommand("maxskill", 2, MaxSkillCommand.class);
        addCommand("resetskill", 2, ResetSkillCommand.class);
        addCommand("search", 2, SearchCommand.class);
        addCommand("jail", 2, JailCommand.class);
        addCommand("unjail", 2, UnJailCommand.class);
        addCommand("job", 2, JobCommand.class);
        addCommand("unbug", 2, UnBugCommand.class);
        addCommand("id", 2, IdCommand.class);
        addCommand("gachalist", GachaListCommand.class);
        addCommand("loot", LootCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv3Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());

        addCommand("debuff", 3, DebuffCommand.class);
        addCommand("fly", 3, FlyCommand.class);
        addCommand("spawn", 3, SpawnCommand.class);
        addCommand("mutemap", 3, MuteMapCommand.class);
        addCommand("checkdmg", 3, CheckDmgCommand.class);
        addCommand("inmap", 3, InMapCommand.class);
        addCommand("reloadevents", 3, ReloadEventsCommand.class);
        addCommand("reloaddrops", 3, ReloadDropsCommand.class);
        addCommand("reloadportals", 3, ReloadPortalsCommand.class);
        addCommand("reloadmap", 3, ReloadMapCommand.class);
        addCommand("reloadshops", 3, ReloadShopsCommand.class);
        addCommand("hpmp", 3, HpMpCommand.class);
        addCommand("maxhpmp", 3, MaxHpMpCommand.class);
        addCommand("music", 3, MusicCommand.class);
        addCommand("monitor", 3, MonitorCommand.class);
        addCommand("monitors", 3, MonitorsCommand.class);
        addCommand("ignore", 3, IgnoreCommand.class);
        addCommand("ignored", 3, IgnoredCommand.class);
        addCommand("pos", 3, PosCommand.class);
        addCommand("togglecoupon", 3, ToggleCouponCommand.class);
        addCommand("togglewhitechat", 3, ChatCommand.class);
        addCommand("fame", 3, FameCommand.class);
        addCommand("givenx", 3, GiveNxCommand.class);
        addCommand("givevp", 3, GiveVpCommand.class);
        addCommand("givems", 3, GiveMesosCommand.class);
        addCommand("giverp", 3, GiveRpCommand.class);
        addCommand("expeds", 3, ExpedsCommand.class);
        addCommand("kill", 3, KillCommand.class);
        addCommand("seed", 3, SeedCommand.class);
        addCommand("maxenergy", 3, MaxEnergyCommand.class);
        addCommand("killall", 3, KillAllCommand.class);
        addCommand("notice", 3, NoticeCommand.class);
        addCommand("rip", 3, RipCommand.class);
        addCommand("openportal", 3, OpenPortalCommand.class);
        addCommand("closeportal", 3, ClosePortalCommand.class);
        addCommand("pe", 3, PeCommand.class);
        addCommand("startevent", 3, StartEventCommand.class);
        addCommand("endevent", 3, EndEventCommand.class);
        addCommand("startmapevent", 3, StartMapEventCommand.class);
        addCommand("stopmapevent", 3, StopMapEventCommand.class);
        addCommand("online2", 3, OnlineTwoCommand.class);
        addCommand("ban", 3, BanCommand.class);
        addCommand("unban", 3, UnBanCommand.class);
        addCommand("healmap", 3, HealMapCommand.class);
        addCommand("healperson", 3, HealPersonCommand.class);
        addCommand("hurt", 3, HurtCommand.class);
        addCommand("killmap", 3, KillMapCommand.class);
        addCommand("night", 3, NightCommand.class);
        addCommand("npc", 3, NpcCommand.class);
        addCommand("face", 3, FaceCommand.class);
        addCommand("hair", 3, HairCommand.class);
        addCommand("startquest", 3, QuestStartCommand.class);
        addCommand("completequest", 3, QuestCompleteCommand.class);
        addCommand("resetquest", 3, QuestResetCommand.class);
        addCommand("timer", 3, TimerCommand.class);
        addCommand("timermap", 3, TimerMapCommand.class);
        addCommand("timerall", 3, TimerAllCommand.class);
        addCommand("warpmap", 3, WarpMapCommand.class);
        addCommand("warparea", 3, WarpAreaCommand.class);
        addCommand("gotonpc", 3, GotoNpcCommand.class);
        addCommand("xiguai", 3, SuckMonsterCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv4Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());

        addCommand("servermessage", 4, ServerMessageCommand.class);
        addCommand("proitem", 4, ProItemCommand.class);
        addCommand("seteqstat", 4, SetEqStatCommand.class);
        addCommand("exprate", 4, ExpRateCommand.class);
        addCommand("mesorate", 4, MesoRateCommand.class);
        addCommand("droprate", 4, DropRateCommand.class);
        addCommand("bossdroprate", 4, BossDropRateCommand.class);
        addCommand("questrate", 4, QuestRateCommand.class);
        addCommand("travelrate", 4, TravelRateCommand.class);
        addCommand("fishrate", 4, FishingRateCommand.class);
        addCommand("itemvac", 4, ItemVacCommand.class);
        addCommand("forcevac", 4, ForceVacCommand.class);
        addCommand("zakum", 4, ZakumCommand.class);
        addCommand("horntail", 4, HorntailCommand.class);
        addCommand("pinkbean", 4, PinkbeanCommand.class);
        addCommand("pap", 4, PapCommand.class);
        addCommand("pianus", 4, PianusCommand.class);
        addCommand("cake", 4, CakeCommand.class);
        addCommand("playernpc", 4, PlayerNpcCommand.class);
        addCommand("playernpcremove", 4, PlayerNpcRemoveCommand.class);
        addCommand("pnpc", 4, PnpcCommand.class);
        addCommand("pnpcremove", 4, PnpcRemoveCommand.class);
        addCommand("pmob", 4, PmobCommand.class);
        addCommand("pmobremove", 4, PmobRemoveCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv5Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());

        addCommand("debug", 5, DebugCommand.class);
        addCommand("set", 5, SetCommand.class);
        addCommand("showpackets", 5, ShowPacketsCommand.class);
        addCommand("showmovelife", 5, ShowMoveLifeCommand.class);
        addCommand("showsessions", 5, ShowSessionsCommand.class);
        addCommand("iplist", 5, IpListCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv6Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());

        addCommand("setgmlevel", 6, SetGmLevelCommand.class);
        addCommand("warpworld", 6, WarpWorldCommand.class);
        addCommand("saveall", 6, SaveAllCommand.class);
        addCommand("dcall", 6, DCAllCommand.class);
        addCommand("mapplayers", 6, MapPlayersCommand.class);
        addCommand("getacc", 6, GetAccCommand.class);
        addCommand("shutdown", 6, ShutdownCommand.class);
        addCommand("clearquestcache", 6, ClearQuestCacheCommand.class);
        addCommand("clearquest", 6, ClearQuestCommand.class);
        addCommand("supplyratecoupon", 6, SupplyRateCouponCommand.class);
        addCommand("spawnallpnpcs", 6, SpawnAllPNpcsCommand.class);
        addCommand("eraseallpnpcs", 6, EraseAllPNpcsCommand.class);
        addCommand("addchannel", 6, ServerAddChannelCommand.class);
        addCommand("addworld", 6, ServerAddWorldCommand.class);
        addCommand("removechannel", 6, ServerRemoveChannelCommand.class);
        addCommand("removeworld", 6, ServerRemoveWorldCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv7Commands() {
        //  /scripts/npc/commands.js
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());

        addCommand("summonMonster", 7, SummonMonsterCommand.class);
        addCommand("updateAP", 7, UpdateAPCommand.class);
        addCommand("updateSP", 7, UpdateSPCommand.class);
        addCommand("showPlayers", 7, ShowPlayersCommand.class);
        addCommand("give", 7, GiveCommand.class);

        addCommand("gmCommand", 7, client.command.commands.gm7.GmCommand.class);
        addCommand("ratesCommand", 7, RatesCommand.class);
        addCommand("staffCommand", 7, StaffCommand.class);
        addCommand("uptimeCommand", 7, UptimeCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

}
