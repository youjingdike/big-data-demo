package com.xq.exportdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class HiveExportDataApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {

        SpringApplication.run(HiveExportDataApplication.class, args);

    }

    //如果部署到容器需要这么修改
    /*@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }*/
}





