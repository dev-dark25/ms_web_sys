package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.life.MaplePlayerNPC;

/**
 * 执行"PlayerNpcRemoveCommand"操作的GM命令
 * Author: Arthur L
 */
public class PlayerNpcRemoveCommand extends Command {
    {
        setDescription("从当前地图移除一个玩家NPC。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!playernpcremove <playername>");
            return;
        }

        // 从当前地图移除指定的玩家NPC
        MaplePlayerNPC.removePlayerNPC(c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]));
    }
}