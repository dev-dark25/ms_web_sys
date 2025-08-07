package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import client.inventory.Equip;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.inventory.ItemConstants;

/**
 * 执行"SetEqStatCommand"操作的GM命令
 * Author: Arthur L
 */
public class SetEqStatCommand extends Command {
    {
        setDescription("为玩家的所有装备设置统一的属性值（如力量、敏捷、生命等）和速度跳跃属性值。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!seteqstat <stat value> [<spdjmp value>]");
            return;
        }

        // 解析输入参数
        short newStat = (short) Math.max(0, Integer.parseInt(params[0]));
        short newSpdJmp = params.length >= 2 ? (short) Integer.parseInt(params[1]) : 0;

        // 获取玩家的装备物品
        MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);

        // 遍历玩家的所有装备物品
        for (byte i = 1; i <= equip.getSlotLimit(); i++) {
            try {
                Equip eq = (Equip) equip.getItem(i);
                if (eq == null) continue;

                // 设置装备的属性值
                eq.setWdef(newStat);
                eq.setAcc(newStat);
                eq.setAvoid(newStat);
                eq.setJump(newSpdJmp);
                eq.setMatk(newStat);
                eq.setMdef(newStat);
                eq.setHp(newStat);
                eq.setMp(newStat);
                eq.setSpeed(newSpdJmp);
                eq.setWatk(newStat);
                eq.setDex(newStat);
                eq.setInt(newStat);
                eq.setStr(newStat);
                eq.setLuk(newStat);

                // 设置装备为不可交易
                short flag = eq.getFlag();
                flag |= ItemConstants.UNTRADEABLE;
                eq.setFlag(flag);

                // 强制更新玩家的装备
                player.forceUpdateItem(eq);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}