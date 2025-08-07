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
import server.maps.MaplePortal;
import server.maps.MapleMap;

public class JailCommand extends Command {
    {
        setDescription("将玩家关进监狱");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!jail <玩家ID> [<分钟(默认5分钟)>]");
            return;
        }

        int minutesJailed = 5;
        if (params.length >= 2) {
            minutesJailed = Integer.valueOf(params[1]);
            if (minutesJailed <= 0) {
                player.yellowMessage("语法：!jail <玩家ID> [<分钟(默认5分钟)>]");
                return;
            }
        }

        MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
        if (victim != null) {
            victim.addJailExpirationTime(minutesJailed * 60 * 1000);

            int mapid = 300000012;

            if (victim.getMapId() != mapid) {    // those gone to jail won't be changing map anyway
                MapleMap target = c.getChannelServer().getMapFactory().getMap(mapid);
                MaplePortal targetPortal = target.getPortal(0);
                victim.saveLocationOnWarp();
                victim.changeMap(target, targetPortal);
                player.message(victim.getName() + " 被GM监禁 " + minutesJailed + " 分钟.");
            } else {
                player.message(victim.getName() + "'监禁时间被延长了 " + minutesJailed + " 分钟.");
            }

        } else {
            player.message("玩家 '" + params[0] + "' 不存在");
        }
    }
}
