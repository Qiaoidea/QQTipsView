package code.qiao.com.demo.tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FragmentPagerItem {

    private final int iconId;
    private final String mTitle;
    private final Class<? extends Fragment> mFragmentClass;
    private final Bundle mArgs;

    protected FragmentPagerItem(final int resId, final String title, final Class<? extends Fragment> f,
                                final Bundle args) {
        iconId = resId;
        mTitle = title;
        mFragmentClass = f;
        mArgs = args;
    }

    public static FragmentPagerItem create(final int resId, final String title,
            final Class<? extends Fragment> fragmentClass) {
        return create(resId,title, fragmentClass, new Bundle());
    }

    public static FragmentPagerItem create(final int resId, final String title,
            final Class<? extends Fragment> fragmentClass,
            final Bundle args) {
        return new FragmentPagerItem(resId,title, fragmentClass, args);
    }

    public int getPageIconRes(){
        return iconId;
    }

    public CharSequence getPagerTitle() {
        return mTitle;
    }

    public Fragment newInstance(final Context context) {
        return Fragment.instantiate(context, mFragmentClass.getName(), mArgs);
    }

    public Bundle getArgs() {
        return mArgs;
    }


}
