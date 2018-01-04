package com.cl.data.protocolbuf;

import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import proto.test.example.PBTestProtos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static proto.test.example.PBTestProtos.PBTest.Corpus.*;

/**
 * Created by 亮 on 2018/1/4.
 */
public class PBForStream {

    private String filePath = null;

    private byte[] pbBytes = null;

    @Before
    public void init(){
        try {
            filePath = PBForStream.class.getClassLoader().getResource("PBForWrite.pro").getFile();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void wirteForTest() throws Exception{
        FileOutputStream outputStream = new FileOutputStream(new File(filePath));
        PBTestProtos.PBTest pbTestProtos = PBTestProtos.PBTest.newBuilder()
                .setId(2)
                .setCorpus(IMAGES)
                .addResult(PBTestProtos.PBTest.Result.newBuilder().setUrl("http").build())
                .addResult(PBTestProtos.PBTest.Result.newBuilder().setUrl("ftp").build())
//                .setQuery("abc")
                .build();
        pbTestProtos.writeTo(outputStream);
    }

    @Test
    public void readForTest() throws Exception{
        FileInputStream inputStream = new FileInputStream(new File(filePath));
        PBTestProtos.PBTest pbTest= PBTestProtos.PBTest.parseFrom(inputStream);
        System.out.println(pbTest.toString());
    }

    @Test
    public void writeByteArrayForTest(){
        PBTestProtos.PBTest pbTestProtos = PBTestProtos.PBTest.newBuilder()
                .setId(2)
                .setCorpus(IMAGES)
                .addResult(PBTestProtos.PBTest.Result.newBuilder().setUrl("http").build())
                .addResult(PBTestProtos.PBTest.Result.newBuilder().setUrl("ftp").build())
                .build();
        //获取序列化后的字节数组
        pbBytes = pbTestProtos.toByteArray();
        System.out.println(pbBytes.length);
    }

    @Test
    public void readByteArrayForTest(){
        try {
            writeByteArrayForTest();
            PBTestProtos.PBTest pbTest= PBTestProtos.PBTest.parseFrom(pbBytes);
            System.out.println(pbTest.toString());
        }catch (InvalidProtocolBufferException e){
            e.printStackTrace();
        }

    }
}
