/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client.autoban;

import client.MapleCharacter;
import config.CommonConfig;
import net.server.Server;
import tools.FilePrinter;
import tools.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 自动封号管理类
 * @author kevintjuh93
 */
public class AutobanManager {
    private final MapleCharacter chr;
    /**
     * 统计触发封号检测的次数[封号类型, 触发次数]
     */
    private final Map<AutobanFactory, Integer> points = new HashMap<>();
    /**
     * 记录最后一次触发封号检测的时间点
     */
    private final Map<AutobanFactory, Long> lastTime = new HashMap<>();
    /**
     * 记录连续 MISS 次数
     */
    private int misses = 0;
    /**
     * 记录最近一次记录的连续 MISS 的次数
     */
    private int lastMisses = 0;
    /**
     * 当每次要重置 MISS 次数时(受伤时重置)，将会校验 连续 MISS 次数，如果 misses 和 lastMisses 相等，计数 +1；用于简单增加容错的；
     */
    private int sameMissCount = 0;
    /**
     * 时间戳数组，记录某些行为的时间点，用于检测触发的频率是否正常，但是实际调用存在问题。
     * 每个数组元素记录一种类型的时间戳，
     * 参考{@link #setTimestamp(int, int, int) setTimestamp}方法说明
     */
    private final long[] spam = new long[20];
    /**
     * 和{@link #spam}类似
     */
    private final int[] timestamp = new int[20];
    /**
     * 配合{@link #timestamp}，用于记录触发的次数的计数器
     */
    private final byte[] timestampCounter = new byte[20];


    public AutobanManager(MapleCharacter chr) {
        this.chr = chr;
    }

    /**
     * 添加自动封号检测
     * @param fac 封号的类型
     * @param reason 自动封号的原因
     */
    public void addPoint(AutobanFactory fac, String reason) {
        if (CommonConfig.config.server.useAutoBan) {
            // GM 不封
            if (chr.isGM() || chr.isBanned()) {
                return;
            }

            if (lastTime.containsKey(fac)) {
                if (lastTime.get(fac) < (Server.getInstance().getCurrentTime() - fac.getExpire())) {
                    // 在设定的时间内如果没有触发封号，则这里简单的降低触发次数，简单的容错
                    points.put(fac, points.get(fac) / 2);
                }
            }

            // 更新最后一次记录的时间点
            if (fac.getExpire() != -1) {
                lastTime.put(fac, Server.getInstance().getCurrentTime());
            }

            if (points.containsKey(fac)) {
                // 累加触发次数
                points.put(fac, points.get(fac) + 1);
            } else {
                // 初始化触发次数 = 1
                points.put(fac, 1);
            }

            if (points.get(fac) >= fac.getMaximum()) {
                // 触发次数达到了设置的次数上限则自动封号
                chr.autoban(reason);
            }
        }
        if (CommonConfig.config.server.useAutoBanLog) {
            // 记录触发封号到日志中
            FilePrinter.print(FilePrinter.AUTOBAN_WARNING, MapleCharacter.makeMapleReadable(chr.getName()) + " 触发自动封号【" + fac.name() + "】 封号原因: " + reason);
        }
    }

    public void addMiss() {
        this.misses++;
        // 这么写是为了后续如果加参数控制，直接改成取配置就行了
        int banMissCount = 20;
        if (misses < banMissCount && misses > 6) {
            Log.warn("玩家" + chr.getName() + "miss次数超过6");
        } else if (misses > banMissCount) {
            String errMsg = "玩家" + chr.getName() + "miss次数过多";
            System.out.println(errMsg);
            if (CommonConfig.config.server.useAutoBanLog) {
                FilePrinter.print(FilePrinter.AUTOBAN_WARNING, errMsg);
            }
            chr.sendPolice("MISS次数过多，你已被管理员进行监控！");
            if (CommonConfig.config.server.useAutoBan) {
                chr.autoban("连续MISS次数超过" + banMissCount + "次");
            }
        }
    }

    /**
     * 重置连续 MISS 次数；
     * 注意，这个检测可能不靠谱，虽然用 sameMissCount 来增加一个容错，但最后还是 注释掉了自动封号
     */
    public void resetMisses() {
        if (lastMisses == misses && misses > 6) {
            sameMissCount++;
        }
        if (sameMissCount > 4) {
            chr.sendPolice("检测到你不讲武德，居然带闪，将被自动封号(不是，交闪不封)！");
            //chr.autoban("Autobanned for : " + misses + " Miss godmode", 1);
        } else if (sameMissCount > 0) {
            this.lastMisses = misses;
        }
        this.misses = 0;
    }

    //Don't use the same type for more than 1 thing

    /**
     * 记录时间戳；
     * 注意不要在数组相同的地方记录代表不同意义的时间戳；
     * @param type 数组的索引，用这个来区别每个时间戳的意义
     */
    public void spam(int type) {
        this.spam(type, Server.getInstance().getCurrentTime());
    }

    /**
     * 记录时间戳；
     * 注意不要在数组相同的地方记录代表不同意义的时间戳；
     * @param type 数组的索引，用这个来区别每个时间戳的意义
     * @param timestamp 时间戳
     */
    public void spam(int type, long timestamp) {
        this.spam[type] = timestamp;
    }

    public long getLastSpam(int type) {
        return spam[type];
    }

    /**
     * 设置并检测时间戳和相应触发的次数，将有问题的踢下线
     * <p>
     * 1: 宠物食物<br>
     * 2: 背包道具堆叠<br>
     * 3: 背包道具排序<br>
     * 4: SpecialMove<br>
     * 5: 使用捕捉道具，如费洛蒙香水<br>
     * 6: 丢道具<br>
     * 7: 聊天<br>
     * 8: 回血异常<br>
     * 9: 回蓝异常<br>
     *
     * @param type  记录的时间戳的类型
     * @param time  时间戳
     * @param times 次数
     */
    public void setTimestamp(int type, int time, int times) {
        // 这里校验的是两次的时间戳要相等，条件很苛刻
        if (this.timestamp[type] == time) {
            this.timestampCounter[type]++;
            if (this.timestampCounter[type] >= times) {
                if (CommonConfig.config.server.useAutoBan) {
                    chr.getClient().disconnect(false, false);
                }

                FilePrinter.print(FilePrinter.EXPLOITS, "玩家 " + chr.getName() + " 被捕获到频繁操作[" + type + "], 已经强制下线！.");
            }
        } else {
            this.timestamp[type] = time;
            this.timestampCounter[type] = 0;
        }
    }

    public Map getPoints() {
        return points;
    }

    public Map getLastTime() {
        return lastTime;
    }
}
