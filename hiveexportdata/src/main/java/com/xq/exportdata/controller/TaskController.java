package com.xq.exportdata.controller;

import com.xq.exportdata.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

;

@Controller
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @RequestMapping("/addtask")
    @ResponseBody
    public String addTask(String id,String date){
        System.out.println("启动任务的。。。");
        String res = "";
        int re = taskService.addTask(id,date);
        if (re == 2) {
            res = "任务启动成功。。。";
        } else if (re == 1) {
            res = "无法启动，该id的任务已执行。。。";
        } else if (re == 3) {
            res = "时间格式不正确。。。";
        } else {
            res = "任务启动失败。。。";
        }
        return res;
    }

    @RequestMapping("/stoptask")
    @ResponseBody
    public String stopTask(String id){
        System.out.println("我是停止任务的。。。");
        String res = "";
        int re = taskService.stopTask(id);
        if (re == 0) {
            res = "任务id 不能为空。。。";
        } else if  (re == 1) {
            res = "没有该id的任务执行。。。";
        } else if (re == 2) {
            res = "任务停止成功。。。";
        } else if (re == 3) {
            res = "任务停止失败。。。";
        }
        return res;
    }
}
