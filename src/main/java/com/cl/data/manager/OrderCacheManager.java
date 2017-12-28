package com.cl.data.manager;

import com.cl.data.entry.DailyOrder;
import com.cl.data.entry.Diff;
import com.cl.data.entry.Order;
import com.cl.data.stream.LocalFileReader;
import com.cl.data.util.DateUtils;
import com.google.common.collect.ComparisonChain;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 亮 on 2017/12/11.
 */
public class OrderCacheManager {

    private ConcurrentHashMap<Integer, OrderForHotel> cacheOrders = null;

    private ConcurrentHashMap<Integer, DiffForOrder> cacheDiffs = null;

    public ConcurrentHashMap<Integer, OrderForHotel> getCacheOrders() {
        return cacheOrders;
    }

    public ConcurrentHashMap<Integer, DiffForOrder> getCacheDiffs() {
        return cacheDiffs;
    }

    public OrderCacheManager() {
        this.cacheOrders = new ConcurrentHashMap<Integer, OrderForHotel>();
        this.cacheDiffs = new ConcurrentHashMap<Integer, DiffForOrder>();
    }

    /**
     * 针对单个酒店，添加每日订单。其中，订单信息中记录订单重复次数
     * @param order
     * @return
     * @throws Exception
     */
    public DailyOrder addOrderForHotel(DailyOrder order) throws Exception{
        OrderForHotel orderForHotel = null;
        DailyOrder preOrder = null;
        int hotelId = order.getHotelId();

        if(cacheOrders.containsKey(hotelId)){
            orderForHotel = cacheOrders.get(hotelId);
        }else{
            orderForHotel = new OrderForHotel();
            orderForHotel.setHotelId(hotelId);
            cacheOrders.put(hotelId, orderForHotel);
        }
        preOrder = orderForHotel.findPreOrder(order, 0);//findPreOrder（订单，迭代次数初始值）
        Diff diff = null;
        Diff preDiff = null;
        if(preOrder != null){
            if(preOrder instanceof DailyOrder.OrderRoot){//标记为起始单
                order.setPrevious(preOrder);
            }else if(preOrder.getPoster() == null){//创建单向链表
                preOrder.setPoster(order);
            }
            diff = computeDiff(preOrder, order);//计算订单两天差异
        }

        //重复订单不添加
        if(order.getOrderDup() == -1){
            return order;
        }
        //添加单个酒店下的订单
        orderForHotel.create(order);
        if(diff != null){
            addDiffForOrder(diff);
        }
        return order;
    }

    /**
     * 针对单个酒店，添加每日订单差异信息。其中，订单差异信息中记录订单重复次数
     * @param diff
     * @return
     * @throws Exception
     */
    public Diff addDiffForOrder(Diff diff) throws Exception{
        DiffForOrder diffForOrder = null;
        Diff preDiff = null;
        int hotelId = diff.getHotelId();

        if(cacheDiffs.containsKey(hotelId)){
            diffForOrder = cacheDiffs.get(hotelId);
        }else{
            diffForOrder = new DiffForOrder();
            diffForOrder.setHotelId(hotelId);
            cacheDiffs.put(hotelId, diffForOrder);

        }
        preDiff = diffForOrder.findPreDiff(diff, 0);//findPreOrder（订单，迭代次数初始值）
        if(preDiff != null){
            if(preDiff instanceof Diff.DiffRoot){//标记为起始单
                diff.setPrevious(preDiff);
            }else if(preDiff.getPoster() == null){//创建单向链表
                preDiff.setPoster(diff);
            }
        }
        //重复订单差异不添加
        if(diff.getOrderDup() == -1){
            return diff;
        }
        //添加单个酒店下的订单差异
        diffForOrder.create(diff);
        return diff;
    }

