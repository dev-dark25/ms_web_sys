package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import scripting.portal.PortalScriptManager;

public class ReloadPortalsCommand extends Command {
    {
        setDescription("重新加载传送门脚本。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        // 重新加载传送门脚本
        PortalScriptManager.getInstance().reloadPortalScripts();

        // 提示重新加载成功
        player.dropMessage(5, "传送门脚本已重新加载。");
    }
}