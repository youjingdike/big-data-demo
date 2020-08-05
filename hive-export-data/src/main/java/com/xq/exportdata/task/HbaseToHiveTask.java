package com.xq.exportdata.task;

import java.time.LocalDateTime;

public class HbaseToHiveTask implements Runnable {
    private String id = "";

    public HbaseToHiveTask(String id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("任务："+id+",启动时间："+ LocalDateTime.now());
        if ("1".equals(id)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("任务："+id+",结束时间："+ LocalDateTime.now());
    }
}
