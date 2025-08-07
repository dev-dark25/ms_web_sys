package client.command.commands.gm7;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;

public class GiveCommand extends Command {
    {
        setDescription("give player meso/nx/exp");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        if (params.length < 3) {
            player.yellowMessage("输入: @give <type: meso/nv/exp> <characterId> <count>");
            return;
        }

        String type;
        int characterId, count;
        try {
            type = params[0];
            characterId = Integer.parseInt(params[1]);
            count = Integer.parseInt(params[2]);
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
        } else {

        }

    }
}