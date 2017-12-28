package com.cl.data.entry;

import com.google.common.collect.ComparisonChain;

import java.util.Arrays;

/**
 * Created by 亮 on 2017/12/15.
 * 订单差异类
 */
public class Diff implements Order.Element<Long>, Comparable<Long> {

    private int hotelId = 0;

    private int orderId = 0;

    private Long orderTime = 0l;
    //差异索引
    private byte[] index = null;
    //差异内容
    private byte[][] diff = null;
    //下一个营业日期的订单差异
    private Diff poster = null;
    //前一个营业日期的订单差异
    private Diff previous = null;
    //订单重复次数
    private int orderDup = 0;

    public Diff() {
    }

    public Diff(int hotelId, Long orderTime) {
        this.hotelId = hotelId;
        this.orderTime = orderTime;
    }

    public Diff getPoster() {
        return poster;
    }

    public Diff setPoster(Diff poster) {
        this.poster = poster;
        return this;
    }

    public Diff getPrevious() {
        return previous;
    }

    public Diff setPrevious(Diff previous) {
        this.previous = previous;
        return this;
    }

    public int getOrderDup() {
        return orderDup;
    }

    public Diff setOrderDup(int orderDup) {
        this.orderDup = orderDup;
        return this;
    }

    public int getHotelId() {
        return hotelId;
    }

    public Diff setHotelId(int hotelId) {
        this.hotelId = hotelId;
        return this;
    }

    public int getOrderId() {
        return orderId;
    }

    public Diff setOrderId(int orderId) {
        this.orderId = orderId;
        return this;
    }

    public Long getOrderTime() {
        return orderTime;
    }

    public Diff setOrderTime(Long orderTime) {
        this.orderTime = orderTime;
        return this;
    }

    public byte[] getIndex() {
        return index;
    }

    public Diff setIndex(byte[] index) {
        this.index = index;
        return this;
    }

    public byte[][] getDiff() {
        return diff;
    }

    public Diff setDiff(byte[][] diff) {
        this.diff = diff;
        return this;
    }

    @Override
    public Long getKey() {
        return this.orderTime + this.orderId;
    }

    @Override
    public int compareTo(Long other) {
        return ComparisonChain.start().compare((Long)(this.orderTime + this.orderId), other).result();
    }

    @Override
    public String toString() {
        return "Diff{" +
                "hotelId=" + hotelId +
                ", orderId=" + orderId +
                ", orderTime=" + orderTime +
                ", index=" + Arrays.toString(index) +
                ", diff=" + Arrays.toString(diff) +
                ", poster=" + poster +
                ", previous=" + previous +
                ", orderDup=" + orderDup +
                '}';
    }

    /**
     * 订单为首单的标识类，无实际意义
     */
    public static class DiffRoot extends Diff{
        public DiffRoot(int orderId) {
            setOrderId(orderId);
        }
    }
}
