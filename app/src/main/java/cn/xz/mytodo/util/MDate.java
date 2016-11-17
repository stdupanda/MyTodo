package cn.xz.mytodo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MDate {
    private static SimpleDateFormat sdf_all = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf_day = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm:ss");

    private MDate(){}

    /**
     * 返回yyyy-MM-dd HH:mm:ss格式的当前时间
     * @return
     */
    public static String getDate(){
        return sdf_all.format(new Date());
    }

    /**
     * 返回yyyy-MM-dd格式的当前时间
     * @return
     */
    public static String getDay() {
        return sdf_day.format(new Date());
    }

    /**
     * 返回HH:mm:ss格式的当前时间
     * @return
     */
    public static String getTime() {
        return sdf_time.format(new Date());
    }

    /**
     * 按照yyyy-MM-dd HH:mm:ss格式化时间
     * @param date
     * @return string
     */
    public static String format(Date date) {
        if (null == date) {
            return "";
        }
        return sdf_all.format(date);
    }

    /**
     * 按照yyyy-MM-dd格式化时间
     * @param date
     * @return string
     */
    public static String formatDate(Date date) {
        if (null == date) {
            return "";
        }
        return sdf_day.format(date);
    }

    /**
     * 按照HH:mm:ss格式化时间
     * @param date
     * @return string
     */
    public static String formatTime(Date date) {
        if (null == date) {
            return "";
        }
        return sdf_time.format(date);
    }
}
