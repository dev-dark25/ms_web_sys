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

/*
   @Author: Arthur L - Refactored command content into modules
*/
package client.command.commands.gm2;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import config.CommonConfig;

public class ApCommand extends Command {
    {
        setDescription("增加能力点数");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!ap [<玩家名称>] <AP点>");
            return;
        }

        if (params.length < 2) {
            int newAp = Integer.parseInt(params[0]);
            if (newAp < 0) newAp = 0;
            else if (newAp > CommonConfig.config.server.maxAp) newAp = CommonConfig.config.server.maxAp;

            player.changeRemainingAp(newAp, false);
        } else {
            MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
            if (victim != null) {
                int newAp = Integer.parseInt(params[1]);
                if (newAp < 0) newAp = 0;
                else if (newAp > CommonConfig.config.server.maxAp) newAp = CommonConfig.config.server.maxAp;
                victim.changeRemainingAp(newAp, false);
                player.dropMessage(5, "给与玩家'"+params[0]+"' "+newAp+"' 点SP");
            } else {
                player.message("玩家 '" + params[0] + "' 不存在");
            }
        }
    }
}
