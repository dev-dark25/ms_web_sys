package com.example.web.controller;

import com.example.web.mapper.secondary.AccountsMapper;
import com.example.web.mapper.secondary.CharactersMapper;
import com.example.web.mapper.secondary.SkillsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@RequestMapping("/api")
@RestController
public class MSController {

    //    private Logger logger = LoggerFactory.getLogger("special"); // 使用定制的logger
    private final Logger logger = LoggerFactory.getLogger(MSController.class);  // 默认

    @Resource
    private AccountsMapper accountsMapper;

    @Resource
    private CharactersMapper charactersMapper;

    @Resource
    private SkillsMapper skillsMapper;

    @Resource
    private RestTemplate restTemplate;

    @RequestMapping(value = "/ms/start", method = RequestMethod.POST)
    public void start() {
        // 调用nacos服务    TODO

        Map map = new HashMap();
        map.put("1", 1);
        ResponseEntity<String> re = restTemplate.postForEntity("http://localhost:8080/heaven-ms/init", map, String.class);
        System.out.println(re.getBody());

//        String body = restTemplate.postForObject("localhost:8080/heaven-ms/init", map, String.class);
//        System.out.println(body);
    }

    @RequestMapping(value = "/ms/stop", method = RequestMethod.POST)
    public void stop() {
        // 调用nacos服务    TODO

//        restTemplate.getForObject("http://localhost:8080/heaven-ms/shutdown", String.class);

//        ResponseEntity<String> re = restTemplate.getForEntity("localhost:8080/heaven-ms/shutdown", String.class);
//        System.out.println(re.getBody());

        String body = restTemplate.getForObject("http://localhost:8080/heaven-ms/shutdown", String.class);
        System.out.println(body);
    }

    @RequestMapping(value = "/ms/restart", method = RequestMethod.POST)
    public void restart() {
        // 调用nacos服务    TODO

        String body = restTemplate.getForObject("http://localhost:8080/heaven-ms/restart", String.class);
        System.out.println(body);
    }

    @RequestMapping(value = "/ms/getConfig", method = RequestMethod.POST)
    public Map getConfig(@RequestBody Map<String, Object> req) {
        // 调用nacos服务    TODO

        return restTemplate.postForObject("http://localhost:8080/heaven-ms/getConfig", req, Map.class);
    }

    @RequestMapping(value = "/ms/updataConfig", method = RequestMethod.POST)
    public Map updataConfig(@RequestBody Map<String, Object> req) {
        // 调用nacos服务    TODO

        return restTemplate.postForObject("http://localhost:8080/heaven-ms/updataConfig", req, Map.class);
    }

    @RequestMapping(value = "/ms/getCommand", method = RequestMethod.POST)
    public Map getCommand(@RequestBody Map<String, Object> req) {
        // 调用nacos服务    TODO

        return restTemplate.postForObject("http://localhost:8080/heaven-ms/getCommand", req, Map.class);
    }

    @RequestMapping(value = "/ms/updataCommand", method = RequestMethod.POST)
    public Map updataCommand(@RequestBody Map<String, Object> req) {
        // 调用nacos服务    TODO

        return restTemplate.postForObject("http://localhost:8080/heaven-ms/updataCommand", req, Map.class);
    }

    @RequestMapping(value = "/ms/getPlayer", method = RequestMethod.POST)
    public Map getPlayer(@RequestBody Map<String, Object> req) {
        // 调用nacos服务    TODO

        return restTemplate.postForObject("http://localhost:8080/heaven-ms/getPlayer", req, Map.class);
    }

    @RequestMapping(value = "/ms/updataPlayer", method = RequestMethod.POST)
    public Map updataPlayer(@RequestBody Map<String, Object> req) {
        // 调用nacos服务    TODO

        return restTemplate.postForObject("http://localhost:8080/heaven-ms/updataPlayer", req, Map.class);
    }

    @RequestMapping(value = "/ms/getAccounts", method = RequestMethod.POST)
    public Object getAccounts() {
        Map<String, Object> map = new HashMap<>();
        List<Object> list = accountsMapper.selectAll();
        List<Object> list1 = accountsMapper.selectById(3);
        int loggedCount = accountsMapper.selectCountByLoggedin();
        map.put("code", "200");
        map.put("message", "222");
        map.put("list", list);
        map.put("loggedCount", loggedCount);
        map.put("list1", list1);
        return map;
    }

    @RequestMapping(value = "/ms/getCharacters", method = RequestMethod.POST)
    public Map getCharacters(@RequestBody Map requestParam) {
        logger.info("login: {}", requestParam);
        int currentPage = (int) requestParam.get("currentPage");
        int pageSize = (int) requestParam.get("pageSize");
        Map<String, Object> param = new HashMap<>();
        param.put("currentIndex", (currentPage - 1) * pageSize);
        param.put("pageSize", pageSize);
        List<Map> list = charactersMapper.selectByPagination(param);
        System.out.println(list.get(0).get("gm"));
        int total = charactersMapper.selectCount();
        Map<String, Object> map = new HashMap<>();
        map.put("code", "200");
        map.put("message", "222");
        map.put("list", list);
        map.put("total", total);
        return map;
    }

    @RequestMapping(value = "/ms/getSkills", method = RequestMethod.POST)
    public Map getSkills(@RequestBody Map requestParam) {
        int characterId = (int) requestParam.get("characterId");
        int currentPage = (int) requestParam.get("currentPage");
        int pageSize = (int) requestParam.get("pageSize");
        Map<String, Object> param = new HashMap<>();
        param.put("characterId", characterId);
        param.put("currentIndex", (currentPage - 1) * pageSize);
        param.put("pageSize", pageSize);
        List<Object> list = skillsMapper.selectByPagination(param);
        int total = skillsMapper.selectCount(param);
        Map<String, Object> map = new HashMap<>();
        map.put("code", "200");
        map.put("message", "222");
        map.put("list", list);
        map.put("total", total);
        return map;
    }

    @RequestMapping(value = "/ms/getRealtimeLoggedCount", method = RequestMethod.POST)
    public Map getRealtimeLoggedCount() {
        Map<String, Object> param = new HashMap<>();
        int total = skillsMapper.selectCount(param);
        Map<String, Object> map = new HashMap<>();
        map.put("code", "200");
        map.put("message", "222");

        // 获取ThreadLocalRandom实例
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Integer> list1 = new ArrayList<Integer>();
        List<Integer> list2 = new ArrayList<Integer>();
        IntStream.range(1, 6).forEach(i -> {
            // 生成一个随机整数，在[0, Integer.MAX_VALUE]范围内
//            int randomInt = random.nextInt();
            int randomInt = random.nextInt(10, 101);
            // 生成一个随机整数，在指定范围内，例如[1, 50]
            int randomIntRange = random.nextInt(1, 51);
            list1.add(randomInt);
            list2.add(randomIntRange);
        });
        map.put("list1", list1);
        map.put("list2", list2);
        return map;
    }
}
