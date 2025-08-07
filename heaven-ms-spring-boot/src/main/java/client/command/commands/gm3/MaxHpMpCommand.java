package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class MaxHpMpCommand extends Command {
    {
        // 设置命令描述
        setDescription("设置自己或其他玩家的最大HP和MP值。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        MapleCharacter victim = player;

        int statUpdate = 1;
        if (params.length >= 2) {
            // 如果提供了两个参数，则第一个参数是目标玩家的名称
            victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
            statUpdate = Integer.valueOf(params[1]);
        } else if (params.length == 1) {
            // 如果只提供了一个参数，那么将其视为要更新的值
            statUpdate = Integer.valueOf(params[0]);
        } else {
            // 参数不正确时，显示正确的语法
            player.yellowMessage("语法：!maxhpmp [<playername>] <value>");
        }

        if (victim != null) {
            // 计算要设置的新最大HP和MP值
            int extraHp = victim.getCurrentMaxHp() - victim.getClientMaxHp();
            int extraMp = victim.getCurrentMaxMp() - victim.getClientMaxMp();
            statUpdate = Math.max(1 + Math.max(extraHp, extraMp), statUpdate);

            int maxhpUpdate = statUpdate - extraHp;
            int maxmpUpdate = statUpdate - extraMp;

            // 更新目标玩家的最大HP和MP值
            victim.updateMaxHpMaxMp(maxhpUpdate, maxmpUpdate);
        } else {
            player.message("无法在当前游戏世界中找到玩家 '" + params[0] + "'。");
        }
    }
}