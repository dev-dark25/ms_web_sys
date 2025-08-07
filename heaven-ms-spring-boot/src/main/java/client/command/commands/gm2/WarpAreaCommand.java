package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMap;

import java.awt.*;
import java.util.Collection;

public class WarpAreaCommand extends Command {
    {
        setDescription("将附近的玩家传送到指定地图。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!warparea <mapid>");
            return;
        }

        try {
            MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(params[0]));
            if (target == null) {
                player.yellowMessage("地图 ID " + params[0] + " 无效。");
                return;
            }

            Point pos = player.getPosition();

            Collection<MapleCharacter> characters = player.getMap().getAllPlayers();

            for (MapleCharacter victim : characters) {
                if (victim.getPosition().distanceSq(pos) <= 50000) {
                    victim.saveLocationOnWarp();
                    victim.changeMap(target, target.getRandomPlayerSpawnpoint());
                }
            }
        } catch (Exception ex) {
            player.yellowMessage("地图 ID " + params[0] + " 无效。");
        }
    }
}