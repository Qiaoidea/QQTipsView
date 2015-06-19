package code.qiao.com.tipsview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TipsView extends FrameLayout {

    public static final float DEFAULT_RADIUS = 20;

    private Paint paint;
    private Path path;

    float x = 0;
    float y = 0;

    float anchorX = 0;
    float anchorY = 0;

    float startX = 500;
    float startY = 100;

    float thisX = 0;
    float thisY = 0;

    float radius = DEFAULT_RADIUS;

    boolean isTrigger, isTouch;

    ImageView exploredImageView;
    View tipImageView;

    public TipsView(Context context) {
        super(context);
        init();
    }

    public TipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.TRANSPARENT);
        path = new Path();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(0xffed5050);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        exploredImageView = new ImageView(getContext());
        exploredImageView.setLayoutParams(params);
        exploredImageView.setImageResource(R.drawable.tips_bubble);
        exploredImageView.setVisibility(View.INVISIBLE);
        addView(exploredImageView);
    }

    public void setColor(int color){
        paint.setColor(color);
    }

    private void calculate() {
        float distance = (float) Math.sqrt(Math.pow(y - startY, 2) + Math.pow(x - startX, 2));
        radius = -distance / 15 + DEFAULT_RADIUS;

        if (radius < 7) {
            isTrigger = true;
        } else {
            isTrigger = false;
        }

        float offsetX = (float) (radius * Math.sin(Math.atan((y - startY) / (x - startX))));
        float offsetY = (float) (radius * Math.cos(Math.atan((y - startY) / (x - startX))));

        float x1 = startX - offsetX;
        float y1 = startY + offsetY;

        float x2 = x - offsetX;
        float y2 = y + offsetY;

        float x3 = x + offsetX;
        float y3 = y - offsetY;

        float x4 = startX + offsetX;
        float y4 = startY - offsetY;

        path.reset();
        path.moveTo(x1, y1);
        path.quadTo(anchorX, anchorY, x2, y2);
        path.lineTo(x3, y3);
        path.quadTo(anchorX, anchorY, x4, y4);
        path.lineTo(x1, y1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calculate();
        if (isTrigger || !isTouch || tipImageView == null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
        } else {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
            canvas.drawPath(path, paint);
            canvas.drawCircle(startX, startY, radius, paint);
            canvas.drawCircle(x, y, radius, paint);
        }
        super.onDraw(canvas);
    }

    public void attach(final View attachView, Listener listener) {
        attach(attachView, new Func<View>() {
            @Override
            public View invoke() {
                Bitmap bm = view2Bitmap(attachView);
                ImageView iv = new ImageView(getContext());
                iv.setImageBitmap(bm);
                return iv;
            }
        }, listener);
    }

    public void attach(final View attachView, final Func<View> copyViewCreator, final Listener listener) {
        attachView.setOnTouchListener(new OnTouchListener() {
            protected void init() {
                int[] attachLocation = new int[2];
                attachView.getLocationOnScreen(attachLocation);
                int[] thisLocation = new int[2];
                TipsView.this.getLocationOnScreen(thisLocation);

                startX = attachLocation[0] - thisLocation[0] + attachView.getWidth() / 2;
                startY = attachLocation[1] - thisLocation[1] + attachView.getHeight() / 2;

                x = startX;
                y = startY;

                tipImageView = copyViewCreator.invoke();

                tipImageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                TipsView.this.addView(tipImageView);
                tipImageView.measure(0,0);

                tipImageView.setX(startX - tipImageView.getMeasuredWidth() / 2);
                tipImageView.setY(startY - tipImageView.getMeasuredHeight() / 2);

                if (listener != null) {
                    listener.onStart();
                }
            }

            protected void destory() {
                TipsView.this.removeView(tipImageView);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    init();
                    isTouch = true;
                    int[] location = new int[2];
                    TipsView.this.getLocationOnScreen(location);
                    thisX = location[0];
                    thisY = location[1];

                    invalidate();
                    return true;
                }
                if (!isTouch)
                    return false;
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    isTouch = false;
                    destory();

                    if (isTrigger) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isTrigger = false;
                                if (listener != null) {
                                    listener.onComplete();
                                }
                            }
                        }, 1000);
                        exploredImageView.setX(x - exploredImageView.getWidth() / 2);
                        exploredImageView.setY(y - exploredImageView.getHeight() / 2);
                        exploredImageView.setVisibility(View.VISIBLE);
                        exploredImageView.setImageResource(R.drawable.tips_bubble);
                        ((AnimationDrawable) exploredImageView.getDrawable()).stop();
                        ((AnimationDrawable) exploredImageView.getDrawable()).start();
                    } else {
                        if (listener != null) {
                            listener.onCancel();
                        }
                    }
                }

                anchorX = (event.getRawX() - thisX + startX) / 2;
                anchorY = (event.getRawY() - thisY + startY) / 2;
                x = event.getRawX() - thisX;
                y = event.getRawY() - thisY;

                tipImageView.setX(x - tipImageView.getWidth() / 2);
                tipImageView.setY(y - tipImageView.getHeight() / 2);

                invalidate();
                return true;
            }
        });
    }

    public static Bitmap view2Bitmap(View v) {
        Bitmap bm = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        v.draw(canvas);
        return bm;
    }



    public interface Func<Tresult> {
        Tresult invoke();
    }

    public static interface Listener {
        void onStart();

        void onComplete();

        void onCancel();
    }

    public static TipsView init(Context context,FrameLayout rootView) {
        TipsView tipsView = new TipsView(context);
        rootView.addView(tipsView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        return tipsView;
    }

    public static TipsView init(Context context,RelativeLayout rootView) {
        TipsView tipsView = new TipsView(context);
        rootView.addView(tipsView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        return tipsView;
    }
}