package com.exam.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class TimeUtils {
    //时间的格式
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Timestamp getTimestampByString(String time){
        if(time!=null || time.length()!=0) {
            //开始时间 和结束时间将中国标准时间格式化为yyyy-MM-dd HH:mm:ss
            String metEndTemp = time.replace("GMT", "").replaceAll("\\(.*\\)", "");
            SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss", Locale.ENGLISH);
            try {
                time=format.format(format2.parse(metEndTemp));

            } catch (ParseException e) {
                System.out.println("出错啦！！！");
            }

        }
        return null;
    }

    //根据Long转化为localDateTime
    public static LocalDateTime getLocalDateTimeByLong(long time){
        Instant instant = Instant.ofEpochMilli(time);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime;
    }
}
