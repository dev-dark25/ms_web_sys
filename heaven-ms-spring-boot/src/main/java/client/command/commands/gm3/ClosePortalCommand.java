package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class ClosePortalCommand extends Command {
    {
        setDescription("关闭指定地图上的传送门。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!closeportal <portalid>");
            return;
        }
        player.getMap().getPortal(params[0]).setPortalState(false);
    }
}