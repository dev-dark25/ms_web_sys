package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class HpMpCommand extends Command {
    {
        // 设置命令描述
        setDescription("为指定玩家恢复HP和MP或修改HP和MP值。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        MapleCharacter victim = player;
        int statUpdate = 1;

        if (params.length == 2) {
            // 如果提供了两个参数，第一个参数是玩家名称，第二个参数是数值
            victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
            statUpdate = Integer.valueOf(params[1]);
        } else if (params.length == 1) {
            // 如果只提供了一个参数，该参数是数值
            statUpdate = Integer.valueOf(params[0]);
        } else {
            player.yellowMessage("语法：!hpmp [<playername>] <value>");
        }

        if (victim != null) {
            // 更新玩家的HP和MP值
            victim.updateHpMp(statUpdate);
        } else {
            player.message("玩家 '" + params[0] + "' 未在这个世界中找到。");
        }
    }
}