package com.cl.data.util;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.helpers.DateTimeDateFormat;

import java.util.Date;

/**
 * Created by 亮 on 2017/12/13.
 */
public class DateUtils {

    /**
     * 日期字符串转换为长整型
     * @param dateStr
     * @return
     * @throws Exception
     */
    public static Long DateToLong(String dateStr) throws Exception{
        return DateTimeDateFormat.getDateTimeInstance().parse(dateStr).getTime();
    }

    /**
     * 长整型转换为日期字符串
     * @param dateLong
     * @return
     * @throws Exception
     */
    public static String LongToDate(Long dateLong) throws Exception{
        Date d = new Date();
        d.setTime(dateLong);
        return DateFormatUtils.format(d, "yyyy-MM-dd 00:00:00");
    }

    /**
     * 以给定时间为准，提前或推后days天的时间，长整型
     * @param dateLong
     * @param days
     * @return
     * @throws Exception
     */
    public static Long longToPreDate(Long dateLong, int days) throws Exception{
        Date d = new Date();
        d.setTime(dateLong);
        return org.apache.commons.lang.time.DateUtils.addDays(d, days).getTime();
    }
}
