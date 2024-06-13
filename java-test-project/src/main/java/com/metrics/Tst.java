package com.metrics;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Tst {
    // 作为全局变量，可以在自定义监控中使用
    public static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    static {
        // 添加 Prometheus 全局 Label，建议加一下对应的应用名
        registry.config().commonTags("application", "prometheus-java-demo");
    }
    public static void main(String[] args) {
        // 添加 JVM 相关的指标
        new ClassLoaderMetrics().bindTo(registry);
        new JvmMemoryMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);
        new JvmHeapPressureMetrics().bindTo(registry);
        // new JvmInfoMetrics().bindTo(registry);
        // 添加 system 相关的指标
        new ProcessorMetrics().bindTo(registry);
        new UptimeMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);
        // new DiskSpaceMetrics(new File("")).bindTo(registry);

        // 添加 log 相关指标
        // new LogbackMetrics().bindTo(registry);
        // new Log4j2Metrics().bindTo(registry);

        // Test GC
        // System.gc();
        try {
            // 暴露 Prometheus HTTP 服务，如果已经有，可以使用已有的 HTTP Server
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/metrics", httpExchange -> {
                String response = registry.scrape();
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });


            new Thread(server::start).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
