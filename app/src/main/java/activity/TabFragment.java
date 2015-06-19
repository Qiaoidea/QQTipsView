package activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by bingosoft on 15/6/18.
 */
public class TabFragment extends Fragment{
    private String mTitle = "Default";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        TextView textView = new TextView(getActivity());
        textView.setTextSize(20);
        textView.setBackgroundColor(Color.parseColor("#ffffffff"));
        textView.setGravity(Gravity.CENTER);
        textView.setText(mTitle);
        return textView;
    }

    public String getTitle(){
        return mTitle;
    }

}
