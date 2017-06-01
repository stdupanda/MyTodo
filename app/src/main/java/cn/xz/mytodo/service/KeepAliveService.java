package cn.xz.mytodo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.xz.mytodo.util.MLog;

public class KeepAliveService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // return super.onStartCommand(intent, flags, startId);
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        Intent localIntent = new Intent();
        localIntent.setClass(this, KeepAliveService.class); // 销毁时重新启动Service
        MLog.log("KeepAliveService被停止，尝试再次启动。");
        this.startService(localIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}