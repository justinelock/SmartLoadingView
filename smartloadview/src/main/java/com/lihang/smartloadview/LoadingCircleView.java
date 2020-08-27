package com.lihang.smartloadview;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 登录加载按钮
 */
public class LoadingCircleView extends AppCompatTextView {

    //view的宽度
    private int width;

    //View的高度
    private int height;

    //圆角半径
    private int circleAngle;

    //从用户获得的圆角
    private int obtainCircleAngle;

    private int textScrollMode = 1;//文字滚动模式，默认为1：来回滚动

    //矩形2边需要缩短的距离
    private int default_all_distance;

    //当前矩形在x轴left的位置
    private int current_left;

    //动画执行时间
    private int duration = 500;

    //圆角矩形画笔
    private Paint paint;


    //对勾（√）画笔
    private Paint okPaint;

    //文字绘制所在矩形
    private Rect textRect = new Rect();

    //根据view的大小设置成矩形
    private RectF rectf = new RectF();

    /**
     * 动画集
     */
    //这是开始的启动
    private AnimatorSet animatorSet = new AnimatorSet();
    //这是网络错误的
    private AnimatorSet animatorNetfail = new AnimatorSet();

    //矩形到正方形过度的动画
    private ValueAnimator animator_rect_to_square;

    //是否开始绘制对勾
    private boolean startDrawOk = false;

    //对路径处理实现绘制动画效果
    private PathEffect effect;

    //路径--用来获取对勾的路径
    private Path path = new Path();

    //取路径的长度
    private PathMeasure pathMeasure;


    /**
     * 加载loading动画相关
     */
    //是否开始绘画，加载转圈动画
    private boolean isDrawLoading = false;
    //是否处于加载状态，注意，这里和上面是2个概念，只要点击按钮，没有走错误和走正确的都视为在加载状态下
    private boolean isLoading = false;
    private int startAngle = 0;
    private int progAngle = 30;
    private boolean isAdd = true;


    /**
     * 以下是自定义属性
     */

    //不可点击的背景颜色
    private int cannotclick_color;

    //加载失败的背景颜色
    private int error_color;

    // -1 白色
    private int text_after_color;

    //正常情况下view的背景颜色
    private int normal_color;

    //是否可以点击状态
    private boolean smartClickable;

    public LoadingCircleView setErrorColor(int error_color) {
        this.error_color = error_color;
        return this;
    }

    public LoadingCircleView setNormalColor(int normal_color) {
        this.normal_color = normal_color;
        return this;
    }

    public LoadingCircleView setNormalString(String normalString) {
        this.normalString = normalString;
        return this;
    }

    public LoadingCircleView setErrorString(String errorString) {
        this.errorString = errorString;
        return this;
    }

    //按钮文字
    private String normalString = "";//getResources().getString(R.string.normalString);
    private String errorString = "";//getResources().getString(R.string.errorString);
    private String currentString;//当前要绘画的TextStr

    //获取文字绘画区域
    private Rect mRect;
    //当前字体颜色值
    private int textColor;

    @Override
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    //当前字体透明度
    private int textAlpha;
    //文字滚动速度
    private int speed;

    //这是全屏动画
    //private CircleBigView circleBigView;


    public LoadingCircleView(Context context) {
        this(context, null);
    }

