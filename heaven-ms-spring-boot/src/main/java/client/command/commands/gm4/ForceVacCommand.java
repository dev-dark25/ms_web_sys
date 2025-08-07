package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import client.inventory.MaplePet;
import client.inventory.manipulator.MapleInventoryManipulator;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.MaplePacketCreator;

import java.util.Arrays;
import java.util.List;

/**
 * 实现强制吸物（Force Vac）的GM命令
 * Author: Arthur L
 */
public class ForceVacCommand extends Command {
    {
        setDescription("强制吸取地图上的所有物品。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        List<MapleMapObject> items = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.ITEM));

        // 遍历地图上的物品
        for (MapleMapObject item : items) {
            MapleMapItem mapItem = (MapleMapItem) item;

            mapItem.lockItem();
            try {
                // 如果物品已被拾取，则继续下一个物品
                if (mapItem.isPickedUp()) continue;

                // 处理不同类型的物品
                if (mapItem.getMeso() > 0) {
                    // 如果是金币，将金币添加到玩家的金币总数中
                    player.gainMeso(mapItem.getMeso(), true);
                } else if (player.applyConsumeOnPickup(mapItem.getItemId())) {
                    // 如果是消耗品，应用该物品效果（例如药水）
                } else if (mapItem.getItemId() == 4031865 || mapItem.getItemId() == 4031866) {
                    // 如果是点卷道具，将点卷添加到玩家的点卷账户中
                    player.getCashShop().gainCash(1, mapItem.getItemId() == 4031865 ? 100 : 250);
                } else if (mapItem.getItem().getItemId() >= 5000000 && mapItem.getItem().getItemId() <= 5000100) {
                    // 如果是宠物道具，创建宠物并添加到玩家的物品栏中
                    int petId = MaplePet.createPet(mapItem.getItem().getItemId());
                    if (petId == -1) {
                        continue;
                    }
                    MapleInventoryManipulator.addById(c, mapItem.getItem().getItemId(), mapItem.getItem().getQuantity(), null, petId);
                } else if (MapleInventoryManipulator.addFromDrop(c, mapItem.getItem(), true)) {
                    // 如果是其他类型的物品，将其添加到玩家的物品栏中
                    if (mapItem.getItemId() == 4031868) {
                        player.updateAriantScore();
                    }
                }

                // 通知地图上的其他玩家物品已被拾取，并将物品从地图上移除
                player.getMap().pickItemDrop(MaplePacketCreator.removeItemFromMap(mapItem.getObjectId(), 2, player.getId()), mapItem);
            } finally {
                mapItem.unlockItem();
            }
        }
    }
}