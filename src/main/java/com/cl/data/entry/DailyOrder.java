package com.cl.data.entry;

import com.google.common.collect.ComparisonChain;

/**
 * 订单实体信息
 * Created by 亮 on 2017/12/11.
 */
public class DailyOrder implements Order.Element<Long>, Comparable<Long>{

    //酒店ID
    private int hotelId = -1;
    //订单ID
    private int orderId = -1;
    //订单状态
    private Order.OrderState orderState = null;
    //订单营业日期
    private Long orderTime = 0l;
    //细分市场码
    private String codeForSegMarket = null;
    //预定日期
    private Long resvDate = null;
    //到店日期
    private Long arrDate = null;
    //离店日期
    private Long deptDate = null;
    //取消日期
    private Long cnlDate = null;
    //下一个营业日期的订单
    private DailyOrder poster = null;
    //前一个营业日期的订单
    private DailyOrder previous = null;
    //订单重复次数
    private int orderDup = 0;

    public int getOrderDup() {
        return orderDup;
    }

    public void setOrderDup(int orderDup) {
        this.orderDup = orderDup;
    }

    public int getOrderId() {
        return orderId;
    }

    public DailyOrder setOrderId(int orderId) {
        this.orderId = orderId;
        return this;
    }

    public int getHotelId() {
        return hotelId;
    }

    public Long getOrderTime() {
        return orderTime;
    }

    public String getCodeForSegMarket() {
        return codeForSegMarket;
    }

    public DailyOrder setOrderTime(Long orderTime) {
        this.orderTime = orderTime;
        return this;
    }

    public DailyOrder setHotelId(int hotelId) {
        this.hotelId = hotelId;
        return this;
    }

    public Long getResvDate() {
        return resvDate;
    }

    public DailyOrder setResvDate(Long resvDate) {
        this.resvDate = resvDate;
        return this;
    }

    public Long getArrDate() {
        return arrDate;
    }

    public DailyOrder setArrDate(Long arrDate) {
        this.arrDate = arrDate;
        return this;
    }

    public Long getDeptDate() {
        return deptDate;
    }

    public DailyOrder setDeptDate(Long deptDate) {
        this.deptDate = deptDate;
        return this;
    }

    public Long getCnlDate() {
        return cnlDate;
    }

    public DailyOrder setCnlDate(Long cnlDate) {
        this.cnlDate = cnlDate;
        return this;
    }

    public DailyOrder getPoster() {
        return poster;
    }

    public void setPoster(DailyOrder poster) {
        this.poster = poster;
    }

    public DailyOrder getPrevious() {
        return previous;
    }

    public void setPrevious(DailyOrder previous) {
        this.previous = previous;
    }

    public Order.OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(Order.OrderState orderState) {
        this.orderState = orderState;
    }

    public DailyOrder() {
    }

    public DailyOrder(int hotelId, Long orderTime, String codeForSegMarket) {
        this.hotelId = hotelId;
        this.orderTime = orderTime;
        this.codeForSegMarket = codeForSegMarket;
    }

    /**
     * 以订单营业日期、订单ID为key（索引）
     * @return
     */
    public Long getKey() {
        return this.orderTime + this.orderId;
    }

    public int compareTo(Long other) {
        return ComparisonChain.start().compare((Long)(this.orderTime + this.orderId), other).result();
    }

    @Override
    public String toString() {
        return "DailyOrder{" +
                "hotelId=" + hotelId +
                ", orderId=" + orderId +
                ", orderState=" + orderState +
                ", orderTime=" + orderTime +
                ", codeForSegMarket=" + codeForSegMarket +
                ", resvDate=" + resvDate +
                ", arrDate=" + arrDate +
                ", deptDate=" + deptDate +
                ", cnlDate=" + cnlDate +
                ", poster=" + poster +
                ", previous=" + previous +
                ", orderDup=" + orderDup +
                '}';
    }

    /**
     * 订单为首单的标识类，无实际意义
     */
    public static class OrderRoot extends DailyOrder{
       public OrderRoot(int orderId) {
            setOrderId(orderId);
        }
    }

}
