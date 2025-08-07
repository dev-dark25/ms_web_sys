package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.life.MaplePlayerNPC;

/**
 * 执行"PlayerNpcCommand"操作的GM命令
 * Author: Arthur L
 */
public class PlayerNpcCommand extends Command {
    {
        setDescription("在当前地图生成一个玩家NPC。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!playernpc <playername>");
            return;
        }

        // 生成一个玩家NPC并将其放置在当前地图上
        if (!MaplePlayerNPC.spawnPlayerNPC(player.getMapId(), player.getPosition(), c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]))) {
            player.dropMessage(5, "无法生成玩家NPC。要么当前地图没有足够的空间，要么已用尽了可用的脚本ID。");
        }
    }
}