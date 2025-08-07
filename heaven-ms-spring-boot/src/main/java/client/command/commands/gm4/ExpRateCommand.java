package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import tools.MaplePacketCreator;

/**
 * 更改游戏服务器的经验倍率的GM命令
 * Author: Arthur L
 */
public class ExpRateCommand extends Command {
    {
        setDescription("更改游戏服务器的经验倍率。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!exprate <newrate>");
            return;
        }

        // 获取新的经验倍率并确保它不小于1
        int exprate = Math.max(Integer.parseInt(params[0]), 1);

        // 设置游戏服务器的经验倍率
        c.getWorldServer().setExpRate(exprate);

        // 广播消息通知所有玩家经验倍率的变化
        c.getWorldServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Rate] 经验倍率已更改为 " + exprate + "x。"));
    }
}