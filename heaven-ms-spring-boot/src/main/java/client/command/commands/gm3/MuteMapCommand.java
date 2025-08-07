package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class MuteMapCommand extends Command {
    {
        setDescription("将当前地图静音或取消静音。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (player.getMap().isMuted()) {
            player.getMap().setMuted(false);
            player.dropMessage(5, "您所在的地图已取消静音。");
        } else {
            player.getMap().setMuted(true);
            player.dropMessage(5, "您所在的地图已静音。");
        }
    }
}