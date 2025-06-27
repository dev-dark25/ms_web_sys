package com.example.web.component;

import com.example.web.mapper.primary.AccountInfoMapper;
import com.example.web.mapper.primary.GameResourceMapper;
import com.example.web.mapper.primary.ResourceRecordMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Component
public class ExtractResourceUtil {

    @Resource
    private GameResourceMapper gameResourceMapper;

    @Resource
    private AccountInfoMapper accountInfoMapper;

    @Resource
    private ResourceRecordMapper resourceRecordMapper;

    Random random = new Random();

    // 五星基础概率
    private static double probability5 = 0.006;
    // 五星递增概率
    private static double probability5add = 0.06;
    // 五星概率递增初始值
    private static double count5 = 73;
    // 四星基础概率
    private static double probability4 = 0.0255;
    // 四星递增概率
    private static double probability4add = 0.5;
    // 四星概率递增初始值
    private static double count4 = 8;

    // 常驻抽取次数
    private int permanentCount = 0;
    // 获取当前距上个五星已抽取次数
    private int permanentCurrentCount5 = 0;
    // 获取当前距上个四星已抽取次数
    private int permanentCurrentCount4 = 0;

    // UP角色抽取次数
    private int UPCount = 0;
    // 获取当前距上个UP五星角色已抽取次数
    private int UPCurrentCount5 = 0;
    // 获取当前距上个UP四星角色已抽取次数
    private int UPCurrentCount4 = 0;
    // 上个五星是否是UP角色
    private int UPCharacter5 = 0;   // 0-默认值；1-是；2-否
    // 上个四星是否是UP角色
    private int UPCharacter4 = 0;   // 0-默认值；1-是；2-否

    // 限定四/五星角色固定概率
    private double UPCharacterProbability = 0.5;

    // 常驻五星角色资源列表
    private List<Map> permanentFiveStarList = null;
    // 常驻四星资源列表
    private List<Map> permanentFourStarList = null;
    // 常驻三星资源列表
    private List<Map> permanentThreeStarList = null;
    // 当期UP五星角色资源列表
    private List<Map> timeLimitFiveStarList = null;
    // 当期UP四星角色资源列表
    private List<Map> timeLimitFourStarList = null;

    // 多次抽取
    public List getCompleteResources(Map map) {
        // 获取默认资源
        initResource(map);

        List<Map> result = new ArrayList();
        List<String> list = getResources(map);
        for (String i : list) {
            map.put("starLever", i);
            result.add(getStarResource(map));
        }
        updateResource(map, result);
        return result;
    }

    @Transactional
    public void updateResource(Map map, List list) {
        map.put("permanentCount", permanentCount);
        map.put("permanentCurrentCount5", permanentCurrentCount5);
        map.put("permanentCurrentCount4", permanentCurrentCount4);
        map.put("UPCount", UPCount);
        map.put("UPCurrentCount5", UPCurrentCount5);
        map.put("UPCurrentCount4", UPCurrentCount4);
        map.put("UPCharacter5", UPCharacter5);
        map.put("UPCharacter4", UPCharacter4);
        accountInfoMapper.updateById(map);
        resourceRecordMapper.batchInsert((Integer) map.get("id"), list);
    }

    private void initResource(Map map) {
        Map accountMap = accountInfoMapper.selectById(map); // 从缓存中获取   TODO
        permanentCount = (int) accountMap.get("permanent_count");
        permanentCurrentCount5 = (int) accountMap.get("permanent_current_count_5");
        permanentCurrentCount4 = (int) accountMap.get("permanent_current_count_4");
        UPCount = (int) accountMap.get("up_count");
        UPCurrentCount5 = (int) accountMap.get("up_current_count_5");
        UPCurrentCount4 = (int) accountMap.get("up_current_count_4");
        UPCharacter5 = (int) accountMap.get("up_character_5");
        UPCharacter4 = (int) accountMap.get("up_character_4");

        // 从缓存中获取   TODO
        Map<String, Object> paramterMap = new HashMap();
        paramterMap.put("type", "0");
        paramterMap.put("timeLimit", "1");
        paramterMap.put("up", "1");
        paramterMap.put("star", "5");
        paramterMap.put("timeLimitType", map.get("limitedType"));
        timeLimitFiveStarList = gameResourceMapper.selectList(paramterMap);
        paramterMap.remove("timeLimitType");

        paramterMap.put("timeLimit", "0");
        paramterMap.put("star", "4");
        timeLimitFourStarList = gameResourceMapper.selectList(paramterMap);

        paramterMap.put("up", "0");
        paramterMap.put("star", "5");
        permanentFiveStarList = gameResourceMapper.selectList(paramterMap);

        paramterMap.put("star", "4");
        permanentFourStarList = gameResourceMapper.selectList(paramterMap);

        paramterMap.put("type", "1");
        paramterMap.put("star", "3");
        permanentThreeStarList = gameResourceMapper.selectList(paramterMap);
    }

    // 获取实际资源
    private Map getStarResource(Map map) {
        List<Map> starList = null;
//        String type = (String) map.get("type");    // 0-角色；1-武器
        String starLever = (String) map.get("starLever");
        if ("5".equals(starLever)) {
            starList = getStarResourceList(map, starLever, UPCharacter5);
        } else if ("4".equals(starLever)) {
            starList = getStarResourceList(map, starLever, UPCharacter4);
        } else {
            // 获取三星资源列表
            starList = permanentThreeStarList;
        }
        return starList.get(random.nextInt(starList.size()));
    }

