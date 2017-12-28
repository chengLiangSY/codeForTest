package com.cl.data.thread;

/**
 * Created by 亮 on 2017/12/27.
 */
public class WaitAndNotifyForTest {
    private volatile boolean isRunning = false;

    public void waitForTest(long timeout){
        synchronized (this){
            while (isRunning){
                try {
                    wait(timeout);
                    System.out.println(String.format("线程：%s 执行操作，等待%sms,获取WaitAndNotifyForTest对象锁【%s】...", Thread.currentThread().getName(), timeout, Thread.holdsLock(this)));
                }catch (InterruptedException e){}
            }
            isRunning = true;
        }

        try {
            try{
                System.out.println(String.format("线程：%s 执行操作，sleep 6s....", Thread.currentThread().getName()));
                Thread.sleep(6000);
            }catch (InterruptedException e){}
        }finally {
            synchronized (this){
                if(isRunning){
                    isRunning = false;
                }
                this.notifyAll();
            }

        }
    }

    public static void main(String[] args) {
        WaitAndNotifyForTest want = new WaitAndNotifyForTest();
        new Thread(new Runnable() {
            @Override
            public void run() {
                want.waitForTest(1000);
            }
        }, "client-1").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                want.waitForTest(1000);
            }
        }, "client-2").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                want.waitForTest(1000);
            }
        }, "client-3").start();
    }
}
