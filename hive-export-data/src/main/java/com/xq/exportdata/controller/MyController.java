package com.xq.exportdata.controller;

import com.alibaba.fastjson.JSONObject;;
import com.xq.exportdata.entity.User;
import com.xq.exportdata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MyController {

    @Autowired
    private UserService userService;

    @RequestMapping("/all")
    public JSONObject findAll(){
        List<User> userList1 = userService.findAllUserMapper1();
        List<User> userList2 = userService.findAllUserMapper2();
        userList1.addAll(userList2);
//        List<User> userList = new ArrayList<>();
//        userList.addAll(userList1);
//        userList.addAll(userList2);
        JSONObject json = new JSONObject();
        json.put("data",userList1);
        return json;
    }

    @RequestMapping("/test")
    public String test(){
        System.out.println("我是测试的。。。");
        return "返回成功了。。。";
    }
    /***    数据库1  ***/
    @RequestMapping("/add1")
    public JSONObject addOne1(User user){
        userService.addOneUserMapper1(user);
        JSONObject json = new JSONObject();
        json.put("data",user);
        return json;
    }



    @RequestMapping("/update1")
    public JSONObject update1(User user){
        userService.updateOneUserMapper1(user);
        JSONObject json = new JSONObject();
        json.put("data",user);
        return json;
    }

    @RequestMapping("/del1")
    public void delOne1(Integer id){
        userService.delOneUserMapper1(id);
    }

    /***    数据库2  ***/
    @RequestMapping("/add2")
    public JSONObject addOne2(User user){
        userService.addOneUserMapper2(user);
        JSONObject json = new JSONObject();
        json.put("data",user);
        return json;
    }



    @RequestMapping("/update2")
    public JSONObject update2(User user){
        userService.updateOneUserMapper2(user);
        JSONObject json = new JSONObject();
        json.put("data",user);
        return json;
    }

    @RequestMapping("/del2")
    public void delOne2(Integer id){
        userService.delOneUserMapper2(id);
    }
}
