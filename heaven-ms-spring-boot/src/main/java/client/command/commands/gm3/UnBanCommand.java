package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import tools.MysqlConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UnBanCommand extends Command {
    {
        setDescription("解除封禁某个玩家。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：!unban <playername>");
            return;
        }

        try {
            // 获取数据库连接
            Connection con = MysqlConnection.getConnection();

            // 通过玩家名称获取账号ID
            int aid = MapleCharacter.getAccountIdByName(params[0]);

            // 更新账号表中的封禁状态
            PreparedStatement p = con.prepareStatement("UPDATE accounts SET banned = -1 WHERE id = " + aid);
            p.executeUpdate();

            // 解除IP封禁
            p = con.prepareStatement("DELETE FROM ipbans WHERE aid = " + aid);
            p.executeUpdate();

            // 解除MAC封禁
            p = con.prepareStatement("DELETE FROM macbans WHERE aid = " + aid);
            p.executeUpdate();

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            player.message("解除封禁 " + params[0] + " 失败。");
            return;
        }
        player.message("已解除封禁 " + params[0] + "。");
    }
}