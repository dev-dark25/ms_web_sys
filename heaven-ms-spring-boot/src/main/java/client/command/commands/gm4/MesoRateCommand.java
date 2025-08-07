package client.command.commands.gm4;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import tools.MaplePacketCreator;

/**
 * 执行"MesoRate"（金币倍率）的GM命令
 * Author: Arthur L
 */
public class MesoRateCommand extends Command {
    {
        setDescription("更改服务器的金币倍率。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!mesorate <newrate>");
            return;
        }

        // 获取新的金币倍率
        int mesorate = Math.max(Integer.parseInt(params[0]), 1);
        // 设置服务器的金币倍率
        c.getWorldServer().setMesoRate(mesorate);
        // 广播通知金币倍率已更改
        c.getWorldServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Rate] 金币倍率已更改为 " + mesorate + "x."));
    }
}