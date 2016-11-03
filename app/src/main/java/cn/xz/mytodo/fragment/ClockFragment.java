package cn.xz.mytodo.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xz.mytodo.MainActivity;
import cn.xz.mytodo.R;
import cn.xz.mytodo.common.IConst;
import cn.xz.mytodo.util.MLog;
import cn.xz.mytodo.view.CircleProgressView;

public class ClockFragment extends Fragment implements View.OnClickListener {

    public static final int NOTIFICATION_ID = 0xa01;
    private final int REQUEST_CODE = 0xb01;

    private static final int SLEEP_PERIOD = 1000;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    /**
     * 是否正在运行
     */
    boolean isRunning = false;
    /**
     * 用于在循环中判断是否需要停止当前循环
     */
    boolean ifNeedStop = false;

    /**
     * 一个番茄钟周期，单位：分钟
     */
    private int CLOCK_TIME = 1;

    @BindView(R.id.mCircleProgressView)
    CircleProgressView mCircleView;

    @BindView(R.id.tvSwitch)
    TextView tvSwitch;

    NotificationManager notificationManager;
    //Notification notification;

    /*private <T extends View> T bindView(int viewId) {
        try {
            return (T) getActivity().findViewById(viewId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_clock, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        notificationManager = (NotificationManager)
                getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences sp = getActivity()
                .getSharedPreferences(IConst.SP_FILE_NAME, Context.MODE_PRIVATE);
        CLOCK_TIME = sp.getInt(IConst.SP_KEY_CLOCK_PERIOD, IConst.SP_DEFAULT_VALUE_CLOCK_PERIOD);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        MLog.log(dm.widthPixels);
        //
        tvSwitch.getLayoutParams().width = (int) (dm.widthPixels * 0.8);

        tvSwitch.setOnClickListener(this);

        mCircleView.setProgress(0);
        mCircleView.setTxtHint1("");
        mCircleView.setTxtHint2("");
        ViewGroup.LayoutParams layoutParams = mCircleView.getLayoutParams();
        layoutParams.width = (int) (dm.widthPixels * 0.7);
        layoutParams.height = (int) (dm.widthPixels * 0.7);
        //
        //tvSwitch.performClick();
        MLog.log("更多！");
    }

    void setStatus() {
        if (isRunning) {
            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(getActivity());
            android.support.v7.app.AlertDialog dialog = builder.setTitle("提示")
                    .setMessage("确定要停止番茄钟吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ifNeedStop = true;
                        }
                    })
                    .setNegativeButton("取消", null)
                    .setCancelable(false)
                    .show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        } else {
            new ClockTask().executeOnExecutor(executorService, "");
        }
        //MToast.Show(this, "" + SystemClock.currentThreadTimeMillis());
    }

    /**
     * 设置打开应用时屏幕常量（退出后不起作用）
     *
     * @param ifOn true保持亮 false不保持亮
     */
    private void setScreenOnOff(boolean ifOn) {
        if (ifOn) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * 设置当前状态
     *
     * @param isRunningFlag 当前是否正在运行番茄钟
     */
    private void setClockStatus(boolean isRunningFlag) {
        isRunning = isRunningFlag;
        if (isRunningFlag) {
            setScreenOnOff(true);
            tvSwitch.setText(getString(R.string.stopClock));
        } else {
            setScreenOnOff(false);
            tvSwitch.setText(getString(R.string.startClock));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSwitch: {
                setStatus();
                break;
            }
            default: {
                //
            }
        }
    }

    private class ClockTask extends AsyncTask<String, Long, String> {//Params, Progress, Result

        int timeTotal = CLOCK_TIME * 60;//秒
        int timeLeft = timeTotal;

        @Override
        protected void onPreExecute() {
            setClockStatus(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            long ps;
            while (1 <= timeLeft) {
                if (ifNeedStop) {
                    return "STOP_CLOCK";
                }
                timeLeft--;

                ps = ((timeTotal - timeLeft) * 100) / timeTotal;
                publishProgress(ps);

                SystemClock.sleep(SLEEP_PERIOD);
            }
            sendNotification();
            return "STOP_CLOCK";
        }

        @Override
        protected void onPostExecute(String s) {
            if ("STOP_CLOCK".equals(s)) {
                setClockStatus(false);
                ifNeedStop = false;
            }
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            Long val = values[0];
            mCircleView.setProgress(val.intValue());
            mCircleView.setTxtHint1("" + formatNumber(timeLeft / 60) + ":" + formatNumber(timeLeft % 60));
            mCircleView.setTxtHint2("" + CLOCK_TIME + "min");
            //MLog.log(timeLeft);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            MLog.log("onCancelled");
            super.onCancelled();
        }
    }

    private String formatNumber(int num) {
        return StringUtils.leftPad(Integer.toString(num), 2, "0");
    }

    private void sendNotification() {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getActivity());
        // 此处设置的图标仅用于显示新提醒时候出现在设备的通知栏
        mBuilder.setSmallIcon(R.drawable.tomato_logo);
        mBuilder.setContentTitle("通知的标题");
        mBuilder.setContentText("通知的内容");
        Notification notification = mBuilder.build();

        // 当用户下来通知栏时候看到的就是RemoteViews中自定义的Notification布局
        RemoteViews contentView = new RemoteViews(getActivity().getPackageName(),
                R.layout.notification);
        notification.contentView = contentView;
        notification.contentView.setTextViewText(R.id.tv_syncing,
                getString(R.string.clock_finished));
        // 发送通知到通知栏时：提示声音 + 手机震动 + 点亮Android手机呼吸灯。
        // 注意！！（提示声音 + 手机震动）这两项基本上Android手机均支持。
        // 但Android呼吸灯能否点亮则取决于各个手机硬件制造商自家的设置。
        notification.defaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
        //notification.flags = Notification.FLAG_NO_CLEAR;
        notification.flags = Notification.FLAG_AUTO_CANCEL;//使得可以清除
        // 通知的时间
        notification.when = System.currentTimeMillis();
        // 需要注意的是，作为选项，此处可以设置MainActivity的启动模式为singleTop，避免重复新建onCreate()。
                /* <activity android:launchMode="singleTop" */
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//点击进入新activity，会打开多个相同activity，需要在Intent设置如下flag
        // 当用户点击通知栏的Notification时候，切换回MainActivity。
        PendingIntent pi = PendingIntent.getActivity(getActivity(), REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        MLog.log(pi);
        notification.contentIntent = pi;
        // 发送到手机的通知栏
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
