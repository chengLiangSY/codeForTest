package com.cl.data.util;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by 亮 on 2017/12/13.
 */
public class UtilsForTest {

    @Test
    public void dataUtilsForTest() throws Exception{

        String dateStr = "2017-10-01 00:00:00";
        System.out.println("DateUtils.DateToLong(dateStr) " + DateUtils.DateToLong(dateStr));
        Long time = DateUtils.DateToLong(dateStr);
        System.out.println("DateUtils.LongToDate(time) : " + DateUtils.LongToDate(time));
        Long preTime = DateUtils.longToPreDate(time, -1);
        System.out.println("DateUtils.LongToDate(preTime) : " + DateUtils.LongToDate(preTime));
        System.out.println(preTime == DateUtils.longToPreDate(time, -1));


    }
}
