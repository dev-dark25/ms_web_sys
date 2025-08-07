package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class EndEventCommand extends Command {
    {
        setDescription("结束服务器上正在进行的事件。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        c.getChannelServer().setEvent(null);
        player.dropMessage(5, "您已结束事件。不再允许更多玩家加入。");
    }
}