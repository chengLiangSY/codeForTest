package com.cl.data.thread;

import org.junit.Test;

/**
 * Created by 亮 on 2017/12/26.
 */
public class FSEditLogTest {
    //是否开启自动同步计划？
    private volatile boolean isAutoSyncScheduled = false;
    //是否有当前同步操作正在进行？
    private volatile boolean isSyncRunning = false;
    //针对事务ID，单调增长的计数器
    private long txid = 0;
    //存储最新的事务ID
    private long synctxid = 0;
    //事务数量
    private long numTransactions = 0;
    //所有事务的共计时间
    private long totalTimeTransactions = 0;

    private static final ThreadLocal<TransactionId> myTransactionId = new ThreadLocal<TransactionId>(){
        @Override
        protected TransactionId initialValue() {
            return new TransactionId(Long.MAX_VALUE);
        }
    };


    public void logEdit(String log){
        synchronized (this){
            System.out.println(String.format("线程：%s 执行操作，logEdit()启动，事务ID = %s ....", Thread.currentThread().getName(), txid));
            waitIfAutoSyncScheduled();
            long start = beginTransaction();
            System.out.println(String.format("线程：%s 执行操作，数据写入EditLongStream字节缓冲区(write into currentBuffer)....", Thread.currentThread().getName()));
            try {
                if("client-x".equals(Thread.currentThread().getName())){
                    Thread.currentThread().sleep(5000);
                }else{
                    Thread.currentThread().sleep(1000);
                }
            }catch (InterruptedException e){}
            endTransaction(start);

            isAutoSyncScheduled = true;
            System.out.println(String.format("线程：%s 执行操作，设置isAutoSyncScheduled = true，释放 ->>logEdit 环节锁.... ", Thread.currentThread().getName()));
        }

        logSync();

    }

    public void logSync(){
        long syncStart = 0;
        long mytxid = myTransactionId.get().txid;

        boolean sync = false;
        try {
            synchronized (this) {
                try {
                    while (mytxid > synctxid && isSyncRunning) {
                        try {
                            wait(1000);
                        } catch (InterruptedException e) {
                        }
                    }

                    if (mytxid <= synctxid) {
                        System.out.println(String.format("mytxid <= synctxid, currentThread is %s", Thread.currentThread().getName()));
                        return;
                    }
                    syncStart = txid;
                    isSyncRunning = true;
                    sync = true;

                    System.out.println(String.format("线程：%s 执行操作，EditLogStream 双缓冲(currentBuffer<-->readyBuffer)进行swqp操作....", Thread.currentThread().getName()));
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } finally {
                    doneWithAutoScheduling();
                }
            }

            long start = monotonicNow();
            try {
                System.out.println(String.format("线程：%s 执行操作，readyBuffer缓冲区数据刷写磁盘...", Thread.currentThread().getName()));
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
            }
            long elapsed = monotonicNow() - start;
            System.out.println(String.format("线程：%s 执行操作，刷盘时间：%s", Thread.currentThread().getName(), elapsed));
        }finally {
            synchronized (this){
                if(sync){
                    synctxid = syncStart;
                    isSyncRunning = false;
                    System.out.println(String.format("线程：%s 执行操作，释放 -->> logSync 锁【synctxid = syncStart, isSyncRunning = false】....", Thread.currentThread().getName()));
                    System.out.println();
                }
                this.notifyAll();
            }
        }

    }

    /**
     * 同步计划开启则等待
     */
    synchronized void waitIfAutoSyncScheduled(){
        try{
            while (isAutoSyncScheduled){
                this.wait(10);
                System.out.println(String.format("线程：%s 执行操作，waitIfAutoSyncScheduled 等待 0.01s.... ", Thread.currentThread().getName()));
            }
        }catch (InterruptedException e){
        }
    }

    /**
     * 若果同步计划开启，则关闭同步计划，同时唤醒其他等待线程
     */
    synchronized void doneWithAutoScheduling(){
        if(isAutoSyncScheduled){
            isAutoSyncScheduled = false;
            System.out.println(String.format("线程：%s 执行操作，关闭同步计划[doneWithAutoScheduling]....", Thread.currentThread().getName()));
            notifyAll();
        }
    }

    /**
     * 事务封装器
     */
    private static class TransactionId{
        public long txid;

        public TransactionId(long txid) {
            this.txid = txid;
        }
    }

    /**
     * 开启事务ID，txid自增，保存在当前线程下
     * @return 返回微秒值
     */
    private  long beginTransaction(){
        assert Thread.holdsLock(this);

        txid++;
        TransactionId id = myTransactionId.get();
        id.txid = txid;

        return monotonicNow();
    }

    /**
     * 关闭事务ID，统计相关数据
     * @param start
     */
    private void endTransaction(long start){
        assert Thread.holdsLock(this);

        long end = monotonicNow();
        numTransactions++;
        totalTimeTransactions += (end - start);
        System.out.println(String.format("线程：%s 执行操作，事务结束【numTransactions is %s, totalTimeTransactions is %s】.... ", Thread.currentThread().getName(), numTransactions, totalTimeTransactions));
    }


    private long monotonicNow(){
        final long NANOSECONDS_PRE_MILLISECOND = 1000000;
        return System.nanoTime() / NANOSECONDS_PRE_MILLISECOND;
    }

//    @
    public static void main (String[] args){

        FSEditLogTest fsEditLog = new FSEditLogTest();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                fsEditLog.logEdit(null);
            }
        }, "client-1").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                fsEditLog.logEdit(null);
            }
        }, "client-2").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                fsEditLog.logEdit(null);
            }
        }, "client-3").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                fsEditLog.logEdit(null);
            }
        }, "client-4").start();


    }


}
