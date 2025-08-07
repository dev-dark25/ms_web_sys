package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import net.server.Server;

public class ToggleCouponCommand extends Command {
    {
        setDescription("在服务器上切换优惠券（Coupon）的状态。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!togglecoupon <itemid>");
            return;
        }
        // 通过调用Server的toggleCoupon方法，切换指定道具的状态
        Server.getInstance().toggleCoupon(Integer.parseInt(params[0]));
    }
}