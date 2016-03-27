package code.qiao.com.demo.tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FragmentPagerItemAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private final List<FragmentPagerItem> mItems;
    private final SparseArray<Fragment> mPageReferenceMap;
    private OnInstantiateFragmentListener mListener;

    private FragmentPagerItemAdapter(final Context context, final FragmentManager fm,
                                     final List<FragmentPagerItem> items) {
        super(fm);
        mContext = context;
        mItems = items;
        mPageReferenceMap = new SparseArray<Fragment>();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final Fragment f = (Fragment) super.instantiateItem(container, position);
        mPageReferenceMap.put(position, f);
        if (mListener != null) {
            mListener.onInstantiate(position, f, mItems.get(position).getArgs());
        }
        return f;
    }

    @Override
    public Fragment getItem(final int position) {
        return mItems.get(position).newInstance(mContext);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public Fragment getFragment(final int position) {
        return mPageReferenceMap.get(position);
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return mItems.get(position).getPagerTitle();
    }

    public int getPageIconRes(final int position) {
        return mItems.get(position).getPageIconRes();
    }

    public void setOnInstantiateFragmentListener(final OnInstantiateFragmentListener l) {
        mListener = l;
    }

    public interface OnInstantiateFragmentListener {

        void onInstantiate(final int position, final Fragment fragment, final Bundle args);

    }

    public static class Builder {

        private final FragmentActivity mActivity;
        private final List<FragmentPagerItem> mItems;

        public Builder(final FragmentActivity activity) {
            mActivity = activity;
            mItems = new ArrayList<>();
        }

        public Builder add(final FragmentPagerItem item) {
            mItems.add(item);
            return this;
        }

        public Builder add(final int iconIesId,final String title, final Class<? extends Fragment> clazz) {
            return add(FragmentPagerItem.create(iconIesId,title, clazz));
        }

        public Builder add(final int iconIesId,final int titleResId, final Class<? extends Fragment> clazz) {
            return add(FragmentPagerItem.create(iconIesId,mActivity.getString(titleResId), clazz));
        }

        public Builder add(final int iconIesId,final int titleResId, final Class<? extends Fragment> clazz,
                final Bundle args) {
            return add(FragmentPagerItem.create(iconIesId,mActivity.getString(titleResId), clazz, args));
        }

        public FragmentPagerItemAdapter build() {
            return new FragmentPagerItemAdapter(mActivity, mActivity.getSupportFragmentManager(),
                    mItems);
        }
    }

}
