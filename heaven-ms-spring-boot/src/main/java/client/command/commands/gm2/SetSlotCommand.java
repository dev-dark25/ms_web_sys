package client.command.commands.gm2;

import client.*;
import client.command.Command;

public class SetSlotCommand extends Command {
    {
        setDescription("设置玩家的背包槽位数量。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!setslot <newlevel>");
            return;
        }

        // 计算需要的新槽位数量（以4为步进）
        int slots = (Integer.parseInt(params[0]) / 4) * 4;
        for (int i = 1; i < 5; i++) {
            int curSlots = player.getSlots(i);
            if (slots <= -curSlots) {
                continue;
            }

            // 设置玩家的槽位数量
            player.gainSlots(i, slots - curSlots, true);
        }

        player.yellowMessage("槽位数量已更新。");
    }
}