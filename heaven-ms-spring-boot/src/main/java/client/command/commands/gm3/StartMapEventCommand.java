package client.command.commands.gm3;

import client.MapleClient;
import client.command.Command;

public class StartMapEventCommand extends Command {
    {
        setDescription("在当前地图启动一个事件。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        // 调用玩家所在地图的startEvent方法
        c.getPlayer().getMap().startEvent(c.getPlayer());
    }
}