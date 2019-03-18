package com.softbasic.micro.controller;

import com.softbasic.micro.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



import java.util.*;

/**
 * Created by LCR on 2018/12/18.
 */
@RestController
@RequestMapping("core")
public class RestTest {
    private static Logger log = LoggerFactory.getLogger(RestTest.class);

    @RequestMapping(value = "test",method = RequestMethod.GET)
    public List<User> test(){
        log.error("报错了");
        User user1 = new User();
        user1.setId("1");
        user1.setUserName("张三分");
        user1.setPassword("123");
        user1.setCreateTime(new Date());
        log.info("中间了");
        User user2 = new User();
        user2.setId("2");
        user2.setUserName("吴天罡");
        user2.setPassword("12223");
        user2.setCreateTime(new Date());

        List<User> ss=new ArrayList<>();
        ss.add(user1);
        ss.add(user2);
        log.debug("结束了");
        return ss;
    }

    @RequestMapping(value = "testMap",method = RequestMethod.GET)
    public Map<String,User> testMap(){
        User user1 = new User();
        user1.setId("1");
        user1.setUserName("张三分");
        user1.setPassword("123");
        user1.setCreateTime(new Date());

        User user2 = new User();
        user2.setId("2");
        user2.setUserName("吴天罡");
        user2.setPassword("12223");
        user2.setCreateTime(new Date());

        Map<String,User> ss=new HashMap<>();
        ss.put("1",user1);
        ss.put("2",user2);
        return ss;
    }
}
