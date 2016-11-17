package cn.xz.mytodo.util;

import java.util.Random;

/**
 * <p>desc :
 * <p>date : 2016-4-3 上午9:00:46
 */
public class RandomUtil {
    public static String GenRandLetterAndNumberStr(int length) {
        Random rand = null;
        Object initLock = new Object();
        char[] charArr = null;
        if (length < 1) {
            return null;
        }
        if (rand == null) {
            synchronized (initLock) {
                if (rand == null) {
                    rand = new Random();
                    charArr = ("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toLowerCase().toCharArray();
                }
            }
        }
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < length; i++) {
            int j = rand.nextInt(charArr.length);
            sb.append(charArr[j]);
        }
        return sb.toString();
    }

    public static String GenRandNumberStr(int length) {
        Random rand = null;
        Object initLock = new Object();
        char[] charArr = null;
        if (length < 1) {
            return null;
        }
        if (rand == null) {
            synchronized (initLock) {
                if (rand == null) {
                    rand = new Random();
                    charArr = ("0123456789").toCharArray();
                }
            }
        }
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < length; i++) {
            int j = rand.nextInt(charArr.length);
            sb.append(charArr[j]);
        }
        return sb.toString();
    }
}
