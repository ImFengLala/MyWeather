package com.jluandroid.myweather.handleFragmentBack;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Fengl on 2018/3/24.
 */

public class BackHandlerHelper {

    private static final String TAG = "BackHandlerHelper";

    /**
     * 将back事件分发给FragmentManager中管理的子Fragment，如果该FragmentManager中所有的Fragment都
     * 没有处理back事件，则尝试FragmentManager.popBackStack()
     * @return 如果任何一个子Fragment处理了back事件则返回true，否则返回false
     * @see #handleBackPress(Fragment)
     * @see #handleBackPress(FragmentActivity)
     */
    public static boolean handleBackPress(FragmentManager fragmentManager) {
        Log.d(TAG, "handleBackPress: 然后第二步返回走到了这里啦");
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) {
            return false;
        }
        for (Fragment child : fragments) {
            Log.d(TAG, "handleBackPress: 对每个子Fragment判断是否处理过返回事件啦");
            if (isFragmentBackHandled(child)) {
                Log.d(TAG, "handleBackPress: 得到返回的值就知道子Fragment有没有处理过返回事件啦");
                return true;
            }
        }
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            return true;
        }
        return false;
    }

    public static boolean handleBackPress(Fragment fragment) {
        return handleBackPress(fragment.getChildFragmentManager());
    }

    public static boolean handleBackPress(FragmentActivity fragmentActivity) {
        return handleBackPress(fragmentActivity.getSupportFragmentManager());
    }

    /**
     * 判断是否处理了back事件，用于在Fragment类中调用
     * @param fragment 传入Fragment类的this即可
     * @return 若子
     */
    private static boolean isFragmentBackHandled(Fragment fragment) {
        Log.d(TAG, "isFragmentBackHandled: 调用每个子Fragment的返回处理事件啦");
        return fragment != null
                && fragment.isVisible()
                && fragment.getUserVisibleHint() // for ViewPager
                && fragment instanceof FragmentBackHandler
                && ((FragmentBackHandler)fragment).onBackPressed();
    }
}
