package code.qiao.com.tabview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import code.qiao.com.tipsview.R;

/**
 * Created by bingosoft on 15/6/16.
 */
public class NotifyView extends View {
    private int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

    private Bitmap mBitmap;
    private Rect mIconRect;

    private String mText="9333";
    private Paint mTextPaint;
    private Rect mTextBound = new Rect();


    public NotifyView(Context context){
        super(context);
        init();
    }


    public NotifyView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }

    private void init(){
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tab_notify_bg);
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
    }

    private void invalidateView(){
        if (Looper.getMainLooper() == Looper.myLooper()){
            invalidate();
        } else{
            postInvalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mText==null) return;
        /**
         * 根据view的测量高度来控制text字体大小（text除margin外还与图标上下边距各距5dp）
         * text宽度为自适应，最大为测量宽度
         */
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight()-getPaddingTop()-getPaddingBottom();
        dp = height/3;

        mTextPaint.setTextSize(height-dp);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
        int iconWidth = Math.min(width,mTextBound.width()+dp);//保证不超过给定宽度
        iconWidth = Math.max(iconWidth,height); //保证高度小于宽度

        mIconRect = new Rect(getPaddingLeft(),getPaddingTop(),getPaddingLeft()+iconWidth,getPaddingTop()+height);

        setMeasuredDimension(iconWidth+getPaddingLeft()+getPaddingRight(), height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(getLeft(), getTop(), getLeft() + getWidth(), getTop() + getHeight(), new Paint());
        if(mText == null) return;
        canvas.drawBitmap(mBitmap, null, mIconRect, null);
        int textWidth = Math.min(mIconRect.width()-dp,mTextBound.width());//保证不超过给定宽度
        canvas.drawText(mText,mIconRect.centerX()-textWidth/2,mIconRect.centerY()+mTextBound.height()/2,mTextPaint); //从左下角绘制
    }

    public void setText(String text){
        mText = text;
        invalidateView();
    }
}
