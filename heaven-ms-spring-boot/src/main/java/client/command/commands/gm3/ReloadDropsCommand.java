package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.life.MapleMonsterInformationProvider;

public class ReloadDropsCommand extends Command {
    {
        setDescription("重新加载怪物掉落数据。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        // 清除怪物掉落数据缓存
        MapleMonsterInformationProvider.getInstance().clearDrops();

        player.dropMessage(5, "怪物掉落数据已重新加载。");
    }
}