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
   @Author: Ronan
*/
package client.command.commands.gm0;

import client.command.Command;
import client.MapleCharacter;
import client.MapleClient;
import config.CommonConfig;
import server.maps.MapleMap;

public class MapOwnerClaimCommand extends Command {
    {
        setDescription("查询地图归属");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        if (c.tryacquireClient()) {
            try {
                MapleCharacter chr = c.getPlayer();

                if (CommonConfig.config.server.useMapOwnershipSystem) {
                    if (chr.getEventInstance() == null) {
                        MapleMap map = chr.getMap();
                        String name = map.getMapOwnerName();
                        if (map.countBosses() == 0) {   // thanks Conrad for suggesting bosses prevent map leasing
//                            MapleMap ownedMap = chr.getOwnedMap();  // thanks Conrad for suggesting not unlease a map as soon as player exits it
                            if(name != null) {
                                chr.getMap().dropMessage(6, "当前地图‘" + name + "’玩家已持续狩猎" + map.getMapOwnerAttackTime() + "，请与归属者友好协商，共同狩猎");
                            } else {
                                chr.getMap().dropMessage(6, "当前地图为无归属状态");
                            }
                        } else {
                            chr.getMap().dropMessage(6, "此处为BOSS刷新地图，没有归属权");
                        }
                    } else {
                        chr.getMap().dropMessage(6, "此地图无归属功能");
                    }
                } else {
                    chr.getMap().dropMessage(6, "暂未开启地图归属功能");
                }
            } finally {
                c.releaseClient();
            }
        }
    }
}
