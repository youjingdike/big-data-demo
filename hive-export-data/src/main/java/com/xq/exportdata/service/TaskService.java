package com.xq.exportdata.service;

import com.xq.exportdata.task.HbaseToHiveTask;
import com.xq.exportdata.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class TaskService {
    private static Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private ConcurrentHashMap<String,ScheduledFuture<?>>  task = new ConcurrentHashMap<>();

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {

        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(20);
        executor.setThreadNamePrefix("taskExecutor-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    public int addTask(String id,String date) {
        System.out.println("addTask. start...");
        int flag = 0;
        if (id == null || "".equals(id)) return 0;
        if (task.containsKey(id)) {
            return 1;
        }
        ScheduledFuture<?> future = null;
        if (StringUtils.isEmpty(date)) {
            future = threadPoolTaskScheduler.scheduleWithFixedDelay(new HbaseToHiveTask(id),new Date(),1000);
            task.put(id, future);
            flag = 2;
        } else {
            Date dt = null;
            try {
                dt = DateUtil.parseStr(date);
                future = threadPoolTaskScheduler.scheduleWithFixedDelay(new HbaseToHiveTask(id),dt,1000);
                task.put(id, future);
                flag = 2;
            } catch (ParseException e) {
                e.printStackTrace();
                flag = 3;
            }
        }
        return flag;
    }

    public int stopTask(String id) {
        if (id == null || "".equals(id)) return 0;
        ScheduledFuture<?> scheduledFuture = task.get(id);
        if (scheduledFuture == null) {
            return 1;
        }
        if (!scheduledFuture.cancel(true)) {
            return 3;
        }
        task.remove(id);
        return 2;
    }
}
