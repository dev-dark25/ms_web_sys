package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;

/**
 * 在游戏地图中生成"蛋糕怪物"的GM命令
 * Author: Arthur L
 */
public class CakeCommand extends Command {
    {
        setDescription("在游戏地图中生成一个蛋糕怪物。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        MapleMonster monster = MapleLifeFactory.getMonster(9400606);

        // 如果命令参数中包含怪物的初始HP，则将其设置为怪物的初始HP
        if (params.length == 1) {
            double mobHp = Double.parseDouble(params[0]);
            // 将怪物的初始HP限制在合理范围内
            int newHp = (mobHp <= 0) ? Integer.MAX_VALUE : ((mobHp > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) mobHp);

            monster.setStartingHp(newHp);
        }

        // 在玩家位置下方的地图上生成怪物
        player.getMap().spawnMonsterOnGroundBelow(monster, player.getPosition());
    }
}