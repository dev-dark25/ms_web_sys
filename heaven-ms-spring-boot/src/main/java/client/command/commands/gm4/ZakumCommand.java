package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.life.MapleLifeFactory;

/**
 * 执行"ZakumCommand"操作的GM命令
 * Author: Arthur L
 */
public class ZakumCommand extends Command {
    {
        setDescription("召唤扎昆怪物。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        // 在玩家位置下方生成扎昆的假怪物
        player.getMap().spawnFakeMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800000), player.getPosition());

        // 生成扎昆的其他怪物
        for (int x = 8800003; x < 8800011; x++) {
            player.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(x), player.getPosition());
        }
    }
}