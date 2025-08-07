package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import tools.MaplePacketCreator;

/**
 * 设置Boss掉落率的GM命令
 * Author: Ronan
 */
public class BossDropRateCommand extends Command {
    {
        setDescription("设置服务器的Boss掉落率。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!bossdroprate <newrate>");
            return;
        }

        // 获取新的Boss掉落率
        int bossdroprate = Math.max(Integer.parseInt(params[0]), 1);

        // 设置服务器的Boss掉落率
        c.getWorldServer().setBossDropRate(bossdroprate);

        // 广播消息通知所有玩家Boss掉落率的变化
        c.getWorldServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Rate] Boss Drop Rate已更改为 " + bossdroprate + "x。"));
    }
}