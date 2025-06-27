package com.example.web.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormatUtil {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String DateToString(Date date, String patten){
        sdf = new SimpleDateFormat(patten);
        return sdf.format(date);
    }

    public static void DateToString1(LocalDateTime dateTime, String patten){
        dtf = DateTimeFormatter.ofPattern(patten);
        System.out.println(dtf.format(dateTime));
        System.out.println(DateTimeFormatter.ISO_DATE_TIME.format(dateTime));
    }

    public static void main(String[] args) {
        System.out.println(DateFormatUtil.DateToString(new Date(), "yyyy-MM"));
        DateFormatUtil.DateToString1(LocalDateTime.now(), "yyyy-MM-dd hh:mm:ss");
    }
}