    public LoadingCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRect = new Rect();
        init(attrs);
        initPaint();
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SmartLoadingView);
        // 设置title
        if (TextUtils.isEmpty(getText())) {
            currentString = normalString;
        } else {
            normalString = (String) getText();
            currentString = normalString;
        }

        String currentErrorString = typedArray.getString(R.styleable.SmartLoadingView_errorMsg);
        if (!TextUtils.isEmpty(currentErrorString)) {
            errorString = currentErrorString;
        }
        cannotclick_color = typedArray.getColor(R.styleable.SmartLoadingView_background_cannotClick, getResources().getColor(R.color.blackbb));
        error_color = typedArray.getColor(R.styleable.SmartLoadingView_background_error, getResources().getColor(R.color.red));
        text_after_color = typedArray.getColor(R.styleable.SmartLoadingView_text_after_color, getResources().getColor(R.color.dark_gray));
        smartClickable = typedArray.getBoolean(R.styleable.SmartLoadingView_smart_clickable, true);
        normal_color = typedArray.getColor(R.styleable.SmartLoadingView_background_normal, getResources().getColor(R.color.white));
        obtainCircleAngle = (int) typedArray.getDimension(R.styleable.SmartLoadingView_cornerRaius, getResources().getDimension(R.dimen.default_corner));
        textScrollMode = typedArray.getInt(R.styleable.SmartLoadingView_textScrollMode, 1);
        speed = typedArray.getInt(R.styleable.SmartLoadingView_speed, 400);
        int paddingTop = getPaddingTop() == 0 ? dip2px(7) : getPaddingTop();
        int paddingBottom = getPaddingBottom() == 0 ? dip2px(7) : getPaddingBottom();
        int paddingLeft = getPaddingLeft() == 0 ? dip2px(15) : getPaddingLeft();
        int paddingRight = getPaddingRight() == 0 ? dip2px(15) : getPaddingRight();
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        setBackgroundColor(0);
        setMaxLines(1);
        setGravity(Gravity.CENTER);
    }


    private int dip2px(float dipValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 初始化所有动画
     */
    private void initAnimation() {
        set_rect_to_circle_animation();
        //animatorSet.play(animator_rect_to_square).with(animator_rect_to_angle);
        animatorSet.play(animator_rect_to_square);
        //animatorNetfail.play(animator_squareToRect).with(animator_angle_to_rect);
    }


    /**
     * 设置圆角矩形过度到圆的动画
     * &圆到圆角矩形
     * <p>
     * 矩形到圆角矩形的动画
     * &圆角矩形到矩形的动画
     */
    private void set_rect_to_circle_animation() {
        animator_rect_to_square = ValueAnimator.ofInt(0, default_all_distance);
        animator_rect_to_square.setDuration(duration);
        animator_rect_to_square.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                current_left = (int) animation.getAnimatedValue();
                boolean isZero = false;
                if (default_all_distance == 0) {
                    default_all_distance = height;
                    isZero = true;
                }
                int nowAlpha = textAlpha / 2 - (current_left * textAlpha / default_all_distance) < 0 ? 0 : textAlpha / 2 - (current_left * textAlpha / default_all_distance);
                Log.e("textColor1", "" + textColor);
                textPaint.setColor(addAlpha(textColor, nowAlpha));
                if (current_left == default_all_distance || isZero) {
                    isDrawLoading = true;
                }
                invalidate();

            }
        });

    }

    private void initPaint() {
        //初始画笔 矩形画笔
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        if (smartClickable) {
            paint.setColor(normal_color);
        } else {
            paint.setColor(cannotclick_color);
        }


        //打勾画笔
        okPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        okPaint.setStrokeWidth(5);
        okPaint.setStyle(Paint.Style.STROKE);
        okPaint.setAntiAlias(true);
        okPaint.setStrokeCap(Paint.Cap.ROUND);


        ColorStateList textColors = getTextColors();
        final int[] drawableState = getDrawableState();
        Log.e("drawableState", "" + drawableState.length+","+ drawableState[0]+","+ drawableState[1]);
        okPaint.setColor(text_after_color);
        //获取textView默认颜色值
        textColor = textColors.getColorForState(drawableState, 0);
        //okPaint.setColor(textColor);
        textAlpha = Color.alpha(textColor);


        //文字画笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(getTextSize());
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
    }


    /*
     * 不可点击的这块
     * */
    public void setSmartClickable(boolean clickable) {
        super.setClickable(clickable);
        smartClickable = clickable;
        if (clickable) {
            if (paint != null) {
                paint.setColor(normal_color);
            }
            postInvalidate();
        } else {
            if (paint != null) {
                paint.setColor(cannotclick_color);
            }
            postInvalidate();
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (width == 0) {
            width = w;
            height = h;
            if (obtainCircleAngle > (height / 2)) {
                obtainCircleAngle = height / 2;
            }
            circleAngle = obtainCircleAngle;
            default_all_distance = (w - h) / 2;
            initOk();
            initAnimation();
            //如果不是精准模式，我们代码里设置第一次的长宽，成为精准模式
            //这样避免，更改文字内容时，总是会改变控件的长宽
            setWidth(width);
            setHeight(height);
            setClickable(smartClickable);
        }
    }

    //给TextView字体设置透明度。
    private int addAlpha(int color, int alpha) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    //文字画笔
    private Paint textPaint;
    //文字超过一行时，进行的文字滚动动画
    private ValueAnimator animator_text_scroll;//这只是模式之一
    private ValueAnimator animator_marque;
    private int drawTextStart;
    private int drawMarqueTextStart;

    private void drawText(final Canvas canvas) {
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        rectf.left = current_left + getPaddingLeft();
        rectf.top = 0;
        rectf.right = width - current_left - getPaddingRight();
        rectf.bottom = height;
        //画圆角矩形
        canvas.drawRoundRect(rectf, circleAngle, circleAngle, paint);

        //设置混合模式
        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        textRect.left = getPaddingLeft();
        textRect.top = 0;
        textRect.right = width - getPaddingRight();
        textRect.bottom = height;
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        final int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        //这是测量文字的长度。
        int myTotal = (int) (textPaint.measureText(currentString) + getPaddingRight() + getPaddingLeft());
        if (myTotal > getWidth()) {
            if (textScrollMode == 1) {
                textPaint.setTextAlign(Paint.Align.LEFT);
                if (animator_text_scroll == null && !isLoading) {
                    //此时文字长度已经超过一行，进行文字滚动
                    animator_text_scroll = ValueAnimator.ofInt(textRect.left, (int) (textRect.left - textPaint.measureText(currentString) + (getWidth() - getPaddingLeft() - getPaddingRight())));
                    animator_text_scroll.setDuration(currentString.length() * speed);
                    animator_text_scroll.setRepeatMode(ValueAnimator.REVERSE);
                    animator_text_scroll.setRepeatCount(-1);
                    animator_text_scroll.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            drawTextStart = (int) animation.getAnimatedValue();
                            postInvalidate();
                        }
                    });
                    animator_text_scroll.start();
                }
                canvas.drawText(currentString, drawTextStart, baseline, textPaint);
            } else {
                textPaint.setTextAlign(Paint.Align.LEFT);
                if (animator_text_scroll == null && !isLoading) {
                    //此时文字长度已经超过一行，进行文字滚动
                    animator_text_scroll = ValueAnimator.ofInt(textRect.left, (int) (textRect.left - textPaint.measureText(currentString)));
                    animator_text_scroll.setDuration(currentString.length() * speed);
                    animator_text_scroll.setInterpolator(new LinearInterpolator());
                    animator_text_scroll.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {

                            drawTextStart = (int) animation.getAnimatedValue();
                            postInvalidate();
                            if (drawTextStart == textRect.left) {
                                if (animator_marque != null) {
                                    animator_marque.cancel();
                                    animator_marque = null;
                                }
                            }
                            if (animator_marque == null && !isLoading && drawTextStart <= (int) (textRect.left - textPaint.measureText(currentString) + (getWidth() - getPaddingLeft() - getPaddingRight()) - (getWidth() - getPaddingLeft() - getPaddingRight()) / 3)) {
                                int duration = (int) (((currentString.length() * speed) * (textRect.right - textRect.left)) / textPaint.measureText(currentString));
                                animator_marque = ValueAnimator.ofInt(textRect.right, textRect.left);
                                animator_marque.setDuration(duration);
                                animator_marque.setInterpolator(new LinearInterpolator());
                                animator_marque.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        drawMarqueTextStart = (int) animation.getAnimatedValue();
                                        if (drawMarqueTextStart == textRect.left) {
                                            mHandler.sendEmptyMessageDelayed(14, 1500);
                                        }
                                        postInvalidate();
                                    }
                                });
                                animator_marque.start();
                            }
                        }
                    });
                    animator_text_scroll.start();
                }
                if (animator_marque != null) {
                    canvas.drawText(currentString, drawMarqueTextStart, baseline, textPaint);
                }
                canvas.drawText(currentString, drawTextStart, baseline, textPaint);
            }

        } else {
            cancelScroll();
            textPaint.setTextAlign(Paint.Align.CENTER);
            drawTextStart = textRect.left;
            canvas.drawText(currentString, textRect.centerX(), baseline, textPaint);
        }

        // 还原混合模式
        textPaint.setXfermode(null);
        // 还原画布
        canvas.restoreToCount(sc);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        draw_oval_to_circle(canvas);
        drawText(canvas);

        //绘制加载进度
        if (isDrawLoading) {
            canvas.drawArc(new RectF(width / 2 - height / 2 + height / 4, height / 4, width / 2 + height / 2 - height / 4, height / 2 + height / 2 - height / 4), startAngle, progAngle, false, okPaint);
            startAngle += 6;
            if (progAngle >= 270) {
                progAngle -= 2;
                isAdd = false;
            } else if (progAngle <= 45) {
                progAngle += 6;
                isAdd = true;
            } else {
                if (isAdd) {
                    progAngle += 6;
                } else {
                    progAngle -= 2;
                }
            }
            postInvalidate();
        }

        //绘制打勾
        if (startDrawOk) {
            canvas.drawPath(path, okPaint);
        }

    }


    private void draw_oval_to_circle(Canvas canvas) {
        rectf.left = current_left;
        rectf.top = 0;
        rectf.right = width - current_left;
        rectf.bottom = height;

        //画圆角矩形
        canvas.drawRoundRect(rectf, circleAngle, circleAngle, paint);
    }


    /**
     * 绘制对勾
     */
    private void initOk() {
        //对勾的路径
        path.moveTo(default_all_distance + height / 8 * 3, height / 2);
        path.lineTo(default_all_distance + height / 2, height / 5 * 3);
        path.lineTo(default_all_distance + height / 3 * 2, height / 5 * 2);
        pathMeasure = new PathMeasure(path, true);
    }


    //smartLoadingView 开启动画
    public void start() {
        //没有在loading的情况下才能点击（没有在请求网络的情况下）
        if (!isLoading) {
            cancelScroll();
            startDrawOk = false;
            currentString = normalString;
            this.setClickable(false);
            paint.setColor(normal_color);
            isLoading = true;
            animatorSet.start();
        }
    }

    public boolean isLoading() {
        return isLoading == true;
    }

    public void loading(int bgRid, String message, int tvRid) {
        normalString = message;
//        normal_color = bgRid;
//        textPaint.setColor(tvRid);
        loading();
    }

    /**
     * 按钮始终可以点
     * btnListen.setNormalColor(getResources().getColor(R.color.red_dark)).loading().resetText("1");
     *
     * btnListen.setTextColor(getResources().getColor(R.color.colorPrimary));
     * btnListen.setNormalColor(getResources().getColor(R.color.white)).unloading().resetText("2");
     *
     */
    public LoadingCircleView loading() {
        //没有在loading的情况下才能点击（没有在请求网络的情况下）
        Log.e("textColor2", "" + textColor);
        if (!isLoading) {
            cancelScroll();
            startDrawOk = false;
            currentString = normalString;
            //this.setClickable(false);
            paint.setColor(normal_color);
            isLoading = true;
            animatorSet.start();
        }
        return this;
    }

    public LoadingCircleView unloading() {
        stopAndReset();
        return this;
    }

    public LoadingCircleView stopAndReset() {
        setClickable(true);
        currentString = normalString;
        textPaint.setColor(textColor);
        circleAngle = obtainCircleAngle;
        paint.setColor(normal_color);
        current_left = 0;
        isDrawLoading = false;
        startDrawOk = false;
        isLoading = false;
        invalidate();

        // 这里避免控件翻转效果
        default_all_distance = 0;
        initAnimation();
        return this;
    }

    // 2020.07.19 add 改变当前文本,适合做角色变化时，显示不同的文字
    public LoadingCircleView resetText(String message) {
        currentString = message;
        return this;
    }

    // 默认按钮
    private AnimatorSet animatorDefault = new AnimatorSet();

    /**
     * 按下和 默认颜色
     * btnLoadingLogin.backgroundChange(R.color.White);
     * <p>
     * btnLoadingLogin.backgroundChange(R.color.Black);
     */
    public void backgroundChange(Integer rid) {
        if (rid != null) {
            paint.setColor(getResources().getColor(rid));
        }
        ValueAnimator animator = ValueAnimator.ofInt(0, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                postInvalidate();
            }
        });
        animatorDefault.play(animator);
        animatorDefault.start();
    }

    public void fail() {
        if (isLoading) {
            currentString = errorString;
            paint.setColor(error_color);
            animatorNetfail.start();
        }
    }

    public void fail(String message) {
        errorString = message;
        fail();
    }

    private void cancelScroll() {
        if (animator_text_scroll != null) {
            animator_text_scroll.cancel();
            animator_text_scroll = null;
        }

        if (animator_marque != null) {
            animator_marque.cancel();
            animator_marque = null;
        }
    }


    //加载失败运行(默认加载失败文案)
    public void netFail() {
        if (isLoading) {
            currentString = errorString;
            paint.setColor(error_color);
            animatorNetfail.start();
        }
    }


    //加载失败运行(文案自定义)
    public void netFail(String message) {
        if (isLoading) {
            errorString = message;
            currentString = errorString;
            paint.setColor(error_color);
            animatorNetfail.start();
        }
    }

    public void backToStart() {
        if (isLoading) {
            currentString = normalString;
            paint.setColor(normal_color);
            animatorNetfail.start();
        }
    }


    //立即重置状态
    public void reset() {
        resetAll();
    }

    //用于，扩散动画，有可能不跳转。停留在当前页，1秒后重置状态
    protected void resetLater() {
        mHandler.sendEmptyMessageDelayed(13, 1000);
    }

    public void resetAll() {
        setClickable(true);
        currentString = normalString;
        textPaint.setColor(textColor);
        circleAngle = obtainCircleAngle;
        paint.setColor(normal_color);
        current_left = 0;
        isDrawLoading = false;
        startDrawOk = false;
        isLoading = false;
        invalidate();

        animatorSet.cancel();
        animatorNetfail.cancel();

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 13:
                    resetAll();
                    break;

                case 14:
                    if (animator_text_scroll != null) {
                        animator_text_scroll.cancel();
                        animator_text_scroll = null;
                        postInvalidate();
                    }
                    break;
            }
        }
    };


    //绘制全屏动画
    public interface AnimationFullScreenListener {
        void animationFullScreenFinish();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelScroll();
    }
}
