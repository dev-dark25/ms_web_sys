package client.command.commands.gm7;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleLifeFactory;

/**
 * 执行"SummonMonsterCommand"操作的GM命令
 */
public class SummonMonsterCommand extends Command {
    {
        setDescription("根据怪物id召唤怪物及数量。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        int monsterId, monsterCount;
        try {
            monsterId = Integer.parseInt(params[0]);
            monsterCount = Integer.parseInt(params[1]) > 3 ? 3 : Integer.parseInt(params[1]) <= 0 ? 1 : Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            player.dropMessage("请输入正确的数字");
            return;
        }

        // 在玩家位置下方生成怪物
        for (int i = 0; i < monsterCount; i++) {
            player.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(monsterId), player.getPosition());
        }

    }
}