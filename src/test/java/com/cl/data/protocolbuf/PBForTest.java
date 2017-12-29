package com.cl.data.protocolbuf;

import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * ProtocolBuffer工具测试类，包括：
 * 1、对varint（正数）原理实现的代码演示；
 * 2、对zigzag辅助varint（负数）原理实现的代码演示；
 * Created by 亮 on 2017/12/28.
 */
public class PBForTest {

    private int varint32 = 0;

    private byte[] i32buf = null;

    @Before
    public void initParams(){
        //varint演示所需变量
        varint32 = 128;
        i32buf = new byte[5];


    }


    /**
     * Varint 源码编码示例：
     * Varint 中每个 字节 的最高位 都有特殊含义：
     * 如果是 1，表示后续的 字节 也是该数字的一部分
     * 如果是 0，表示这是最后一个字节，且剩余 7位 都用来表示数字
     * 所以，当使用Varint解码时，只要读取到最高位为0的字节时，就表示已经是Varint的最后一个字节
     */
    @Test
    public void writeVarint32(){
        System.out.println(String.format("经过[varint32]方式压缩前，字节长度为[%s]，二进制表示为[%s]", intToByteArray(varint32).length, byteToBit(intToByteArray(varint32))));
        int idx = 0;
        while (true){
            if((varint32 & ~0x7F) == 0){
                //如果是最后一次取出(或只有7个字节)，则在最高位添加0构成1个字节
                i32buf[idx++] = (byte)varint32;
                break;
            }else {
                // 取出字节串末7位
                // 对于上述取出的7位：在最高位添加1构成一个字节
                i32buf[idx++] = (byte)((varint32 & 0x7F) | 0x80);
                //通过将字节串整体往右移7位，继续从字节串的末尾选取7位，直到取完为止
                varint32 >>>= 7;
            }
        }
        // 将上述形成的每个字节 按序拼接 成一个字节串
        // 即该字节串就是经过Varint编码后的字节
        i32buf = Arrays.copyOf(i32buf, idx);
        System.out.println(String.format("经过[varint32]方式压缩后，字节长度为[%s]，二进制表示为[%s]", i32buf.length, byteToBit(i32buf)));
    }


    @Test
    public void hash(){
        int i = -1140850688;
        int j = (i << 1) ^ (i >> 31);

        System.out.println("--> " + byteToBit(intToByteArray(i)));
        System.out.println("--> " + byteToBit(intToByteArray((i << 1))));
        System.out.println("--> " + byteToBit(intToByteArray((i >> 31))));
        System.out.println("--> " + byteToBit(intToByteArray((i << 1) ^ (i >> 31))));

        System.out.println(String.format("换码前i值为[%s]", byteToBit(intToByteArray(i))));
        System.out.println(String.format("换码后i值为[%s]", byteToBit(intToByteArray(j))));
        int k = (j >>> 1) ^ -(j & 1);
        System.out.println(String.format("换码后i值为[%s]", byteToBit(intToByteArray(k))));


    }








    /**
     * 将正整数对应的字节数组，转换为二进制表示
     * @param bytes 字节数组
     * @param radix 进制表示 2：表示2进制；16：表示16进制；
     * @return
     */
    private String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);
    }

    /**
     * 将整数转换为字节数组
     * @param i
     * @return
     */
    private byte[] intToByteArray(int i){
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)((i >> 0) & 0xFF);
        return result;
    }

    /**
     * 字节数组转换为二进制0/1方式表示的字符串
     * @param bytes
     * @return
     */
    private String byteToBit(byte[] bytes){
        StringBuilder bits = new StringBuilder(" ");
        for (byte b: bytes) {
            bits.append((byte)((b >> 7) & 0x1))
                    .append((byte)((b >> 6) & 0x1))
                    .append((byte)((b >> 5) & 0x1))
                    .append((byte)((b >> 4) & 0x1))
                    .append((byte)((b >> 3) & 0x1))
                    .append((byte)((b >> 2) & 0x1))
                    .append((byte)((b >> 1) & 0x1))
                    .append((byte)((b >> 0) & 0x1))
                    .append(" ");
        }
        return bits.toString();
    }

}
