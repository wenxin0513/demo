package com.example.myexcel.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class DateUtil {


    /**
     * 时间数字正则表达式
     */
    private static final Pattern PATTERN_DATE_NUMBER = Pattern.compile("^[1-9]\\d{10,}$");

    /**
     * 数字、小数正则表达式
     */
    private static final Pattern PATTERN_DATE_DECIMAL = Pattern.compile("[0-9]+\\.*[0-9]*");


    /**
     * 是否为时间类数值
     *
     * @param v 内容
     * @return true/false
     */
    private  static boolean isDateNumber(String v) {
        return PATTERN_DATE_NUMBER.matcher(v).matches();
    }


    /**
     * 是否为Excel数字日期
     *
     * @param v 内容
     * @return true/false
     */
    private static  boolean isDateDecimalNumber(String v) {
        return PATTERN_DATE_DECIMAL.matcher(v).matches();
    }


    /**
     * @description TODO
     * @param input  原始日期字符串
     * @param formatString 日期格式
     * @return boolean
     */
    public static boolean isValidDate(String input, String formatString) {
        if (isDateNumber(input) ){
            return true;
        }
        if (isDateDecimalNumber(input)) {
            return true;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);
            format.setLenient(false);
            format.parse(input);
        } catch (ParseException e) {
            return false;

        } catch (IllegalArgumentException e) {
            return false;

        }
        return true;
    }

}
