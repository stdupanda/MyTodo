package cn.xz.mytodo.util;

import android.util.Log;

import cn.xz.mytodo.common.IConst;

public class MLog {
    private MLog(){}
    public static void log(Object obj) {
        Log.e(IConst.LOG_TAG, "" + obj);
    }

    public static void logInfo(Object obj) {
        Log.i(IConst.LOG_TAG, "" + obj);
    }
}
