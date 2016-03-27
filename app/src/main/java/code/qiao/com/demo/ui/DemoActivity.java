package code.qiao.com.demo.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import code.qiao.com.demo.R;
import code.qiao.com.demo.tab.FragmentPagerItemAdapter;
import code.qiao.com.demo.tab.TabLayout;
import code.qiao.com.tipsview.TipsView;


public class DemoActivity extends AppCompatActivity{
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        initViews();
    }

    public void initViews(){
        viewPager = (ViewPager)findViewById(R.id.viewpager_container);
        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        viewPager.setAdapter(
                new FragmentPagerItemAdapter.Builder(this)
                        .add(R.drawable.tab1, "消息", MessageListFragment.class)
                        .add(R.drawable.tab2, "通讯录", ContactFragment.class)
                        .add(R.drawable.tab3, "发现", TabFragment.class)
                        .add(R.drawable.tab4, "我", TabFragment.class)
                        .build());
        tabLayout.setViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        TipsView.destroy(this);
        super.onDestroy();
    }
}
