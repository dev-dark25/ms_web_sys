package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.life.MapleLifeFactory;

/**
 * 执行"PapCommand"操作的GM命令
 * Author: Arthur L
 */
public class PapCommand extends Command {
    {
        setDescription("在玩家位置下方生成Papulatus BOSS怪物。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        // 在玩家位置下方生成Papulatus BOSS怪物
        player.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8500001), player.getPosition());
    }
}