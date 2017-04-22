package cn.xz.mytodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 在此欢迎 activity 中启动耗时的主界面，初始化完成后进行跳转
 * <br/>
 * 考虑到启动时的联网加载广告，也可以进行类似操作，
 * 先在此界面进行广告的联网加载，再跳转到广告 Activity
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 不设置 contentView，而是显示主题中设置的欢迎界面图片
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
