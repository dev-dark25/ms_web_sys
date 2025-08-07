package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import net.server.Server;
import tools.MaplePacketCreator;

public class NoticeCommand extends Command {
    {
        setDescription("发送服务器公告。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        // 获取命令参数中的公告消息
        String noticeMessage = player.getLastCommandMessage();
        // 向当前服务器的所有玩家发送公告消息
        Server.getInstance().broadcastMessage(c.getWorld(), MaplePacketCreator.serverNotice(6, "[公告] " + noticeMessage));
    }
}