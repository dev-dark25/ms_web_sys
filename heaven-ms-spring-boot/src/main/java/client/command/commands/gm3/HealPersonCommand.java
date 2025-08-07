package client.command.commands.gm3;

import client.MapleStat;
import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class HealPersonCommand extends Command {
    {
        // 设置命令描述
        setDescription("为指定玩家恢复健康（HP和MP）。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        // 获取指定的玩家角色
        MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
        if (victim != null) {
            // 恢复指定玩家的HP和MP
            victim.healHpMp();
        } else {
            player.message("玩家 '" + params[0] + "' 未找到。");
        }
    }
}