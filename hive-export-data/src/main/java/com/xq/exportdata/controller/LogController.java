package com.xq.exportdata.controller;

import com.xq.exportdata.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

;

@RestController
public class LogController {
    private static Logger logger = LoggerFactory.getLogger(LogController.class);
    @Autowired
    private UserService userService;


    @RequestMapping("/log")
    public String logTest(){
        System.out.println("我是测试的。。。");
        logger.info("测试info");
        logger.debug("测试debug");
        logger.warn("测试warn");
        logger.error("测试error");
        return "返回成功了。。。";
    }
}
