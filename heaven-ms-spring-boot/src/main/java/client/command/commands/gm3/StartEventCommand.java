package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import net.server.Server;
import server.events.gm.MapleEvent;
import tools.MaplePacketCreator;

public class StartEventCommand extends Command {
    {
        setDescription("在当前地图启动一个事件，可以设置参与玩家数量。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        int players = 50; // 默认事件玩家数量
        if (params.length > 1)
            players = Integer.parseInt(params[0]);
        // 设置当前频道的事件
        c.getChannelServer().setEvent(new MapleEvent(player.getMapId(), players));
        // 广播事件消息
        Server.getInstance().broadcastMessage(c.getWorld(), MaplePacketCreator.earnTitleMessage(
                "[Event] 一个事件已经在 "
                        + player.getMap().getMapName()
                        + " 启动，允许 "
                        + players
                        + " 名玩家参与。输入 @joinevent 参与。"));
        Server.getInstance().broadcastMessage(c.getWorld(),
                MaplePacketCreator.serverNotice(6, "[Event] 一个事件已经在 "
                        + player.getMap().getMapName()
                        + " 启动，允许 "
                        + players
                        + " 名玩家参与。输入 @joinevent 参与。"));
    }
}