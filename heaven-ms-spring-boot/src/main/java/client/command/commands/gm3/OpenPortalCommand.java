package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class OpenPortalCommand extends Command {
    {
        setDescription("打开指定传送门。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：#r!openportal <portalid>#k");
            return;
        }
        // 获取传送门ID
        String portalId = params[0];
        // 设置传送门状态为打开
        player.getMap().getPortal(portalId).setPortalState(true);
    }
}