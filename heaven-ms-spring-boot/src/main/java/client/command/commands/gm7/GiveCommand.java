package client.command.commands.gm7;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.inventory.Equip;
import server.MapleItemInformationProvider;

public class GiveCommand extends Command {
    {
        setDescription("give player meso/nx/exp");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        if (params.length < 3) {
            player.yellowMessage("输入: @give <type: meso/nx/exp/item/equip> <characterId> <count> <itemId/equipId> <str|dex|int|luk|hp|mp|watk|matk|wdef|mdef|acc|avoid|speed|jump>");
            return;
        }

        String type;
        int characterId, count, item = 0;
        String[] equip = new String[14];
        try {
            type = params[0];
            characterId = Integer.parseInt(params[1]);
            count = Integer.parseInt(params[2]);
            if (params.length == 4) {
                item = Integer.parseInt(params[3]);
            }
            if (params.length == 5) {
                item = Integer.parseInt(params[3]);
                equip = params[4].split("\\|");
            }
        } catch (NumberFormatException e) {
            player.dropMessage("请输入正确的参数");
            return;
        }

        MapleCharacter targetCharacter = c.getWorldServer().getPlayerStorage().getCharacterById(characterId);
        if (targetCharacter == null) {
            player.message("characterId: " + characterId + " 不存在");
            return;
        }

        if ("meso".equals(type)) {
            targetCharacter.gainMeso(count);
            targetCharacter.startMapEffect("管理员给你发送了" + count + "金币！", 5120015);
            player.message(targetCharacter.getName() + " 已收到金币：" + count);
        } else if ("nx".equals(type)) {
            targetCharacter.getCashShop().gainCash(1, count);
            targetCharacter.getCashShop().gainCash(2, count);
            targetCharacter.getCashShop().gainCash(4, count);
            targetCharacter.startMapEffect("管理员给你发送了" + count + "点券！", 5120015);
            player.message(targetCharacter.getName() + " 已收到" + "点券、皇家点券、代金券" + "各：" + count);
        } else if ("exp".equals(type)) {
            targetCharacter.gainExp(count);
            targetCharacter.startMapEffect("管理员给你发送了" + count + "经验！", 5120015);
            player.message(targetCharacter.getName() + " 已收到经验：" + count);
        } else if ("item".equals(type)) {
            targetCharacter.getAbstractPlayerInteraction().gainItem(item, (short) (count));
            String itemName = MapleItemInformationProvider.getInstance().getName(item);
            targetCharacter.startMapEffect("管理员给你发送了道具：" + itemName + count + "个", 5120015);
            player.message(targetCharacter.getName() + " 已收到道具：" + itemName + count + "个");
        } else if ("equip".equals(type)) {
            if (params.length == 5) {
                Equip eqp = (Equip) targetCharacter.getAbstractPlayerInteraction().gainEquip(item, Short.parseShort(equip[0]), Short.parseShort(equip[1]),
                        Short.parseShort(equip[2]), Short.parseShort(equip[3]), Short.parseShort(equip[4]), Short.parseShort(equip[5]),
                        Short.parseShort(equip[6]), Short.parseShort(equip[7]), Short.parseShort(equip[8]), Short.parseShort(equip[9]),
                        Short.parseShort(equip[10]), Short.parseShort(equip[11]), Short.parseShort(equip[12]), Short.parseShort(equip[13]), -1L, (short) 1);
                targetCharacter.startMapEffect("管理员给你发送了装备：" + eqp, 5120015);
                player.message(targetCharacter.getName() + " 已收到装备：" + eqp);
            } else {
                Equip eqp = (Equip) targetCharacter.getAbstractPlayerInteraction().gainEquip(item, null, null,
                        null, null, null, null, null, null, null, null,
                        null, null, null, null, -1L, (short) 1);
                targetCharacter.startMapEffect("管理员给你发送了装备：" + eqp, 5120015);
                player.message(targetCharacter.getName() + " 已收到装备：" + eqp);
            }
        } else {

        }

    }
}