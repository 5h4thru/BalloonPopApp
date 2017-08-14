package com.example.shathru.ballonpopapp;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.shathru.ballonpopapp.Util.PixelHelper;

@SuppressLint("AppCompatCustomView")
public class Balloon extends ImageView implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    private ValueAnimator mAnimator;
    private BalloonListener mListener;
    private boolean mBalloonPopped;

    public Balloon(Context context) {
        super(context);
    }

    public Balloon(Context context, int color, int rawHeight) {
        super(context);

        mListener = (BalloonListener) context;

        this.setImageResource(R.drawable.balloon);

        //change color of the ballon
        this.setColorFilter(color);
        int rawWidth = rawHeight / 2;
        int dpHeight = PixelHelper.pixelsToDp(rawHeight, context);
        int dpWidth = PixelHelper.pixelsToDp(rawWidth, context);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpWidth, dpHeight);
        setLayoutParams(params);
    }

    public void releaseBalloon(int screenHeight, int duration) {
        mAnimator = new ValueAnimator();
        mAnimator.setDuration(duration);
        mAnimator.setFloatValues(screenHeight, 0f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setTarget(this);
        mAnimator.addListener(this);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    public void setPopped(boolean popped) {
        mBalloonPopped = popped;
        if (popped) {
            mAnimator.cancel();
        }
    }


    public interface BalloonListener {
        void popBalloon(Balloon balloon, boolean userTouch);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        setY((Float) animation.getAnimatedValue());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mBalloonPopped && event.getAction() == MotionEvent.ACTION_DOWN) {
            mListener.popBalloon(this, true);
            mBalloonPopped = true;
            mAnimator.cancel();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (!mBalloonPopped) {
            mListener.popBalloon(this, false);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
