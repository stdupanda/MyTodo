package cn.xz.mytodo.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import cn.xz.mytodo.MainActivity;
import cn.xz.mytodo.util.MLog;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 3;
    private ClockFragment clockFragment = null;
    private TodoFragment todoFragment = null;
    private MoreFragment moreFragment = null;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        clockFragment = new ClockFragment();
        todoFragment = new TodoFragment();
        moreFragment = new MoreFragment();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MLog.log("position:" + position);
//        MLog.log("container:" + container);
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        MLog.log("destroyItem:" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_CLOCK: {
                fragment = clockFragment;
                break;
            }
            case MainActivity.PAGE_TODO: {
                fragment = todoFragment;
                break;
            }
            case MainActivity.PAGE_MORE:
                fragment = moreFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
