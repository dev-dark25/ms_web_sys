/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

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
package client;

public enum MapleJob {
    BEGINNER(0, "新手"),

    WARRIOR(100, "战士"),
    FIGHTER(110, "剑客"), CRUSADER(111, "勇士"), HERO(112, "英雄"),
    PAGE(120, "准骑士"), WHITEKNIGHT(121, "骑士"), PALADIN(122, "圣骑士"),
    SPEARMAN(130, "枪战士"), DRAGONKNIGHT(131, "龙骑士"), DARKKNIGHT(132, "黑骑士"),

    MAGICIAN(200, "魔法师"),
    FP_WIZARD(210, "火毒法师"), FP_MAGE(211, "火毒巫师"), FP_ARCHMAGE(212, "火毒魔导师"),
    IL_WIZARD(220, "冰雷法师"), IL_MAGE(221, "冰雷巫师"), IL_ARCHMAGE(222, "冰雷魔导师"),
    CLERIC(230, "牧师"), PRIEST(231, "祭司"), BISHOP(232, "主教"),

    BOWMAN(300, "弓箭手"),
    HUNTER(310, "猎手"), RANGER(311, "射手"), BOWMASTER(312, "神射手"),
    CROSSBOWMAN(320, "弩弓手"), SNIPER(321, "游侠"), MARKSMAN(322, "箭神"),

    THIEF(400, "飞侠"),
    ASSASSIN(410, "刺客"), HERMIT(411, "无影人"), NIGHTLORD(412, "隐士"),
    BANDIT(420, "侠客"), CHIEFBANDIT(421, "独行侠"), SHADOWER(422, "侠盗"),

    PIRATE(500, "海盗"),
    BRAWLER(510, "拳手"), MARAUDER(511, "斗士"), BUCCANEER(512, "冲锋队长"),
    GUNSLINGER(520, "火枪手"), OUTLAW(521, "大副"), CORSAIR(522, "船长"),

    MAPLELEAF_BRIGADIER(800, "管理员"),
    GM(900, "GM"), SUPERGM(910, "SGM"),

    NOBLESSE(1000, "初心者"),
    DAWNWARRIOR1(1100, "魂骑士1"), DAWNWARRIOR2(1110, "魂骑士2"), DAWNWARRIOR3(1111, "魂骑士3"), DAWNWARRIOR4(1112, "魂骑士4"),
    BLAZEWIZARD1(1200, "炎术士1"), BLAZEWIZARD2(1210, "炎术士2"), BLAZEWIZARD3(1211, "炎术士3"), BLAZEWIZARD4(1212, "炎术士4"),
    WINDARCHER1(1300, "风灵使者1"), WINDARCHER2(1310, "风灵使者2"), WINDARCHER3(1311, "风灵使者3"), WINDARCHER4(1312, "风灵使者4"),
    NIGHTWALKER1(1400, "夜行者1"), NIGHTWALKER2(1410, "夜行者2"), NIGHTWALKER3(1411, "夜行者3"), NIGHTWALKER4(1412, "夜行者4"),
    THUNDERBREAKER1(1500, "奇袭者1"), THUNDERBREAKER2(1510, "奇袭者2"), THUNDERBREAKER3(1511, "奇袭者3"), THUNDERBREAKER4(1512, "奇袭者4"),

    LEGEND(2000, "战童"), EVAN(2001, "EVAN"),
    ARAN1(2100, "战神1"), ARAN2(2110, "战神2"), ARAN3(2111, "战神3"), ARAN4(2112, "战神4"),

    EVAN1(2200, "EVAN1"), EVAN2(2210, "EVAN2"), EVAN3(2211, "EVAN3"), EVAN4(2212, "EVAN4"), EVAN5(2213, "EVAN5"), EVAN6(2214, "EVAN6"),
    EVAN7(2215, "EVAN7"), EVAN8(2216, "EVAN8"), EVAN9(2217, "EVAN9"), EVAN10(2218, "EVAN10");

    private final int jobid;
    private final String name;
    private final static int maxId = 22;    // maxId = (EVAN / 100);
    
    private MapleJob(int id, String name) {
        this.jobid = id;
        this.name = name;
    }
    
    public static int getMax() {
        return maxId;
    }

    public int getId() {
        return jobid;
    }

    public String getName() {
        return name;
    }

    public static MapleJob getById(int id) {
        for (MapleJob l : MapleJob.values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }

    public static MapleJob getBy5ByteEncoding(int encoded) {
        switch (encoded) {
            case 2:
                return WARRIOR;
            case 4:
                return MAGICIAN;
            case 8:
                return BOWMAN;
            case 16:
                return THIEF;
            case 32:
                return PIRATE;
            case 1024:
                return NOBLESSE;
            case 2048:
                return DAWNWARRIOR1;
            case 4096:
                return BLAZEWIZARD1;
            case 8192:
                return WINDARCHER1;
            case 16384:
                return NIGHTWALKER1;
            case 32768:
                return THUNDERBREAKER1;
            default:
                return BEGINNER;
        }
    }
    
    public boolean isA(MapleJob basejob) {  // thanks Steve (kaito1410) for pointing out an improvement here
        int basebranch = basejob.getId() / 10;
        return (getId() / 10 == basebranch && getId() >= basejob.getId()) || (basebranch % 10 == 0 && getId() / 100 == basejob.getId() / 100);
    }
    
    public int getJobNiche() {
        return (jobid / 100) % 10;
        
        /*
        case 0: BEGINNER;
        case 1: WARRIOR;
        case 2: MAGICIAN;
        case 3: BOWMAN;  
        case 4: THIEF;
        case 5: PIRATE;
        */
    }
}
