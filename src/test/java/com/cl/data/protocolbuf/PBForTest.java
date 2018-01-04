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

    private int sint32 = 0;

    private byte[] i32buf = null;

    @Before
    public void initParams(){
        //varint演示所需变量
        varint32 = 86942;
        i32buf = new byte[5];

        //sint32演示所需变量
        sint32 = -500;


    }


    /**
     * Varint32 源码编码示例(PB中针对正整数，如果提前预知字段值可能取负数，应采用sint32 / sint64 数据类型)：
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

    /**
     * sint32 源码编码示例：
     */
    @Test
    public void writeSint32(){
        int zigzagInt32Encode = zigzagInt32Encode(sint32);
        zigzagInt32Decode(zigzagInt32Encode);
        writeVarint32(zigzagInt32Encode);

    }



    /**
     * PB中针对sint32（PB中只针对负整数）的zigzag辅助编码（编码过程）实现。其中，
     * 1、source << 1，为带符号位向左移动1位，低位补0，数学解释为将负数绝对值存储在高31位；
     * 2、source >> 31，为带符号位向右移动31位，高位补1，数学解释为将符号位保存在最低位，同时高31位全部为1；
     * 3、(source << 1) ^ (source >> 31)，异或运算结果为，将高31位取反，同时保证最高位为0（既符号位，表示正整数），最终转换为正整数；
     * 4、PB中，zigzag编码使得，负整数越大（既绝对值越小），转换成的正整数越小
     * @param source
     * @return
     */
    public int zigzagInt32Encode(int source){
        System.out.println(String.format("zigzag编码前，输入值为[%s]", byteToBit(intToByteArray(source))));
        int target = (source << 1) ^ (source >> 31);
        System.out.println(String.format("zigzag编码后，输入值为[%s]", byteToBit(intToByteArray(target))));
        return target;
    }


    /**
     * PB中针对sint32（PB中只针对负整数）的zigzag辅助编码（解码过程）实现。其中，
     * 1、source >>> 1，为无符号向右移动1为，最高位补0，低31位为原数值（zigzag编码后得到的正整数）；
     * 2、-(source & 1)，将source值转换为-1，十六进制形式为 FF FF FF FF；
     * 3、(source >>> 1) ^ -(source & 1)，异或运算（全部32位取反）结果为，最高位为1（负号），低31位由正整数取反后，得到原始的负整数对应的二进制补码形式；
     * @param source
     * @return
     */
    public int zigzagInt32Decode(int source){
        System.out.println(String.format("zigzag解码前，输入值为[%s]", byteToBit(intToByteArray(source))));
        int target = (source >>> 1) ^ -(source & 1);
        System.out.println(String.format("zigzag解码后，输入值为[%s]", byteToBit(intToByteArray(target))));
        return target;
    }

    /**
     * PB中，VarInt32编码
     * @param pos
     */
    public void writeVarint32(int pos){
        System.out.println(String.format("经过[varint32]方式压缩前，字节长度为[%s]，二进制表示为[%s]", intToByteArray(pos).length, byteToBit(intToByteArray(pos))));
        int idx = 0;
        while (true){
            if((pos & ~0x7F) == 0){
                //如果是最后一次取出(或只有7个字节)，则在最高位添加0构成1个字节
                i32buf[idx++] = (byte)pos;
                break;
            }else {
                // 取出字节串末7位
                // 对于上述取出的7位：在最高位添加1构成一个字节
                i32buf[idx++] = (byte)((pos & 0x7F) | 0x80);
                //通过将字节串整体往右移7位，继续从字节串的末尾选取7位，直到取完为止
                pos >>>= 7;
            }
        }
        // 将上述形成的每个字节 按序拼接 成一个字节串
        // 即该字节串就是经过Varint编码后的字节
        i32buf = Arrays.copyOf(i32buf, idx);
        System.out.println(String.format("经过[varint32]方式压缩后，字节长度为[%s]，二进制表示为[%s]", i32buf.length, byteToBit(i32buf)));
    }



    @Test
    public void hash(){
        int j = zigzagInt32Encode(-2);
        zigzagInt32Decode(j);
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
