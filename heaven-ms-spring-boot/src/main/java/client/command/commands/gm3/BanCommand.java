package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import net.server.Server;
import server.TimerManager;
import tools.MysqlConnection;
import tools.MaplePacketCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BanCommand extends Command {
    {
        setDescription("禁止玩家访问服务器并记录原因。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 2) {
            player.yellowMessage("语法：!ban <玩家名字> <原因> (请提供详细的原因)");
            return;
        }
        String ign = params[0];
        String reason = joinStringFrom(params, 1);
        MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(ign);
        if (target != null) {
            String readableTargetName = MapleCharacter.makeMapleReadable(target.getName());
            String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
            //禁止IP
            PreparedStatement ps = null;
            try {
                Connection con = MysqlConnection.getConnection();
                if (ip.matches("[0-9]{1,3}\\..*")) {
                    ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?, ?)");
                    ps.setString(1, ip);
                    ps.setString(2, String.valueOf(target.getClient().getAccID()));

                    ps.executeUpdate();
                    ps.close();
                }

                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                c.getPlayer().message("禁止IP地址时发生错误");
                c.getPlayer().message(target.getName() + "的IP未被禁止: " + ip);
            }
            target.getClient().banMacs();
            reason = c.getPlayer().getName() + " 禁止了 " + readableTargetName + " 原因是 " + reason + " (IP: " + ip + ") " + "(MAC: " + c.getMacs() + ")";
            target.ban(reason);
            target.yellowMessage("你已被 #b" + c.getPlayer().getName() + " #k 禁止访问服务器。");
            target.yellowMessage("禁止原因: " + reason);
            c.announce(MaplePacketCreator.getGMEffect(4, (byte) 0));
            final MapleCharacter rip = target;
            TimerManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    rip.getClient().disconnect(false, false);
                }
            }, 5000); //5 秒
            Server.getInstance().broadcastMessage(c.getWorld(), MaplePacketCreator.serverNotice(6, "[RIP]: " + ign + " 已被禁止。"));
        } else if (MapleCharacter.ban(ign, reason, false)) {
            c.announce(MaplePacketCreator.getGMEffect(4, (byte) 0));
            Server.getInstance().broadcastMessage(c.getWorld(), MaplePacketCreator.serverNotice(6, "[RIP]: " + ign + " 已被禁止。"));
        } else {
            c.announce(MaplePacketCreator.getGMEffect(6, (byte) 1));
        }
    }
}