package com.prism.springas.tools;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
    -时间操作工具类
 */
@Component
public class DateTool {

    /*
        -基本时间操作工具
        -将Calendar.getTime格式化完成的字符串二次格式化为Date
     */
    public Date simpleFormat(String date) throws Exception{
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
    }


    /**
     * -获取 ±X小时内的时间
     * @param date      当前设定时间
     * @param hours     指定小时数（±X）
     * @return
     * @throws Exception
     */
    public Date CalendarTimeFormatHour(Date date,int hours) throws Exception{
        Calendar ca=Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.HOUR_OF_DAY, hours);
        return  simpleFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ca.getTime()));
    }


}
