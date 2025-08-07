package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMap;

import java.util.Collection;

public class WarpMapCommand extends Command {
    {
        setDescription("将当前地图中的所有玩家传送到指定地图。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!warpmap <mapid>");
            return;
        }

        try {
            MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(params[0]));
            if (target == null) {
                player.yellowMessage("地图 ID " + params[0] + " 无效。");
                return;
            }

            // 获取当前地图中的所有玩家
            Collection<MapleCharacter> characters = player.getMap().getAllPlayers();

            // 将每个玩家保存位置并传送到指定地图的随机玩家刷新点
            for (MapleCharacter victim : characters) {
                victim.saveLocationOnWarp();
                victim.changeMap(target, target.getRandomPlayerSpawnpoint());
            }
        } catch (Exception ex) {
            player.yellowMessage("地图 ID " + params[0] + " 无效。");
        }
    }
}