package code.qiao.com.demo.tab;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import code.qiao.com.tabview.TabItemView;
import code.qiao.com.tipsview.TipsView;


/**
 * Created by Administrator on 2016/3/24.
 */
public class TabLayout extends LinearLayout implements View.OnClickListener,ViewPager.OnPageChangeListener{

    private ViewPager mViewPager;

    public TabLayout(Context context) {
        super(context);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(this);
            populateTabItem();
        }
    }

    private void populateTabItem() {
        final PagerAdapter adapter = mViewPager.getAdapter();

        if(adapter instanceof FragmentPagerItemAdapter) {
            for (int i = 0; i < adapter.getCount(); i++) {
                TabItemView tabView = new TabItemView(getContext(),
                        ((FragmentPagerItemAdapter) adapter).getPageIconRes(i),
                        adapter.getPageTitle(i).toString(),(int)(Math.random()*99));

                tabView.setOnClickListener(this);
                TipsView.create((Activity) getContext()).attach(tabView.getDotView());

                LayoutParams llp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                llp.weight = 1;
                llp.topMargin = llp.bottomMargin = 10;
                addView(tabView,llp);
            }
            ((TabItemView)getChildAt(0)).setIconAlpha(1f);
        }
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < getChildCount(); i++) {
            if (v == getChildAt(i)) {
                mViewPager.setCurrentItem(i);
                return;
            }
        }
    }

    private int mScrollState;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0){
            ((TabItemView)getChildAt(position)).setIconAlpha(1 - positionOffset);
            ((TabItemView)getChildAt(position + 1)).setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mScrollState = state;
    }

    @Override
    public void onPageSelected(int position) {
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
        }
    }

}
