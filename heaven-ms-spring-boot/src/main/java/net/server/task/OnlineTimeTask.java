package net.server.task;

import client.MapleCharacter;
import net.server.world.World;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class OnlineTimeTask extends BaseTask implements Runnable {
    public OnlineTimeTask(World world) {
        super(world);
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        long dailyStart = LocalDateTime.now()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        for (MapleCharacter character : wserv.getPlayerStorage().getAllCharacters()) {
            // 如果当前执行时间和上一次相差3s内，则考虑误差。如果相差3s上，则认为是重新登录了，不考虑误差
            long lastStatTime = character.getLastStatTime();
            if (lastStatTime <= dailyStart) {
                character.setOnlineTime(0);
            } else if (currentTime - lastStatTime > 3000) {
                character.setOnlineTime(character.getOnlineTime() + 1000);
            } else {
                character.setOnlineTime(character.getOnlineTime() + (currentTime - character.getLastStatTime()));
            }
            character.setLastStatTime(currentTime);
        }
    }
}
