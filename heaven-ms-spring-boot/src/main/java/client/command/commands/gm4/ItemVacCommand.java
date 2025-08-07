package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

import java.util.Arrays;
import java.util.List;

/**
 * 执行"ItemVac"（物品吸取）的GM命令
 * Author: Arthur L
 */
public class ItemVacCommand extends Command {
    {
        setDescription("吸取玩家周围的所有物品。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        // 获取玩家周围的所有物品
        List<MapleMapObject> list = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.ITEM));

        // 遍历物品列表并将它们吸取到玩家的物品栏
        for (MapleMapObject item : list) {
            player.pickupItem(item);
        }
    }
}