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

package client.autoban;

import client.MapleCharacter;
import config.CommonConfig;
import net.server.Server;
import tools.FilePrinter;
import tools.MapleLogger;
import tools.MaplePacketCreator;

/**
 * 自动封禁角色并记录封禁原因
 */
public enum AutobanFactory {
    MOB_COUNT, // 怪物计数（刷怪作弊）
    GENERAL, // 一般通用作弊
    FIX_DAMAGE,// 伤害修复作弊
    DAMAGE_HACK(15, 60 * 1000),//伤害作弊（参数15表示检测到的伤害超过正常比例的阈值）
    DISTANCE_HACK(10, 120 * 1000),//移动速度作弊（参数10表示检测到的玩家移动速度或距离超过正常比例的阈值）
    PORTAL_DISTANCE(5, 30000),//传送门距离作弊（参数5表示检测到的玩家传送门距离超过正常范围的阈值）
    PACKET_EDIT,//数据包修改作弊
    ACC_HACK,//账号作弊
    CREATION_GENERATOR,//创建生成器作弊
    HIGH_HP_HEALING,//高血量治疗作弊
    FAST_HP_HEALING(15),//快速回血作弊（参数15表示快速回血的阈值）
    FAST_MP_HEALING(20, 30000),// 快速回蓝作弊（参数20表示快速回蓝的阈值）
    GACHA_EXP,//抽奖经验作弊
    TUBI(20, 15000),//刷物品作弊（参数20表示刷物品的阈值）
    SHORT_ITEM_VAC,//短距离物品吸取作弊
    ITEM_VAC,//物品吸取作弊
    FAST_ITEM_PICKUP(5, 30000),//快速拾取物品作弊（参数5表示快速拾取物品的阈值）
    FAST_ATTACK(10, 30000),//快速攻击作弊（参数10表示快速攻击的阈值，单位时间30000毫秒（30秒）表示多次触发快速攻击作弊的一个检测时间区间）
    MPCON(25, 30000);//蓝量检测作弊（参数25表示蓝量检测的阈值，单位时间30000毫秒（30秒）表示多次触发蓝量作弊的一个检测时间区间）

    private final int points;
    private final long expiretime;

    private AutobanFactory() {
        this(1, -1);
    }

    private AutobanFactory(int points) {
        this.points = points;
        this.expiretime = -1;
    }

    private AutobanFactory(int points, long expire) {
        this.points = points;
        this.expiretime = expire;
    }

    public int getMaximum() {
        return points;
    }

    public long getExpire() {
        return expiretime;
    }

    public void addPoint(AutobanManager ban, String reason) {
        ban.addPoint(this, reason);
    }

    public void alert(MapleCharacter chr, String reason) {
        if (CommonConfig.config.server.useAutoBan) {
            if (chr != null && MapleLogger.ignored.contains(chr.getId())) {
                return;
            }
            Server.getInstance().broadcastGMMessage((chr != null ? chr.getWorld() : 0), MaplePacketCreator.sendYellowTip((chr != null ? MapleCharacter.makeMapleReadable(chr.getName()) : "") + " caused " + this.name() + " " + reason));
        }
        if (CommonConfig.config.server.useAutoBanLog) {
            FilePrinter.print(FilePrinter.AUTOBAN_WARNING, (chr != null ? MapleCharacter.makeMapleReadable(chr.getName()) : "") + " caused " + this.name() + " " + reason);
        }
    }

    public void autoban(MapleCharacter chr, String value) {
        if (CommonConfig.config.server.useAutoBan) {
            chr.autoban("Autobanned for (" + this.name() + ": " + value + ")");
            //chr.sendPolice("You will be disconnected for (" + this.name() + ": " + value + ")");
        }
    }
}
