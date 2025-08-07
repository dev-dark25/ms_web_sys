package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.maps.MapleMap;

import java.util.Collection;

public class ReloadMapCommand extends Command {
    {
        setDescription("重新加载当前地图。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        // 获取当前地图
        MapleMap newMap = c.getChannelServer().getMapFactory().resetMap(player.getMapId());
        int callerid = c.getPlayer().getId();

        // 获取当前地图上的所有角色
        Collection<MapleCharacter> characters = player.getMap().getAllPlayers();

        // 重置玩家的位置并将他们传送到新地图
        for (MapleCharacter chr : characters) {
            chr.saveLocationOnWarp();
            chr.changeMap(newMap);

            // 通知玩家他们已经被重新定位
            if (chr.getId() != callerid)
                chr.dropMessage("由于地图重新加载，您已被重新定位。对此造成的不便我们深感抱歉。");
        }

        // 重新刷新地图上的怪物和物品
        newMap.respawn();
    }
}