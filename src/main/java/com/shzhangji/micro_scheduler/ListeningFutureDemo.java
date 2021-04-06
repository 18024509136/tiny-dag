package com.shzhangji.micro_scheduler;

import com.google.common.util.concurrent.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * @author huangxiaodi
 * @since 2021-03-29 16:48
 */
public class ListeningFutureDemo {

    public static void main(String[] args) {
        final CountDownLatch latch = new CountDownLatch(1);
        final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        ListenableFuture<String> explosion = service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("任务线程正在执行...");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "任务线程的结果";
            }
        });

        ListenableFuture<String> explosion2 = service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("任务线程2正在执行...");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "任务线程2的结果";
            }
        });

        List<ListenableFuture<String>> futureList = new ArrayList<>(2);
        futureList.add(explosion);
        futureList.add(explosion2);

        ListenableFuture<String> first = Futures.transform(Futures.allAsList(futureList), new AsyncFunction<List<String>, String>() {
            @Override
            public ListenableFuture<String> apply(List<String> input) throws Exception {
                ListenableFuture<String> temp = service.submit(new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        System.out.println("第1个回调线程正在执行...");
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return input + " & 第1个回调线程的结果 ";
                    }
                });
                return temp;
            }
        }, service);


        ListenableFuture<String> second = Futures.transform(first, new AsyncFunction<String, String>() {

            @Override
            public ListenableFuture<String> apply(final String input) throws Exception {
                ListenableFuture<String> temp = service.submit(new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        System.out.println("第2个回调线程正在执行...");
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return input + " & 第2个回调线程的结果 ";
                    }
                });
                return temp;
            }
        }, service);

        ListenableFuture<String> third = Futures.transform(second, new AsyncFunction<String, String>() {

            @Override
            public ListenableFuture<String> apply(final String input) throws Exception {
                ListenableFuture<String> temp = service.submit(new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        System.out.println("第3个回调线程正在执行...");
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return input + " & 第3个回调线程的结果 ";
                    }
                });
                return temp;
            }
        }, service);

        ListenableFuture<String> forth = Futures.transform(third, new AsyncFunction<String, String>() {

            @Override
            public ListenableFuture<String> apply(final String input) throws Exception {
                ListenableFuture<String> temp = service.submit(new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        System.out.println("第4个回调线程正在执行...");
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return input + " & 第4个回调线程的结果 ";
                    }
                });
                return temp;
            }
        }, service);

        Futures.addCallback(forth, new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                latch.countDown();
                System.out.println("结果: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println(t.getMessage());
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.shutdown();
    }
}
