package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class PosCommand extends Command {
    {
        setDescription("获取玩家当前位置信息和Foothold ID。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        float xpos = player.getPosition().x;
        float ypos = player.getPosition().y;
        float fh = player.getMap().getFootholds().findBelow(player.getPosition()).getId();

        // 打印玩家当前位置信息和Foothold ID
        player.dropMessage(6, "当前位置: (" + xpos + ", " + ypos + ")");
        player.dropMessage(6, "Foothold ID: " + fh);
    }
}