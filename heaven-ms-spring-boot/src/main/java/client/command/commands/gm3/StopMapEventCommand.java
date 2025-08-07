package client.command.commands.gm3;

import client.MapleClient;
import client.command.Command;

public class StopMapEventCommand extends Command {
    {
        setDescription("停止当前地图上正在进行的事件。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        // 设置当前地图的事件状态为未开始
        c.getPlayer().getMap().setEventStarted(false);
    }
}