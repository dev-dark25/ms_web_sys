package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import net.server.Server;
import tools.MapleLogger;
import tools.MaplePacketCreator;

public class MonitorCommand extends Command {
    {
        setDescription("监视或停止监视玩家的活动。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!monitor <玩家名>");
            return;
        }
        MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
        if (victim == null) {
            player.message("找不到玩家 '" + params[0] + "' 在这个游戏世界中。");
            return;
        }
        boolean monitored = MapleLogger.monitored.contains(victim.getId());
        if (monitored) {
            MapleLogger.monitored.remove(victim.getId());
        } else {
            MapleLogger.monitored.add(victim.getId());
        }
        player.yellowMessage(victim.getName() + " " + (!monitored ? "现在正在被监视。" : "不再被监视。"));
        String message = player.getName() + (!monitored ? "开始监视" : "停止监视") + victim.getName() + "。";
        Server.getInstance().broadcastGMMessage(c.getWorld(), MaplePacketCreator.serverNotice(5, message));

    }
}