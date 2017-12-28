package com.cl.data.niotest;

import org.junit.Ignore;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 亮 on 2017/12/19.
 */
public class NioAction {

    @Ignore("randomReadFileForTest is not tested at this time!")
    @Test
    public void randomReadFileForTest() {
        URL fileUrl = NioAction.class.getClassLoader().getResource("nio.txt");
        String path = fileUrl.getFile();

        RandomAccessFile aFile = null;
        try{
            aFile = new RandomAccessFile(path, "rw");
            FileChannel fileChannel = aFile.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(1024);
            int bytesRead = fileChannel.read(buf);
            System.out.println("bytesRead -> " + bytesRead);
            while (bytesRead != -1){
                buf.flip();
                while (buf.hasRemaining()){
                    System.out.println((char)buf.get());
                    System.out.println("--->>");
                }
                buf.compact();
                bytesRead = fileChannel.read(buf);

            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Test
    public void startClientForTest(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("192.168.19.38", 8181));
            System.out.println("finishConnect --------->>>>>>>>> " + socketChannel.finishConnect());
            if(socketChannel.finishConnect()){
                int i = 0;
                while (true){
                    if(i >= 10){
                        break;
                    }
                    TimeUnit.SECONDS.sleep(2);
                    String info = "I'm " + i++ + "-th information from client! ";
                    buffer.clear();
                    buffer.put(info.getBytes("UTF-8"));
                    buffer.flip();
                    while (buffer.hasRemaining()){//socketChannel不能确定一次全部写入，用while
                        System.out.println(buffer);
                        socketChannel.write(buffer);
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {

            try {
                System.out.println("it's closed after 10 sec");
                TimeUnit.SECONDS.sleep(10);
                if(socketChannel != null){
                    socketChannel.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Test
    public void startServerForTest(){
        final int BUF_SIZE = 1024;
        final int PORT = 8181;
        final int TIMEOUT = 3000;

        Selector selector = null;
        ServerSocketChannel serverSocketChannel = null;
        try{
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true){
                if(selector.select(TIMEOUT) == 0){
                    System.out.println("waiting ....");
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isAcceptable()){
                        System.out.println("isAcceptable ... ");
                        handleAccept(key);
                    }
                    if(key.isReadable()){
                        System.out.println("isReadable ... ");
                        handleRead(key);
                    }
                    if(key.isWritable() && key.isValid()){
                        handleWrite(key);
                    }
                    if(key.isConnectable()){
                        System.out.println("it's Connectable ...");
                    }
                    iterator.remove();
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(selector != null){
                    selector.close();
                }
                if(serverSocketChannel != null){
                    serverSocketChannel.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    /**
     * 处理新的网络连接，并注册到readKey上。
     * @param key
     * @throws Exception
     */
    public void handleAccept(SelectionKey key) throws IOException{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(1024));
    }

    public void handleRead(SelectionKey key) throws IOException{
        SocketChannel socketChannel = (SocketChannel)key.channel();
        ByteBuffer buffer = (ByteBuffer)key.attachment();
        long readBytes = socketChannel.read(buffer);
        while (readBytes > 0){
            buffer.flip();
            while (buffer.hasRemaining()){
                System.out.print((char)buffer.get());
            }
            System.out.println();
            buffer.clear();
            readBytes = socketChannel.read(buffer);
        }
        if(readBytes == -1){
            socketChannel.close();
            System.out.println(" socketChannel is closed ... ");
        }
    }

    public void handleWrite(SelectionKey key) throws IOException{
        ByteBuffer buffer = (ByteBuffer)key.attachment();
        buffer.flip();
        SocketChannel socketChannel = (SocketChannel)key.channel();
        while (buffer.hasRemaining()){
            socketChannel.write(buffer);
        }
        buffer.compact();

    }

    @Test
    public void byteBufferForTest(){
        RandomAccessFile aFile = null;
        FileChannel fc = null;
        URL fileUrl = NioAction.class.getClassLoader().getResource("testForByteBuffer.zip");
        String path = fileUrl.getFile();
        MappedByteBuffer mbb = null;
        try {
            aFile = new RandomAccessFile(path, "rw");
            fc = aFile.getChannel();
            long start = System.currentTimeMillis();
            ByteBuffer buffer = ByteBuffer.allocate((int)aFile.length());
            int readBytes = fc.read(buffer);
            System.out.println(buffer);
            System.out.println("byteBufferForTest read bytes " + readBytes + "---> " + (char)buffer.get(10));
            long end = System.currentTimeMillis();
            System.out.println("byteBufferForTest cost time ---> " + (end - start) + "ms");

            mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, aFile.length());
            System.out.println("MappedByteBufferForTest ---> " + (char)mbb.get(10));
            long end1 = System.currentTimeMillis();
            System.out.println("MappedByteBufferForTest cost time ---> " + (end1 - end)  + "ms");

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                clean(mbb);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * 处理MappedByteBuffer对象只有在gc时回收的问题，此方法随时回收
     * @param buffer
     * @throws Exception
     */
    public void clean(final Object buffer) throws Exception{
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                    getCleanerMethod.setAccessible(true);
                    sun.misc.Cleaner cleaner = (sun.misc.Cleaner)getCleanerMethod.invoke(buffer, new Object[0]);
                    cleaner.clear();
                    System.out.println("sun.misc.Cleaner ... ");
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    @Test
    public void gatherForTest(){
        ByteBuffer header = ByteBuffer.allocate(10);
        ByteBuffer body = ByteBuffer.allocate(10);

        byte[] b1 = {'0', '1', '4'};
        byte[] b2 = {'2', '3'};
        header.put(b1);
        body.put(b2);

        ByteBuffer[] buffers = {header, body};

        URL fileUrl = NioAction.class.getClassLoader().getResource("scattingAndGather.txt");
        String path = fileUrl.getFile();
        try {
            FileOutputStream os = new FileOutputStream(path);
            FileChannel fc = os.getChannel();
            header.rewind();
            body.rewind();
            fc.write(buffers);
            fc.force(true);
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * NIO sink、source DEMO
     */
    @Test
    public void pipeForTest(){
        Pipe pipe = null;
        ExecutorService exec = Executors.newFixedThreadPool(2);
        try {
            pipe = Pipe.open();
            final Pipe pipeTemp = pipe;

            exec.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    Pipe.SinkChannel sinkChannel = pipeTemp.sink();//获取sink通道
                    System.out.println("SinkChannel is started ...");
                    while (true){
                        TimeUnit.SECONDS.sleep(2);
                        String newData = "Pipe Test At Time " + System.currentTimeMillis();
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        buf.clear();
                        buf.put(newData.getBytes());
                        buf.flip();
                        while (buf.hasRemaining()){
                            System.out.println(buf);
                            sinkChannel.write(buf);
                        }

                    }
                }
            });

            exec.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    Pipe.SourceChannel sourceChannel = pipeTemp.source();
                    System.out.println("sourceChannel is started ...");
                    while (true){
                        TimeUnit.SECONDS.sleep(4);
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        buf.clear();
                        int readBytes = sourceChannel.read(buf);
                        System.out.println("readBytes count -> " + readBytes);
                        while (readBytes > 0){
                            buf.flip();
                            byte[] b = new byte[readBytes];
                            int i = 0;
                            while (buf.hasRemaining()){
                                b[i] = buf.get();
                                System.out.printf("%X", b[i]);
                                i++;
                            }
                            String s = new String(b);
                            System.out.println("sourceChannel ---> " + s);
                            readBytes = sourceChannel.read(buf);
                        }
                    }
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                TimeUnit.SECONDS.sleep(10);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            exec.shutdown();
        }
    }



}





















