package cn.xz.mytodo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xz.mytodo.fragment.MyFragmentPagerAdapter;
import cn.xz.mytodo.util.MLog;
import cn.xz.mytodo.util.MToast;

public class MainActivity extends FragmentActivity
        implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    //计数是从零开始！
    /**
     * 番茄钟
     */
    public static final int PAGE_CLOCK = 0;
    /**
     * 待办列表
     */
    public static final int PAGE_TODO = 1;
    /**
     * 更多
     */
    public static final int PAGE_MORE = 2;

    private long mExitTime = 0;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private MyFragmentPagerAdapter mAdapter;

    @BindView(R.id.tv_top)
    TextView tvTop;

    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.rb_clock)
    RadioButton rbClock;
    @BindView(R.id.rb_todo)
    RadioButton rbTodo;
    @BindView(R.id.rb_more)
    RadioButton rbMore;

    /*private <T extends View> T bindView(int viewId) {
        try {
            return (T) findViewById(viewId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        radioGroup.setOnCheckedChangeListener(this);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MLog.log(supportFragmentManager);
        mAdapter = new MyFragmentPagerAdapter(supportFragmentManager);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);

        //初始化时默认显示番茄钟
        //rbClock.setChecked(true);
        rbTodo.performClick();
    }

    //---onCheckedChanged implement start
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_clock:
                viewPager.setCurrentItem(PAGE_CLOCK);
                tvTop.setText(getText(R.string.clock));
                break;
            case R.id.rb_todo:
                viewPager.setCurrentItem(PAGE_TODO);
                tvTop.setText(getText(R.string.todo));
                break;
            case R.id.rb_more:
                viewPager.setCurrentItem(PAGE_MORE);
                tvTop.setText(getText(R.string.more));
                break;
        }
    }
    //---onCheckedChanged implement stop

    //---OnPageChangeListener implement start
    //重写ViewPager页面切换的处理方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //整个page scroll的过程都会触发
        //MLog.log("onPageScrolled:" + position);
    }

    @Override
    public void onPageSelected(int position) {
        MLog.log("onPageSelected:" + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (viewPager.getCurrentItem()) {
                case PAGE_CLOCK:
                    rbClock.setChecked(true);
                    break;
                case PAGE_TODO:
                    rbTodo.setChecked(true);
                    break;
                case PAGE_MORE:
                    rbMore.setChecked(true);
                    break;
            }
        }
    }
    //---OnPageChangeListener implement stop

    //连续按两次退出系统
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                MToast.Show(this, "再按一次退出程序！");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
