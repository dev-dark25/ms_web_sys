package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import tools.MaplePacketCreator;

public class NpcCommand extends Command {
    {
        setDescription("生成NPC并放置在玩家位置。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("语法：#r!npc <npcid>#k");
            return;
        }
        // 获取NPC的ID
        int npcId = Integer.parseInt(params[0]);
        // 根据NPC的ID创建NPC对象
        MapleNPC npc = MapleLifeFactory.getNPC(npcId);
        if (npc != null) {
            // 设置NPC的位置为玩家的位置
            npc.setPosition(player.getPosition());
            npc.setCy(player.getPosition().y);
            // 设置NPC的X范围
            npc.setRx0(player.getPosition().x + 50);
            npc.setRx1(player.getPosition().x - 50);
            // 设置NPC的Foothold
            npc.setFh(player.getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
            // 将NPC添加到地图上
            player.getMap().addMapObject(npc);
            // 向地图上的所有玩家广播NPC的生成信息
            player.getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc));
        }
    }
}