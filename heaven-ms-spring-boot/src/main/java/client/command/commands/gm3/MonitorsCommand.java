package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import tools.MapleLogger;

public class MonitorsCommand extends Command {
    {
        setDescription("列出当前正在被监视的玩家。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        for (Integer cid : MapleLogger.monitored) {
            player.yellowMessage(MapleCharacter.getNameById(cid) + " 正在被监视。");
        }
    }
}