/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import config.CommonConfig;

public class SupplyRateCouponCommand extends Command {
    {
        setDescription("允许/禁止现金商店出售利率券");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.dropMessage(5, "语法：!supplyratecoupon <yes|no>");
            return;
        }
        
        CommonConfig.config.server.useSupplyRateCoupons = params[0].compareToIgnoreCase("no") != 0;
        player.dropMessage(5, "利率券现在 " + (CommonConfig.config.server.useSupplyRateCoupons ? "在" : "不在") + " 现金商店中出售。");
    }
}
