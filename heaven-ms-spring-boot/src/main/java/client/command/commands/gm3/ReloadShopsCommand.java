package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import server.MapleShopFactory;

public class ReloadShopsCommand extends Command {
    {
        setDescription("重新加载商店脚本。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        // 重新加载商店脚本
        MapleShopFactory.getInstance().reloadShops();
    }
}