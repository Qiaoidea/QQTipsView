package code.qiao.com.demo.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import code.qiao.com.demo.R;
import code.qiao.com.slide.SlideLayout;
import code.qiao.com.tipsview.TipsView;


/**
 * Created by bingosoft on 15/6/19.
 */
public class MessageListFragment extends ListFragment {
    private int itemCount = 28;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bindList(inflater);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setHorizontalScrollBarEnabled(false);
        getListView().setDivider(new ColorDrawable(0x000000));
    }

    public void bindList(final LayoutInflater inflater){
        setListAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return itemCount;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item, null);
                    convertView.setTag(convertView.findViewById(R.id.notify_text));
                }
                final TextView textView = (TextView) convertView.getTag();
                textView.setText("" + (int) (Math.random() * 10));
                Log.e(getClass().getSimpleName(), "getView: .........");

                final SlideLayout sldeView = (SlideLayout) convertView;
                sldeView.setCurrentItem(0, 0);
                TipsView.create(getActivity()).attach(textView //);
                        , new TipsView.DragListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onCancel() {
                        textView.setVisibility(View.VISIBLE);
                    }
                });
                return convertView;
            }
        });
    }
}
