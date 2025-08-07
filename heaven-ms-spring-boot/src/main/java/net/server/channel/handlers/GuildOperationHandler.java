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
package net.server.channel.handlers;

import config.CommonConfig;
import net.server.guild.MapleGuildResponse;
import net.server.guild.MapleGuild;
import constants.game.GameConstants;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.MaplePacketCreator;
import client.MapleCharacter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.server.Server;
import net.server.coordinator.matchchecker.MatchCheckerListenerFactory.MatchCheckerType;
import net.server.guild.MapleAlliance;
import net.server.world.MapleParty;
import net.server.world.World;
import tools.Log;

public final class GuildOperationHandler extends AbstractMaplePacketHandler {
    private boolean isGuildNameAcceptable(String name) {
        if (name.length() < 3 || name.length() > 12) {
            return false;
        }
        // 原逻辑不支持中文和数字
//        for (int i = 0; i < name.length(); i++) {
//            if (!java.lang.Character.isLowerCase(name.charAt(i)) && !java.lang.Character.isUpperCase(name.charAt(i))) {
//                return false;
//            }
//        }
//        return true;
        // 修改成不包含特殊字符
        return !Pattern.compile("[^\u4e00-\u9fa5a-zA-Z0-9_]").matcher(name).find();
    }

    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter mc = c.getPlayer();
        byte type = slea.readByte();
        int allianceId = -1;
        switch (type) {
            case 0x00:
                //c.announce(MaplePacketCreator.showGuildInfo(mc));
                break;
            case 0x02:
                if (mc.getGuildId() > 0) {
                    mc.dropMessage(1, "您已经创建过家族了");
                    return;
                }
                if (mc.getMeso() < CommonConfig.config.server.createGuildCost) {
                    mc.dropMessage(1, "您的金币不足" + GameConstants.numberWithCommas(CommonConfig.config.server.createGuildCost) + "，不能创建家族。");
                    return;
                }
                String guildName = slea.readMapleGbkString();
                if (!isGuildNameAcceptable(guildName)) {
                    mc.dropMessage(1, "家族名称不合法");
                    return;
                }

                Set<MapleCharacter> eligibleMembers = new HashSet<>(MapleGuild.getEligiblePlayersForGuild(mc));
                if (eligibleMembers.size() < CommonConfig.config.server.createGuildMinPartners) {
                    if (mc.getMap().getAllPlayers().size() < CommonConfig.config.server.createGuildMinPartners) {
                        // thanks NovaStory for noticing message in need of smoother info
                        mc.dropMessage(1, "您的公会目前没有足够的共同创始人在此，因此目前无法创建。");
                    } else {
                        // players may be unaware of not belonging on a party in order to become eligible, thanks Hair (Legalize) for pointing this out
                        mc.dropMessage(1, "请确保您尝试邀请的每个人都既不在公会中也不在队伍中。");
                    }

                    return;
                }

                if (!MapleParty.createParty(mc, true)) {
                    mc.dropMessage(1, "您不能在队伍中创建新的公会");
                    return;
                }

                Set<Integer> eligibleCids = new HashSet<>();
                for (MapleCharacter chr : eligibleMembers) {
                    eligibleCids.add(chr.getId());
                }

                c.getWorldServer().getMatchCheckerCoordinator().createMatchConfirmation(MatchCheckerType.GUILD_CREATION, c.getWorld(), mc.getId(), eligibleCids, guildName);
                break;
            case 0x05:
                if (mc.getGuildId() <= 0 || mc.getGuildRank() > 2) {
                    return;
                }

                String targetName = slea.readMapleGbkString();
                MapleGuildResponse mgr = MapleGuild.sendInvitation(c, targetName);
                if (mgr != null) {
                    c.announce(mgr.getPacket(targetName));
                } else {} // already sent invitation, do nothing

                break;
            case 0x06:
                if (mc.getGuildId() > 0) {
                    System.out.println("[Hack] " + mc.getName() + " 在已加入家族的情况下，试图在此加入家族");
                    return;
                }
                int gid = slea.readInt();
                int cid = slea.readInt();
                if (cid != mc.getId()) {
                    System.out.println("[Hack] " + mc.getName() + " 试图不用本角色加入家族");
                    return;
                }

                if (!MapleGuild.answerInvitation(cid, mc.getName(), gid, true)) {
                    return;
                }

                mc.getMGC().setGuildId(gid); // joins the guild
                mc.getMGC().setGuildRank(5); // start at lowest rank
                mc.getMGC().setAllianceRank(5);

                int s = Server.getInstance().addGuildMember(mc.getMGC(), mc);
                if (s == 0) {
                    mc.dropMessage(1, "您尝试加入的公会已经满了");
                    mc.getMGC().setGuildId(0);
                    return;
                }

                c.announce(MaplePacketCreator.showGuildInfo(mc));

                allianceId = mc.getGuild().getAllianceId();
                if(allianceId > 0) Server.getInstance().getAlliance(allianceId).updateAlliancePackets(mc);

                mc.saveGuildStatus(); // update database
                mc.getMap().broadcastMessage(mc, MaplePacketCreator.guildNameChanged(mc.getId(), mc.getGuild().getName())); // thanks Vcoc for pointing out an issue with updating guild tooltip to players in the map
                mc.getMap().broadcastMessage(mc, MaplePacketCreator.guildMarkChanged(mc.getId(), mc.getGuild()));
                break;
            case 0x07:
                cid = slea.readInt();
                String name = slea.readMapleGbkString();
                if (cid != mc.getId() || !name.equals(mc.getName()) || mc.getGuildId() <= 0) {
                    System.out.println("[Hack] " + mc.getName() + " 试图通过名称 \"" + name + "\" 来退出家族 " + mc.getGuildId() + ".");
                    return;
                }

                allianceId = mc.getGuild().getAllianceId();

                c.announce(MaplePacketCreator.updateGP(mc.getGuildId(), 0));
                Server.getInstance().leaveGuild(mc.getMGC());

                c.announce(MaplePacketCreator.showGuildInfo(null));
                if(allianceId > 0) Server.getInstance().getAlliance(allianceId).updateAlliancePackets(mc);

                mc.getMGC().setGuildId(0);
                mc.getMGC().setGuildRank(5);
                mc.saveGuildStatus();
                mc.getMap().broadcastMessage(mc, MaplePacketCreator.guildNameChanged(mc.getId(), ""));
                break;
            case 0x08:
                allianceId = mc.getGuild().getAllianceId();

                cid = slea.readInt();
                name = slea.readMapleGbkString();
                if (mc.getGuildRank() > 2 || mc.getGuildId() <= 0) {
                    System.out.println("[Hack] " + mc.getName() + " 试图驱逐联盟成员，但他的家族排名不是第1或第2");
                    return;
                }

                Server.getInstance().expelMember(mc.getMGC(), name, cid);
                if(allianceId > 0) Server.getInstance().getAlliance(allianceId).updateAlliancePackets(mc);
                break;
            case 0x0d:
                if (mc.getGuildId() <= 0 || mc.getGuildRank() != 1) {
                    System.out.println("[Hack] " + mc.getName() + "在超出权限的情况下试图改变家族排名");
                    return;
                }
                String ranks[] = new String[5];
                for (int i = 0; i < 5; i++) {
                    ranks[i] = slea.readMapleGbkString();
                }

                Server.getInstance().changeRankTitle(mc.getGuildId(), ranks);
                break;
            case 0x0e:
                cid = slea.readInt();
                byte newRank = slea.readByte();
                if (mc.getGuildRank() > 2 || (newRank <= 2 && mc.getGuildRank() != 1) || mc.getGuildId() <= 0) {
                    System.out.println("[Hack] " + mc.getName() + " 在超出权限的情况下试图改变家族排名");
                    return;
                }
                if (newRank <= 1 || newRank > 5) {
                    return;
                }
                Server.getInstance().changeRank(mc.getGuildId(), cid, newRank);
                break;
            case 0x0f:
                if (mc.getGuildId() <= 0 || mc.getGuildRank() != 1 || mc.getMapId() != 200000301) {
                    System.out.println("[Hack] " + mc.getName() + " 试图更改公会徽章，但不是公会领袖");
                    return;
                }
                if (mc.getMeso() < CommonConfig.config.server.changeEmblemCost) {
                    c.announce(MaplePacketCreator.serverNotice(1, "您的金币不足" + GameConstants.numberWithCommas(CommonConfig.config.server.changeEmblemCost) + "，不能改变家族徽章"));
                    return;
                }
                short bg = slea.readShort();
                byte bgcolor = slea.readByte();
                short logo = slea.readShort();
                byte logocolor = slea.readByte();
                Server.getInstance().setGuildEmblem(mc.getGuildId(), bg, bgcolor, logo, logocolor);

                if (mc.getGuild() != null && mc.getGuild().getAllianceId() > 0) {
                    MapleAlliance alliance = mc.getAlliance();
                    Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.getGuildAlliances(alliance, c.getWorld()), -1, -1);
                }

