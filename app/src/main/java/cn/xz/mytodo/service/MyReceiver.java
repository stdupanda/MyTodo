package cn.xz.mytodo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.xz.mytodo.util.MLog;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MLog.log("接收到广播：" + intent.getAction() + "，启动KeepAliveService");
        context.startService(new Intent(context, KeepAliveService.class));
    }
}
