package client.command.commands.gm2;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;

public class SetStatCommand extends Command {
    {
        setDescription("设置玩家的属性点数。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!setstat <newstat>");
            return;
        }

        try {
            // 尝试将输入的参数转换为整数
            int x = Integer.parseInt(params[0]);

            // 如果输入的值大于Short.MAX_VALUE，则将其设置为Short.MAX_VALUE
            if (x > Short.MAX_VALUE) x = Short.MAX_VALUE;
                // 如果输入的值小于4，则将其设置为4，这是属性点数的最小允许值
            else if (x < 4) x = 4;

            // 更新玩家的力量、敏捷、智力和运气属性点数
            player.updateStrDexIntLuk(x);
        } catch (NumberFormatException nfe) {}
    }
}