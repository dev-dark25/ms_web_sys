package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import net.server.Server;
import tools.MaplePacketCreator;

public class RipCommand extends Command {
    {
        setDescription("发送RIP消息。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        // 从参数中获取要发送的消息内容
        String message = joinStringFrom(params, 1);

        // 使用Server.getInstance().broadcastMessage来广播RIP消息
        Server.getInstance().broadcastMessage(c.getWorld(), MaplePacketCreator.serverNotice(6, "[RIP]: " + message));
    }
}