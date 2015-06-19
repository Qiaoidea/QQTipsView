package activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import code.qiao.com.demo.R;
import code.qiao.com.tabview.TabItemView;
import code.qiao.com.tipsview.TipsView;

public class MainActivity extends FragmentActivity{
    private TabItemView []tabs = new TabItemView[4];
    private MessageListFragment testListFragment = new MessageListFragment();
    private Fragment []fragments  = new Fragment[]{testListFragment,new TabFragment(),new TabFragment(),new TabFragment()};
    private TipsView tipsView;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        initViews();
    }

    public void initViews(){
        viewPager = (ViewPager)findViewById(R.id.viewpager_container);
        tipsView = (TipsView)findViewById(R.id.tip);
        testListFragment.setTipsView(tipsView);
        initTabs();
        bindViewPager();
    }

    public void initTabs(){
        int tabRid = R.id.tab0;
        for(int i=0;i<4;i++){
            final TabItemView itemView = (TabItemView)findViewById(tabRid+i);
            itemView.setNotifyNum((int)(Math.random()*9));
            final View notify = itemView.getNotifyView();
            tipsView.attach(notify, new TipsView.Listener(){
                @Override
                public void onStart() {
                    notify.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onCancel() {
                    notify.setVisibility(View.VISIBLE);
                }
            });
            final int position = i;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     onTabItemClick(position);
                }
            });
            tabs[i] = itemView;
        }
        tabs[0].setIconAlpha(1f);
    }

    public void bindViewPager(){
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments[i];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels){
                if (positionOffset > 0){
                    tabs[position].setIconAlpha(1 - positionOffset);
                    tabs[position+1].setIconAlpha(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void onTabItemClick(int postion){
        for(TabItemView item: tabs){
            item.setIconAlpha(0f);
        }
        tabs[postion].setIconAlpha(1f);
        viewPager.setCurrentItem(postion);
    }
}
