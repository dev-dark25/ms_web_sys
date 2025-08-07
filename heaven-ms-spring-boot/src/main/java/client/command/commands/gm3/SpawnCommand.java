package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;

public class SpawnCommand extends Command {
    {
        setDescription("生成怪物到玩家当前位置。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        // 检查命令参数
        if (params.length < 1) {
            player.yellowMessage("语法：!spawn <mobid> [<mobqty>]");
            return;
        }

        // 获取怪物ID
        int monsterId = Integer.parseInt(params[0]);
        MapleMonster monster = MapleLifeFactory.getMonster(monsterId);

        // 检查怪物是否存在
        if (monster == null) {
            return;
        }

        // 检查是否指定生成数量
        if (params.length == 2) {
            int spawnQty = Integer.parseInt(params[1]);
            for (int i = 0; i < spawnQty; i++) {
                // 在玩家当前位置生成怪物
                player.getMap().spawnMonsterOnGroundBelow(monster, player.getPosition());
            }
        } else {
            // 在玩家当前位置生成一个怪物
            player.getMap().spawnMonsterOnGroundBelow(monster, player.getPosition());
        }
    }
}