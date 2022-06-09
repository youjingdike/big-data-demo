package com.xq.tst;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unit test for simple App.
 */
public class TstFuture {

    /*
    * 1.创建异步线程
     */
    @Test
    public void tstCreate() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //runAsync的使用
        CompletableFuture<Void> rFuture = CompletableFuture
                .runAsync(() -> System.out.println("hello siting"), executor);
        //supplyAsync的使用
        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("hello ");
                    return "siting";
                }, executor);


        //阻塞等待，runAsync 的future 无返回值，输出null
        System.out.println("!!!!!!:"+rFuture.join());
        //阻塞等待
        String name = future.join();
        System.out.println("@@@@@:"+name);
        executor.shutdown(); // 线程池需要关闭

        //有时候是需要构建一个常量的CompletableFuture
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.completedFuture("123");
        System.out.println(stringCompletableFuture.get());
    }

    /*
     * 2.线程串行执行:
     * 2.1 任务完成则运行action，不关心上一个任务的结果，无返回值
     */
    @Test
    public void tstSerial1() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> future = CompletableFuture
                .supplyAsync(() -> "hello siting", executor)
                .thenRunAsync(() -> System.out.println("OK"), executor);
        executor.shutdown();
    }

    /*
     * 2.线程串行执行:
     * 2.2 任务完成则运行action，依赖上一个任务的结果，无返回值
     */
    @Test
    public void tstSerial2() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> future = CompletableFuture
                .supplyAsync(() -> "hello siting", executor)
                .thenAcceptAsync(System.out::println, executor);
        executor.shutdown();

    }

    /*
     * 2.线程串行执行:
     * 2.3 任务完成则运行fn，依赖上一个任务的结果，有返回值
     */
    @Test
    public void tstSerial3() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> "hello world", executor)
                .thenApplyAsync(data -> {
                    System.out.println(data);
                    return "OK";
                }, executor);
        System.out.println("@@@@@:"+future.join());
        executor.shutdown();

    }

    /*
     * 2.线程串行执行:
     * 2.4 任务完成则运行fn，依赖上一个任务的结果，有返回值
     */
    @Test
    public void tstSerial4() {
        //第一个异步任务，常量任务
        CompletableFuture<String> f = CompletableFuture.completedFuture("OK");
        //第二个异步任务
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> "hello world", executor)
                .thenComposeAsync(data -> {
                    System.out.println(data);
                    return f; //使用第一个任务作为返回
                }, executor);
        System.out.println("@@@@@:"+future.join());
        executor.shutdown();

    }

    /*
     * 3.线程并行执行:
     * 3.1 两个CompletableFuture[并行]执行完，然后执行action，不依赖上两个任务的结果，无返回值
     */
    @Test
    public void tstParallel1() {
        //第一个异步任务，常量任务
        CompletableFuture<String> first = CompletableFuture.completedFuture("hello world");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> future = CompletableFuture
                //第二个异步任务
                .supplyAsync(() -> "hello siting", executor)
                // () -> System.out.println("OK") 是第三个任务
                .runAfterBothAsync(first, () -> System.out.println("OK"), executor);
        executor.shutdown();

    }

    /*
     * 3.线程并行执行:
     * 3.2 两个CompletableFuture[并行]执行完，然后执行action，依赖上两个任务的结果，无返回值
     */
    @Test
    public void tstParallel2() {
        //第一个异步任务，常量任务
        CompletableFuture<String> first = CompletableFuture.completedFuture("hello world");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> future = CompletableFuture
                //第二个异步任务
                .supplyAsync(() -> "hello siting", executor)
                // (w, s) -> System.out.println(s) 是第三个任务
                .thenAcceptBothAsync(first, (s, w) -> {
                    System.out.println(s);
                    System.out.println(w);
                }, executor);
        executor.shutdown();

    }

    /*
     * 3.线程并行执行:
     * 3.3 两个CompletableFuture[并行]执行完，然后执行action，依赖上两个任务的结果，有返回值
     */
    @Test
    public void tstParallel3() {
        //第一个异步任务，常量任务
        CompletableFuture<String> first = CompletableFuture.completedFuture("hello world");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = CompletableFuture
                //第二个异步任务
                .supplyAsync(() -> "hello siting", executor)
                // (w, s) -> System.out.println(s) 是第三个任务
                .thenCombineAsync(first, (s, w) -> {
                    System.out.println(s);
                    return "OK";
                }, executor);
        System.out.println("@@@@@@@@:"+future.join());
        executor.shutdown();

    }

    /*
     * 4.线程并行执行，谁先执行完则谁触发下一任务（二者选其最快）:
     * 4.1 上一个任务或者other任务完成, 运行action，不依赖前一任务的结果，无返回值
     */
    @Test
    public void tstEither1() {
        //第一个异步任务，休眠1秒，保证最晚执行晚
        CompletableFuture<String> first = CompletableFuture.supplyAsync(()->{
            try{ Thread.sleep(1000); }catch (Exception e){}
            System.out.println("hello world");
            return "hello world";
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> future = CompletableFuture
                //第二个异步任务
                .supplyAsync(() ->{
                    System.out.println("hello siting");
                    return "hello siting";
                } , executor)
                //() ->  System.out.println("OK") 是第三个任务
                .runAfterEitherAsync(first, () ->  System.out.println("OK") , executor);
        executor.shutdown();

    }

    /*
     * 4.线程并行执行，谁先执行完则谁触发下一任务（二者选其最快）:
     * 4.2 上一个任务或者other任务完成, 运行action，依赖前一任务的结果，无返回值
     */
    @Test
    public void tstEither2() {
        //第一个异步任务，休眠1秒，保证最晚执行晚
        CompletableFuture<String> first = CompletableFuture.supplyAsync(()->{
            try{ Thread.sleep(1000);  }catch (Exception e){}
            return "hello world";
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> future = CompletableFuture
                //第二个异步任务
                .supplyAsync(() -> "hello siting", executor)
                // data ->  System.out.println(data) 是第三个任务
                .acceptEitherAsync(first, data ->  System.out.println(data) , executor);
        executor.shutdown();

    }

    /*
     * 4.线程并行执行，谁先执行完则谁触发下一任务（二者选其最快）:
     * 4.3 上一个任务或者other任务完成, 运行fn，依赖最先完成任务的结果，有返回值
     */
    @Test
    public void tstEither3() throws ExecutionException, InterruptedException {
        //第一个异步任务，休眠1秒，保证最晚执行晚
        CompletableFuture<String> first = CompletableFuture.supplyAsync(()->{
            try{
                Thread.sleep(1000);
            } catch (Exception e){
            }
            return "hello world";
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = CompletableFuture
                //第二个异步任务
                .supplyAsync(() -> "hello siting", executor)
                // data ->  System.out.println(data) 是第三个任务
                .applyToEitherAsync(first, data ->  {
                    System.out.println(data);
                    return "OK";
                } , executor);
        System.out.println("@@@@@@@@@@@@@@:"+future.get());
        executor.shutdown();

    }

    /*
     * 5.exceptionally-处理异常:
     * 5.1 如果之前的处理环节有异常问题，则会触发exceptionally的调用相当于 try...catch
     */
    @Test
    public void tstExcp1() {
        CompletableFuture<Integer> first = CompletableFuture
                .supplyAsync(() -> {
                    if (true) {
                        throw new RuntimeException("main error!");
                    }
                    return "hello world";
                })
                .thenApply(data -> 1)
                .exceptionally(e -> {
                    e.printStackTrace(); // 异常捕捉处理，前面两个处理环节的异常都能捕获
                    return 0;
                });
    }

    /*
     * 5.exceptionally-处理异常:
     *  5.2 handle-任务完成或者异常时运行fn，返回值为fn的返回
     *   :相比exceptionally而言，即可处理上一环节的异常也可以处理其正常返回值
     */
    @Test
    public void tstExcp2() {
        CompletableFuture<Integer> first = CompletableFuture
                .supplyAsync(() -> {
                    if (true) { throw new RuntimeException("main error!"); }
                    return "hello world";
                })
                .thenApply(data -> 1)
                .handleAsync((data,e) -> {
                    e.printStackTrace(); // 异常捕捉处理
                    return data;
                });
        System.out.println(first.join());
    }

    /*
     * 5.exceptionally-处理异常:
     *  5.3 whenComplete-任务完成或者异常时运行action，有返回值
     *   * whenComplete与handle的区别在于，它不参与返回结果的处理，把它当成监听器即可
     *   * 即使异常被处理，在CompletableFuture外层，异常也会再次复现
     *   * 使用whenCompleteAsync时，返回结果则需要考虑多线程操作问题，毕竟会出现两个线程同时操作一个结果
     */
    @Test
    public void tstExcp3() {
        CompletableFuture<AtomicBoolean> first = CompletableFuture
                .supplyAsync(() -> {
                    if (true) {  throw new RuntimeException("main error!"); }
                    return "hello world";
                })
                .thenApply(data -> new AtomicBoolean(false))
                .whenCompleteAsync((data,e) -> {
                    //异常捕捉处理, 但是异常还是会在外层复现
                    System.out.println(e.getMessage());
                });
        first.join();
    }

    /*
     * 6.多个任务的简单组合:
     *  whenComplete-任务完成或者异常时运行action，有返回值
     *   * whenComplete与handle的区别在于，它不参与返回结果的处理，把它当成监听器即可
     *   * 即使异常被处理，在CompletableFuture外层，异常也会再次复现
     *   * 使用whenCompleteAsync时，返回结果则需要考虑多线程操作问题，毕竟会出现两个线程同时操作一个结果
     */
    @Test
    public void tstOf() {
        CompletableFuture<Void> future = CompletableFuture
                .allOf(CompletableFuture.completedFuture("A"),
                        CompletableFuture.completedFuture("B"));
        //全部任务都需要执行完
        future.join();
        CompletableFuture<Object> future2 = CompletableFuture
                .anyOf(CompletableFuture.completedFuture("C"),
                        CompletableFuture.completedFuture("D"));
        //其中一个任务行完即可
        future2.join();
    }

    /*
     * 7.取消执行线程任务:
     */
    @Test
    public void tstCancel() {
        CompletableFuture<Integer> future = CompletableFuture
                .supplyAsync(() -> {
                    try { Thread.sleep(1000);  } catch (Exception e) { }
                    return "hello world";
                })
                .thenApply(data -> 1);


        System.out.println("任务取消前:" + future.isCancelled());
        // 如果任务未完成,则返回异常,需要对使用exceptionally，handle 对结果处理
        future.cancel(true);
        System.out.println("任务取消后:" + future.isCancelled());
        future = future.exceptionally(e -> {
            e.printStackTrace();
            return 0;
        });
        System.out.println(future.join());
    }

    /*
     * 8.任务的获取和完成与否判断:
     */
    @Test
    public void tstAssess() {
        CompletableFuture<Integer> future = CompletableFuture
                .supplyAsync(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                    }
                    return "hello world";
                })
                .thenApply(data -> {
                    return 1;
                });


        System.out.println("任务完成前:" + future.isDone());
        future.complete(10);
        System.out.println("任务完成后:" + future.join());
    }
}