                mc.gainMeso(-CommonConfig.config.server.changeEmblemCost, true, false, true);
                mc.getGuild().broadcastNameChanged();
                mc.getGuild().broadcastEmblemChanged();
                break;
            case 0x10:
                if (mc.getGuildId() <= 0 || mc.getGuildRank() > 2) {
                    if(mc.getGuildId() <= 0) System.out.println("[Hack] " + mc.getName() + " 试图改变一个他不属于的家族的公告");
                    return;
                }
                String notice = slea.readMapleGbkString();
                if (notice.length() > 100) {
                    return;
                }
                Server.getInstance().setGuildNotice(mc.getGuildId(), notice);
                break;
            case 0x1E:
                slea.readInt();
                World wserv = c.getWorldServer();

                if (mc.getParty() != null) {
                    wserv.getMatchCheckerCoordinator().dismissMatchConfirmation(mc.getId());
                    return;
                }

                int leaderid = wserv.getMatchCheckerCoordinator().getMatchConfirmationLeaderid(mc.getId());
                if (leaderid != -1) {
                    boolean result = slea.readByte() != 0;
                    if (result && wserv.getMatchCheckerCoordinator().isMatchConfirmationActive(mc.getId())) {
                        MapleCharacter leader = wserv.getPlayerStorage().getCharacterById(leaderid);
                        if (leader != null) {
                            int partyid = leader.getPartyId();
                            if (partyid != -1) {
                                MapleParty.joinParty(mc, partyid, true);    // GMS gimmick "party to form guild" recalled thanks to Vcoc
                            }
                        }
                    }

                    wserv.getMatchCheckerCoordinator().answerMatchConfirmation(mc.getId(), result);
                }

                break;
            default:
                System.out.println("未知的家族操作包: \n" + slea.toString());
        }
    }
}