    // 获取实际资源列表
    private List getStarResourceList(Map map, String starLever, int ifLastResourceIsUP) {
        String limitedType = (String) map.get("limitedType");   // 0-限定池1；1-限定池2
        if (limitedType == null || "".equals(limitedType)) {
            return "5".equals(starLever) ? permanentFiveStarList : permanentFourStarList;
        }
        System.out.println(ifLastResourceIsUP);
        if (ifLastResourceIsUP == 0 || ifLastResourceIsUP == 1) {
            if (!limitedType.isEmpty() && random.nextDouble() < UPCharacterProbability) {
                System.out.println("命中UP");
                // 抽取限定池，且命中当期UP
                UPCharacter5 = "5".equals(starLever) ? 1 : UPCharacter5;
                UPCharacter4 = "4".equals(starLever) ? 1 : UPCharacter4;
                return "5".equals(starLever) ? timeLimitFiveStarList : timeLimitFourStarList;
            } else {
                System.out.println("未命中UP");
                // 获取常驻资源列表
                UPCharacter5 = "5".equals(starLever) ? 2 : UPCharacter5;
                UPCharacter4 = "4".equals(starLever) ? 2 : UPCharacter4;
                return "5".equals(starLever) ? permanentFiveStarList : permanentFourStarList;
            }
        } else {
            System.out.println("保底UP");
            //获取当期UP资源列表
            UPCharacter5 = "5".equals(starLever) ? 1 : UPCharacter5;
            UPCharacter4 = "4".equals(starLever) ? 1 : UPCharacter4;
            return "5".equals(starLever) ? timeLimitFiveStarList : timeLimitFourStarList;
        }
    }

    // 多次抽取
    public List getResources(Map map) {
        int count = (int) map.get("count"); // 抽取数量
        String limitedType = (String) map.get("limitedType"); // 抽取数量
        List<String> list = new ArrayList();
        for (int i = 0; i < count; i++) {
            Map result = getOnceResource(limitedType);
            list.add(result.containsKey("5") ? "5" : result.containsKey("4") ? "4" : "3");
        }
        System.out.println(list);
        return list;
    }

    // 一次抽取
    private Map getOnceResource(String limitedType) {
        int currentCount5 = 0;
        int currentCount4 = 0;
        if (limitedType == null || "".equals(limitedType)) {
            permanentCount++;
            currentCount5 = ++permanentCurrentCount5;
            currentCount4 = ++permanentCurrentCount4;
        } else {
            UPCount++;
            currentCount5 = ++UPCurrentCount5;
            currentCount4 = ++UPCurrentCount4;
        }

        Map<String, Object> map = new HashMap();

        // 获取当前五星抽取概率
        double currentProbability5 = currentCount5 > count5 ? probability5 + (currentCount5 - count5) * probability5add : probability5;
        double result5 = random.nextDouble();
        if (result5 <= currentProbability5) {
            map.put("5", true);
        }

        // 获取当前四星抽取概率
        double currentProbability4 = currentCount4 > count4 ? probability4 + (currentCount4 - count4) * probability4add : probability4;
        double result4 = random.nextDouble();
        if (result4 <= currentProbability4) {
            map.put("4", true);
        }

        // 89抽前
        if (currentCount5 < 89) {
            if (map.containsKey("4")) {
                map.remove("5");
//                currentCount4 = 0;
                if (limitedType == null || "".equals(limitedType)) {
                    permanentCurrentCount4 = 0;
                } else {
                    UPCurrentCount4 = 0;
                }
            } else if (map.containsKey("5")) {
//                currentCount5 = 0;
                if (limitedType == null || "".equals(limitedType)) {
                    permanentCurrentCount5 = 0;
                } else {
                    UPCurrentCount5 = 0;
                }
            }
        }
        // 第89抽
        if (currentCount5 == 89) {
            if (currentCount4 > 8) {
                map.put("4", true);
                map.remove("5");
//                currentCount4 = 0;
                if (limitedType == null || "".equals(limitedType)) {
                    permanentCurrentCount4 = 0;
                } else {
                    UPCurrentCount4 = 0;
                }
            }
        }
        // 第90抽
        if (currentCount5 == 90) {
            map.put("5", true);
            map.remove("4");
//            currentCount5 = 0;
            if (limitedType == null || "".equals(limitedType)) {
                permanentCurrentCount5 = 0;
            } else {
                UPCurrentCount5 = 0;
            }
        }

        return map;
    }

    public static void main(String[] args) {
        ExtractResourceUtil eru = new ExtractResourceUtil();

//        //实例化随机对象
//        Random ran = new Random();
//
//        //通过使用nextDouble()方法是
//        //返回双伪随机
//        //在0.0和1.0之间的值
//        //使用随机值生成器
//        double val = ran.nextDouble();
//        System.out.println(val);
//        System.out.println(2 * eru.probability5add + 0.006);

        Map<String, Object> map = new HashMap();
        map.put("id", 0);
        map.put("count", 40);
        map.put("limitedType", "1");
        System.out.println(eru.getCompleteResources(map));

        int aaa = 10;
        int bbb = ++aaa;
        System.out.println(bbb);
        System.out.println(aaa);
    }
}
