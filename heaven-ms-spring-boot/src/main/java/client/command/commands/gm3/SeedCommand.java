package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import client.inventory.Item;

import java.awt.*;

public class SeedCommand extends Command {
    {
        setDescription("在HPQ地图中生成种子物品。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        // 检查玩家所在地图是否为HPQ地图
        if (player.getMapId() != 910010000) {
            player.yellowMessage("该命令只能在 HPQ 中使用。");
            return;
        }

        // 指定生成种子物品的位置和物品ID
        Point pos[] = {new Point(7, -207), new Point(179, -447), new Point(-3, -687), new Point(-357, -687), new Point(-538, -447), new Point(-359, -207)};
        int seed[] = {4001097, 4001096, 4001095, 4001100, 4001099, 4001098};

        // 在指定位置生成种子物品
        for (int i = 0; i < pos.length; i++) {
            Item item = new Item(seed[i], (byte) 0, (short) 1);
            player.getMap().spawnItemDrop(player, player, item, pos[i], false, true);
            try {
                Thread.sleep(100); // 每个种子生成之间暂停一段时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}