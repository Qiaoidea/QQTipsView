package code.qiao.com.tipsview;

import android.app.Activity;
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

import java.lang.ref.WeakReference;

public class TipsView extends FrameLayout {

    public static final float DEFAULT_RADIUS = 20;

    private Paint paint;
    private Path path;

    float x = 0;
    float y = 0;

    /**
     * 锚点位置
     */
    float anchorX = 0;
    float anchorY = 0;

    float startX = 500;
    float startY = 100;

    /**
     * 在屏幕中的位置
     */
    float locationX = 0;
    float locationY = 0;

    float radius = DEFAULT_RADIUS;

    boolean isTrigger; //是否断开
    boolean  isTouch;  //是否正在触摸

    ImageView exploredImageView;
    View tipImageView;

    public TipsView(Context context) {
        super(context);
        initialize();
    }

    public TipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
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

    /**
     * 计算贝塞尔曲线
     */
    private void calculate() {
        float distance = (float) Math.sqrt(Math.pow(y - startY, 2) + Math.pow(x - startX, 2));
        radius = -distance / 15 + DEFAULT_RADIUS;

        if (radius < 5) {
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

    public void attach(final View attachView) {
        attach(attachView, null);
    }

    public void attach(final View attachView, DragListener dragListener) {
        attach(attachView, new ViewCreator<View>() {
            @Override
            public View invoke() {
                Bitmap bm = view2Bitmap(attachView);
                ImageView iv = new ImageView(getContext());
                iv.setImageBitmap(bm);
                return iv;
            }
        }, dragListener);
    }

    public void attach(final View attachView, final ViewCreator<View> copyViewCreator, final DragListener dragListener) {
        bringToFront();

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
                tipImageView.measure(0, 0);

                tipImageView.setX(startX - tipImageView.getMeasuredWidth() / 2);
                tipImageView.setY(startY - tipImageView.getMeasuredHeight() / 2);

                attachView.setVisibility(INVISIBLE);
                requestDisallowInterceptTouchEvent(attachView,true);

                if (dragListener != null) {
                    dragListener.onStart();
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
                    locationX = location[0];
                    locationY = location[1];

                    invalidate();
                    return true;
                }

                if (!isTouch)
                    return false;

                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    isTouch = false;
                    destory();

                    requestDisallowInterceptTouchEvent(attachView,false);

                    if (isTrigger) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isTrigger = false;
                                if (dragListener != null) {
                                    dragListener.onComplete();
                                }
                            }
                        }, 1000);

                        /**
                         * 在释放位置显示消除动画
                         */
                        exploredImageView.setX(x - exploredImageView.getWidth() / 2);
                        exploredImageView.setY(y - exploredImageView.getHeight() / 2);
                        exploredImageView.setVisibility(View.VISIBLE);
                        ((AnimationDrawable) exploredImageView.getDrawable()).stop();
                        ((AnimationDrawable) exploredImageView.getDrawable()).start();

                    } else {
                        attachView.setVisibility(VISIBLE);
                        if (dragListener != null) {
                            dragListener.onCancel();
                        }
                    }
                }

                anchorX = (event.getRawX() - locationX + startX) / 2;
                anchorY = (event.getRawY() - locationY + startY) / 2;
                x = event.getRawX() - locationX;
                y = event.getRawY() - locationY;

                tipImageView.setX(x - tipImageView.getWidth() / 2);
                tipImageView.setY(y - tipImageView.getHeight() / 2);

                invalidate();
                return true;
            }
        });
    }

    /**
     * 用于拦截其 parent 的touch事件
     * @param view
     */
    protected void requestDisallowInterceptTouchEvent(View view,boolean isDisallow){
        if(view.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup)view.getParent();
            parent.requestDisallowInterceptTouchEvent(isDisallow);
        }
    }

    /**
     * View 转 bitmap
     * @param v
     * @return
     */
    public static Bitmap view2Bitmap(final View v) {
        Bitmap bm = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        v.draw(canvas);
        return bm;
    }

    public interface ViewCreator<Tresult> {
        Tresult invoke();
    }

    /**
     * 拖动监听接口
     */
    public interface DragListener {
        void onStart();

        void onComplete();

        void onCancel();
    }

    /**
     * 用于页面复用管理
     */
    private static WeakReference<TipsView> instance;
    public static TipsView create(final Activity activity) {
        if(instance!=null && instance.get()!=null && instance.get().getTag() == activity){
            return instance.get();
        }
        instance = new WeakReference<>(new TipsView(activity));
        instance.get().setTag(activity);
        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        activity.addContentView(instance.get(),vlp) ;
        return instance.get();
    }
}