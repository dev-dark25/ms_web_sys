package client.command.commands.gm3;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.quest.MapleQuest;

public class QuestCompleteCommand extends Command {
    {
        setDescription("强制完成指定任务。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        if (params.length < 1){
            player.yellowMessage("语法：!completequest <questid>");
            return;
        }

        int questId = Integer.parseInt(params[0]);

        if (player.getQuestStatus(questId) == 1) {
            MapleQuest quest = MapleQuest.getInstance(questId);
            if (quest != null && quest.getNpcRequirement(true) != -1) {
                // 如果任务有NPC要求，强制完成任务，并指定相关NPC
                c.getAbstractPlayerInteraction().forceCompleteQuest(questId, quest.getNpcRequirement(true));
            } else {
                // 否则，只强制完成任务
                c.getAbstractPlayerInteraction().forceCompleteQuest(questId);
            }

            player.dropMessage(5, "任务 " + questId + " 已完成。");
        } else {
            player.dropMessage(5, "任务ID " + questId + " 未启动或已完成。");
        }
    }
}