package code.qiao.com.tabview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by bingosoft on 15/6/16.
 */
public class DotView extends View {
    private int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

    private Rect mIconRect;

    private String mText;
    private Paint mTextPaint;
    private Rect mTextBound = new Rect();


    public DotView(Context context){
        super(context);
        init();
    }


    public DotView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }

    private void init(){
        mTextPaint = new Paint();
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
        int iconWidth = Math.min(width, mTextBound.width() + dp);//保证不超过给定宽度
        iconWidth = Math.max(iconWidth, height); //保证高度小于宽度

        mIconRect = new Rect(getPaddingLeft(),getPaddingTop(),getPaddingLeft()+iconWidth,getPaddingTop()+height);

        setMeasuredDimension(iconWidth+getPaddingLeft()+getPaddingRight(), height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mText == null) return;

        mTextPaint.setColor(Color.RED);
        canvas.drawOval(new RectF(mIconRect), mTextPaint);

        mTextPaint.setColor(Color.WHITE);
        int textWidth = Math.min(mIconRect.width() - dp, mTextBound.width());//保证不超过给定宽度
        canvas.drawText(mText,mIconRect.centerX()-textWidth/2,mIconRect.centerY()+mTextBound.height()/2,mTextPaint); //从左下角绘制
    }

    public void setText(String text){
        mText = text;
        invalidateView();
    }
}
