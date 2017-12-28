package com.cl.data.manager;

import com.cl.data.entry.DailyOrder;
import com.cl.data.entry.Diff;
import com.cl.data.util.DateUtils;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.Test;

import java.util.List;

/**
 * Created by 亮 on 2017/12/12.
 */
public class ManagerForTest {

    @Test
    public void orderCacheManagerForTest() throws Exception{
        long day1 = DateUtils.DateToLong("2017-10-01 00:00:00");
        long day1_1 = DateUtils.DateToLong("2017-10-03 00:00:00");
        long day1_2 = DateUtils.DateToLong("2017-10-04 00:00:00");
        long day2 = DateUtils.DateToLong("2017-10-01 00:00:00");
        long day2_1 = DateUtils.DateToLong("2017-10-02 00:00:00");
        long day2_2 = DateUtils.DateToLong("2017-10-03 00:00:00");
        long day2_3 = DateUtils.DateToLong("2017-10-05 00:00:00");

        DailyOrder do1 = new DailyOrder(1234, day1, "AOC");
        do1.setOrderId(12).setResvDate(day1);

        DailyOrder do3 = new DailyOrder(1234, day1_1, "SDF");
        do3.setOrderId(12);

        DailyOrder do4 = new DailyOrder(1234, day1_2, "AOC");
        do4.setOrderId(12);

        //===========================================

        DailyOrder do2 = new DailyOrder(1234, day2, "WER");
        do2.setOrderId(13);

        DailyOrder do5 = new DailyOrder(1234, day2_1, "AOC");
        do5.setOrderId(13);

        DailyOrder do6 = new DailyOrder(1234, day2_2, "WER");
        do6.setOrderId(13);

        DailyOrder do8 = new DailyOrder(1234, day2_2, "WER");
        do8.setOrderId(13);

        DailyOrder do7 = new DailyOrder(1234, day2_3, "FFF");
        do7.setOrderId(13);

        OrderCacheManager dcm = new OrderCacheManager();
        dcm.addOrderForHotel(do1);
//        dcm.addDiffForOrder(d1);
        dcm.addOrderForHotel(do2);
//        dcm.addDiffForOrder(d2);
        dcm.addOrderForHotel(do3);
//        dcm.addDiffForOrder(d3);
        dcm.addOrderForHotel(do4);
//        dcm.addDiffForOrder(d4);
        dcm.addOrderForHotel(do5);
//        dcm.addDiffForOrder(d5);
        dcm.addOrderForHotel(do6);
        dcm.addOrderForHotel(do7);
        dcm.addOrderForHotel(do8);

        List<DailyOrder> lists = dcm.getCacheOrders().get(1234).getElements();
        List<Diff> diffs = dcm.getCacheDiffs().get(1234).getElements();
        System.out.println(lists.size());
        System.out.println(diffs.size());

        for (DailyOrder order : lists) {
            if(order.getPrevious() != null){
                do{
                    System.out.println(order + "  V " );
                    order = order.getPoster();
                }while (order != null);
            }else{
                continue;
            }
        }
        System.out.println("================ diff underline ====================");
        for (Diff diff : diffs) {
            if(diff.getPrevious() != null){
                do{
                    System.out.println(diff + "  V " );
                    for (int i = 0; diff.getDiff() != null && i < diff.getDiff().length; i++){
                        System.out.println("---> " + StringUtils.newStringUtf8(diff.getDiff()[i]));
                    }
                    diff = diff.getPoster();
                }while (diff != null);
            }else{
                continue;
            }
        }




    }


    @Test
    public void testForOther(){
        String[] strings = "1::".split(":");
        System.out.println(strings.length);
    }
}
