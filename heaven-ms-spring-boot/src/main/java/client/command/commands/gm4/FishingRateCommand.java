package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import tools.MaplePacketCreator;

/**
 * 更改游戏服务器的钓鱼概率倍率的GM命令
 * Author: Ronan
 */
public class FishingRateCommand extends Command {
    {
        setDescription("更改游戏服务器的钓鱼概率倍率。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!fishrate <newrate>");
            return;
        }

        // 获取新的钓鱼概率倍率并确保它不小于1
        int fishrate = Math.max(Integer.parseInt(params[0]), 1);

        // 设置游戏服务器的钓鱼概率倍率
        c.getWorldServer().setFishingRate(fishrate);

        // 广播消息通知所有玩家钓鱼概率倍率的变化
        c.getWorldServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Rate] 钓鱼概率倍率已更改为 " + fishrate + "x。"));
    }
}