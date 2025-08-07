package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import tools.MaplePacketCreator;

/**
 * 更改游戏服务器的掉落率的GM命令
 * Author: Arthur L
 */
public class DropRateCommand extends Command {
    {
        setDescription("更改游戏服务器的掉落率。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!droprate <newrate>");
            return;
        }

        // 获取新的掉落率并确保它不小于1
        int droprate = Math.max(Integer.parseInt(params[0]), 1);

        // 设置游戏服务器的掉落率
        c.getWorldServer().setDropRate(droprate);

        // 广播消息通知所有玩家掉落率的变化
        c.getWorldServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Rate] 掉落率已更改为 " + droprate + "x。"));
    }
}