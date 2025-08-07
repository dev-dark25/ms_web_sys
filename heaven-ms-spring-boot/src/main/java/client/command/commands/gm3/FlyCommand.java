package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import net.server.Server;

public class FlyCommand extends Command {
    {
        setDescription("启用或禁用玩家的飞行功能。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!fly <on/off>");
            return;
        }

        Integer accid = c.getAccID();
        Server srv = Server.getInstance();
        String sendStr = "";
        if (params[0].equalsIgnoreCase("on")) {
            sendStr += "启用了飞行功能（F1）。启用飞行后，您将无法攻击。";
            if (!srv.canFly(accid)) sendStr += " 重新登录以生效。";

            srv.changeFly(c.getAccID(), true);
        } else {
            sendStr += "禁用了飞行功能。您现在可以攻击了。";
            if (srv.canFly(accid)) sendStr += " 重新登录以生效。";

            srv.changeFly(c.getAccID(), false);
        }

        player.dropMessage(6, sendStr);
    }
}