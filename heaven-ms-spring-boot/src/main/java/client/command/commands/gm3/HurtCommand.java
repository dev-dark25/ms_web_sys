package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class HurtCommand extends Command {
    {
        // 设置命令描述
        setDescription("伤害指定玩家的1点HP。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
        if (victim != null) {
            // 更新玩家的HP，减少1点
            victim.updateHp(1);
        } else {
            player.message("玩家 '" + params[0] + "' 未在这个世界中找到。");
        }
    }
}