package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import tools.MaplePacketCreator;

/**
 * 执行"TravelRateCommand"操作的GM命令
 * Author: Arthur L
 */
public class TravelRateCommand extends Command {
    {
        setDescription("设置旅行速度倍率。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!travelrate <newrate>");
            return;
        }

        // 解析输入参数
        int travelrate = Math.max(Integer.parseInt(params[0]), 1);

        // 设置旅行速度倍率
        c.getWorldServer().setTravelRate(travelrate);

        // 广播通知服务器
        c.getWorldServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Rate] Travel Rate has been changed to " + travelrate + "x."));
    }
}