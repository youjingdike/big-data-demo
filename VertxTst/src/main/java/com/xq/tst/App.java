package com.xq.tst;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.file.FileSystemOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException {
        VertxOptions vo = new VertxOptions();
        vo.setEventLoopPoolSize(1);
        vo.setWorkerPoolSize(2);
        FileSystemOptions fileSystemOptions = new FileSystemOptions();
        fileSystemOptions.setFileCachingEnabled(false);
        vo.setFileSystemOptions(fileSystemOptions);
        Vertx vertx = Vertx.vertx(vo);


        JsonObject config = new JsonObject();
        config.put("url", "jdbc:mysql://127.0.0.1:3306/tst?useUnicode=true&characterEncoding=utf8&createDatabaseIfNotExist=true&useSSL=false")
                .put("driver_class", "com.mysql.jdbc.Driver")
                .put("max_pool_size", 16)
                .put("user", "root")
                .put("password", "123456");

        JDBCPool pool = JDBCPool.pool(vertx, config);

        /*pool
                .query("SELECT * FROM aa")
                .execute()
                .onFailure(e -> {
                    System.out.println("!!!!!");
                    System.out.println(e);
                    System.out.println("!!!!!");
                    pool.close();
                })
                .onSuccess(rows -> {
                    for (Row row : rows) {
                        System.out.println(row.getString("name"));
                    }
                });*/

        CountDownLatch countDownLatch = new CountDownLatch(10000);
        long start = System.currentTimeMillis();
        System.out.println("开始时间："+start);
        for (int i = 0; i < 10000; i++) {
            int finalI = i;
            pool
                    .preparedQuery("SELECT * FROM aa WHERE id > ?")
                    // the emp id to look up
                    .execute(Tuple.of(1))
                    .onFailure(e -> {
                        System.out.println("@@@@@");
                        System.out.println(e);
                        System.out.println("@@@@@");
                        pool.close();
                        countDownLatch.countDown();
                    })
                    .onSuccess(rows -> {
                        for (Row row : rows) {
                            System.out.println(finalI + ":" +row.getString("name"));
                        }
                        countDownLatch.countDown();
                    });

        }

        countDownLatch.await();
        System.out.println("用时："+(System.currentTimeMillis() - start) + "ms");
        /*pool.close(new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> voidAsyncResult) {
                System.out.println(voidAsyncResult.succeeded());
            }
        });*/
    }
}
