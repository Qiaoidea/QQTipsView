package code.qiao.com.tabview;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class TabItemView extends ViewGroup{
    private ItemView itemView;
    private NotifyView notifyView;

    public TabItemView(Context context, int resId, String text, int num){
        super(context);
        initWithAttr(context,null);
    }

    public TabItemView(Context context, AttributeSet attrs){
        super(context, attrs);
        initWithAttr(context,attrs);
    }

    private void initWithAttr(Context context, AttributeSet attrs){
        setPadding(0,0,0,0);
        addView(new ItemView(context,attrs));
        addView(new NotifyView(context));
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        if(child instanceof ItemView){
            this.itemView = (ItemView)child;
            super.addView(child,index,params);
        }else if(child instanceof NotifyView) {
            this.notifyView = (NotifyView) child;
            super.addView(child,index,params);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        measureChild(itemView, widthMeasureSpec, heightMeasureSpec);
        int width = itemView.getMeasuredWidth();

        Point point = itemView.getIconRightTop();
        int noheight = itemView.getBitmapWidth()/2;
        int nowidth = width - point.x + noheight/2;
        measureChild(notifyView,MeasureSpec.makeMeasureSpec(nowidth,MeasureSpec.AT_MOST),MeasureSpec.makeMeasureSpec(noheight,MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        itemView.layout(0 ,0,itemView.getMeasuredWidth(),getMeasuredHeight());

        int left  = itemView.getPaddingLeft(), top = itemView.getPaddingTop();
        Point point = itemView.getIconRightTop();
        int noheight = notifyView.getMeasuredHeight();
        int nleft = left+point.x-noheight/2;
        notifyView.layout(nleft,top,nleft+notifyView.getMeasuredWidth(),top+notifyView.getMeasuredHeight());
    }

    public View getNotifyView(){
        return this.notifyView;
    }

    /**
     * 为零不显示
     * 为负显示红点
     * @param num
     */
    public void setNotifyNum(int num){
        if(num>0){
            notifyView.setText(""+num);
        }else if(num < 0){
            notifyView.setText("");
        }else{
            notifyView.setText(null);
            notifyView.setVisibility(INVISIBLE);
        }
    }

    public void setIconAlpha(float alpha){
        itemView.setIconAlpha(alpha);
    }
}
