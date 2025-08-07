package client.command.commands.gm2;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import tools.MaplePacketCreator;

public class UnBugCommand extends Command {
    {
        setDescription("解决地图中的卡bug问题。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.enableActions());
    }
}