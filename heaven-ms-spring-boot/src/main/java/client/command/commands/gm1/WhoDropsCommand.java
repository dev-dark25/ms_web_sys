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
import client.command.Command;
import client.MapleClient;
import server.MapleItemInformationProvider;
import tools.MysqlConnection;
import tools.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;

public class WhoDropsCommand extends Command {
    {
        setDescription("查询物品掉落的怪物");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.dropMessage(5, "输入：@whodrops <物品名字>");
            return;
        }
        
        if (c.tryacquireClient()) {
            try {
                String searchString = player.getLastCommandMessage();
                String output = "";
                Iterator<Pair<Integer, String>> listIterator = MapleItemInformationProvider.getInstance().getItemDataByName(searchString).iterator();
                if(listIterator.hasNext()) {
                    int count = 1;
                    while(listIterator.hasNext() && count <= 3) {
                        Pair<Integer, String> data = listIterator.next();
                        output += "#b" + data.getRight() + "#k 由以下怪物掉落：\r\n";
                        try {
                            Connection con = MysqlConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement("SELECT dropperid FROM drop_data WHERE itemid = ? LIMIT 50");
                            ps.setInt(1, data.getLeft());
                            ResultSet rs = ps.executeQuery();
                            String mobStr = "";
                            while(rs.next()) {
                                int mobId = rs.getInt("dropperid");
                                mobStr += "#o" + mobId + "#、";
                            }
                            if(mobStr != ""){
//                                删除最后一个'、'
                                mobStr = mobStr.substring(0, mobStr.length() - 1);
                                output += mobStr;
                            }
                            rs.close();
                            ps.close();
                            con.close();
                        } catch (Exception e) {
                            player.dropMessage(6, "查询数据时出现问题，请再试一次。");
                            e.printStackTrace();
                            return;
                        }
                        output += "\r\n\r\n";
                        count++;
                    }
                } else {
                    player.dropMessage(5, "您搜索的物品不存在。");
                    return;
                }
                
                c.getAbstractPlayerInteraction().npcTalk(9010000, output);
            } finally {
                c.releaseClient();
            }
        } else {
            player.dropMessage(5, "请稍候，等待处理您的请求。");
        }
    }
}
