package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;

public class DamageStatisticsCommand extends Command {
    {
        setDescription("伤害统计");
    }

    @Override
    public void execute(MapleClient client, String[] params) {
        // -1是关闭状态，大于等于0是开启状态
        MapleCharacter player = client.getPlayer();
        if (!player.isCalcDamage()) {
            player.setTotalDamage(0);
            player.setMaxDamage(0);
            player.setMinDamage(0);
            player.message("开启伤害统计");
        } else {
            String text = "关闭伤害统计，本次统计总伤害[" + player.getTotalDamage() + "]，最大伤害[" + player.getMaxDamage() +
                    "]，最小伤害[" + player.getMinDamage() + "]";
            player.setTotalDamage(-1);
            player.setMaxDamage(-1);
            player.setMinDamage(-1);
            player.message(text);
        }
    }
}