    /**
     * 加载差异字段配置文件,根据配置文件中，指定字段的出现顺序
     * @return
     */
    public List initDiffFields(DailyOrder pre, DailyOrder cur){
        URL cfgUrl = getClass().getClassLoader().getResource("diffFields.cfg");
        LocalFileReader lfr = new LocalFileReader(cfgUrl.getFile());
        List<String> fieldCfgs = lfr.readCfgFile();
        Object preField = null;
        Object curField = null;
        String[] metas = null;
        List<String> result = null;
        Field tempField = null;
        try{
            result = new ArrayList<>();
            for(String field : fieldCfgs){
//                metas = field.split(":");
                tempField = pre.getClass().getDeclaredField(field);
                tempField.setAccessible(true);
                preField = tempField.get(pre);
                tempField = cur.getClass().getDeclaredField(field);
                tempField.setAccessible(true);
                curField = tempField.get(cur);
                result.add(field + ":" + (preField == null ? "" : preField) + ":" + (curField == null ? "" : curField));
            }
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 订单差异计算，以当天订单为基础，比对前一天订单。
     * 订单为Root单时，设置diff为Root型Diff
     * @param pre 前一天订单
     * @param cur 当天订单
     * @return
     */
    public Diff computeDiff(DailyOrder pre, DailyOrder cur){
        Diff diff = cur == null ? null : new Diff();
        if(diff == null) return diff;
        if(pre instanceof DailyOrder.OrderRoot){//订单的diffRoot
            diff.setPrevious(new Diff.DiffRoot(cur.getOrderId()))
            .setHotelId(cur.getHotelId())
            .setOrderId(cur.getOrderId())
            .setOrderTime(cur.getOrderTime());
            return diff;
        }
        List<String> fieldCfgs = initDiffFields(pre, cur);
        String[] metas = null;
        try {
            diff.setHotelId(cur.getHotelId())
                    .setOrderId(cur.getOrderId())
                    .setOrderTime(cur.getOrderTime())
                    .setDiff(new byte[fieldCfgs.size()][]);
            int index = 0;
            for(String f : fieldCfgs){
                metas = f.split(":");
                if(metas.length == 1){//比较差异的两天订单中，某字段的值均为null或空串
                    continue;
                }
                if(metas.length == 2 || !metas[1].equals(metas[2])){
                    diff.getDiff()[index++] = org.apache.commons.codec.binary.StringUtils.getBytesUtf8(String.join(":", metas));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return diff;
    }

    /**
     * 酒店订单数据缓存类，以酒店为集合，划分订单数据
     */
    static class OrderForHotel extends Order<Long, DailyOrder>{

        private int hotelId = -1;

        public void setHotelId(int hotelId) {
            this.hotelId = hotelId;
        }

        private OrderForHotel() {
            super(new ArrayList<DailyOrder>());
        }

        /**
         * 找到当前订单后，返回前一天的订单，没检索到或检索到重复单，返回null
         * @param order 当前订单
         * @return
         */
        public DailyOrder findPreOrder(DailyOrder order, int recursiveNum) throws Exception{
            DailyOrder temp = null;
            long preDate = 0l;
            int e = search(getElements(), order.getKey());
            if(e >= 0){
                temp = getElements().get(e);
                if(order.getHotelId() == -1){//查到之前的订单
                    return temp;
                }else{//统计相同营业日期下的重复订单次数
                    temp.setOrderDup(temp.getOrderDup() + 1);
                    order.setOrderDup(-1);//标注此订单为重复订单
                    return null;
                }
            }else{//不存在重复单：1、此单为首单；2、此单为中间态单
                preDate = DateUtils.longToPreDate(order.getOrderTime(), -1);
                if(getElements().size() == 0 || recursiveNum > 90){//历史90天无此订单，退出查询
                    return new DailyOrder.OrderRoot(order.getOrderId());//标记订单为起始单
                }
                temp = new DailyOrder().setOrderTime(preDate).setOrderId(order.getOrderId()).setHotelId(-1);
                return findPreOrder(temp, ++recursiveNum);
            }
        }

    }


    /**
     * 订单差异数据缓存类，按订单营业日期、订单ID的ASC排序
     */
    static class DiffForOrder extends Order<Long, Diff>{
        private int hotelId = -1;

        public void setHotelId(int hotelId) {
            this.hotelId = hotelId;
        }

        private DiffForOrder() {
            super(new ArrayList<Diff>());
        }

        /**
         * 找到当前订单后，返回前一天的订单，没检索到或检索到重复单，返回null
         * @param diff 当前订单
         * @return
         */
        public Diff findPreDiff(Diff diff, int recursiveNum) throws Exception{
            Diff temp = null;
            long preDate = 0l;
            int e = search(getElements(), diff.getKey());
            if(e >= 0){
                temp = getElements().get(e);
                if(diff.getHotelId() == -1){//查到之前的订单
                    return temp;
                }else{//统计相同营业日期下的重复订单次数
                    temp.setOrderDup(temp.getOrderDup() + 1);
                    diff.setOrderDup(-1);//标注此订单为重复订单
                    return null;
                }
            }else{//不存在重复单：1、此单为首单；2、此单为中间态单
                preDate = DateUtils.longToPreDate(diff.getOrderTime(), -1);
                if(getElements().size() == 0 || recursiveNum > 90){//历史90天无此订单，退出查询
                    return new Diff.DiffRoot(diff.getOrderId());//标记订单为起始单
                }
                temp = new Diff().setOrderTime(preDate).setOrderId(diff.getOrderId()).setHotelId(-1);
                return findPreDiff(temp, ++recursiveNum);
            }
        }
    }







}
