package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import net.server.Server;
import tools.MapleLogger;
import tools.MaplePacketCreator;

public class IgnoreCommand extends Command {
    {
        // 设置命令描述
        setDescription("在游戏中忽略或取消忽略指定的玩家。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!ignore <ign>");
            return;
        }
        MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
        if (victim == null) {
            player.message("玩家 '" + params[0] + "' 未在这个世界中找到。");
            return;
        }
        boolean monitored_ = MapleLogger.ignored.contains(victim.getId());
        if (monitored_) {
            MapleLogger.ignored.remove(victim.getId());
        } else {
            MapleLogger.ignored.add(victim.getId());
        }
        player.yellowMessage(victim.getName() + " " + (!monitored_ ? "现在被忽略。" : "不再被忽略。"));
        String message_ = player.getName() + (!monitored_ ? "开始忽略了 " : "停止忽略了 ") + victim.getName() + "。";
        Server.getInstance().broadcastGMMessage(c.getWorld(), MaplePacketCreator.serverNotice(5, message_));
    }
}