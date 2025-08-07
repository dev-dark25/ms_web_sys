package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;

/**
 * 执行"ProItemCommand"操作的GM命令
 * Author: Arthur L
 */
public class ProItemCommand extends Command {
    {
        setDescription("为自己创建一个具有指定属性的装备。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 2) {
            player.yellowMessage("语法：!proitem <itemid> <stat value> [<spdjmp value>]");
            return;
        }

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int itemid = Integer.parseInt(params[0]);

        if(ii.getName(itemid) == null) {
            player.yellowMessage("Item id '" + params[0] + "' does not exist.");
            return;
        }

        short stat = (short) Math.max(0, Short.parseShort(params[1]));
        short spdjmp = params.length >= 3 ? (short) Math.max(0, Short.parseShort(params[2])) : 0;

        MapleInventoryType type = ItemConstants.getInventoryType(itemid);
        if (type.equals(MapleInventoryType.EQUIP)) {
            Item it = ii.getEquipById(itemid);
            it.setOwner(player.getName());

            hardsetItemStats((Equip) it, stat, spdjmp);
            MapleInventoryManipulator.addFromDrop(c, it);
        } else {
            player.dropMessage(6, "确保它是可以装备的物品。");
        }
    }

    // 设置装备的属性
    private static void hardsetItemStats(Equip equip, short stat, short spdjmp) {
        equip.setStr(stat);
        equip.setDex(stat);
        equip.setInt(stat);
        equip.setLuk(stat);
        equip.setMatk(stat);
        equip.setWatk(stat);
        equip.setAcc(stat);
        equip.setAvoid(stat);
        equip.setJump(spdjmp);
        equip.setSpeed(spdjmp);
        equip.setWdef(stat);
        equip.setMdef(stat);
        equip.setHp(stat);
        equip.setMp(stat);

        short flag = equip.getFlag();
        flag |= ItemConstants.UNTRADEABLE;
        equip.setFlag(flag);
    }
}