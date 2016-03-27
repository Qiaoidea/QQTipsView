package code.qiao.com.demo.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import code.qiao.com.demo.R;


/**
 * Created by bingosoft on 15/6/18.
 */
public class ContactFragment extends Fragment {
    private String mTitle = "TabFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.list_item,container,false);
    }

    public String getTitle(){
        return mTitle;
    }
}
