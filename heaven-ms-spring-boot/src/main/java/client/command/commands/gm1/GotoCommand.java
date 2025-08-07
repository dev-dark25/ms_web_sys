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
package client.command.commands.gm1;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.FieldLimit;
import server.maps.MapleMiniDungeonInfo;

public class GotoCommand extends Command {

    {
        setDescription("移动到指定地图");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.getAbstractPlayerInteraction().npcTalk(9000020, "输入: #b@goto <地图id>#k，可到达指定地图");
            return;
        }
        if (!player.isAlive()) {
            player.dropMessage(1, "死亡时不能使用该指令");
            return;
        }
        if (!player.isGM()) {
            if (player.getEventInstance() != null || MapleMiniDungeonInfo.isDungeonMap(player.getMapId()) || FieldLimit.CANNOTMIGRATE.check(player.getMap().getFieldLimit())) {
                player.dropMessage(1, "你所处的位置不能使用该指令");
                return;
            }
        }
        player.changeMap(Integer.parseInt(params[0]), 0);
    }
}
