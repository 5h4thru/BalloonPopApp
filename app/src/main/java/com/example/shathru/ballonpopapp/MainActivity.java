package com.example.shathru.ballonpopapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Balloon.BalloonListener {

    private ViewGroup mContentView;
    private int[] mBalloonColors = new int[3];
    private int mNextColor;
    private int mScreenHeight;
    private int mScreenWidth;
    public static final int MIN_ANIMATION_DELAY = 500;
    public static final int MAX_ANIMATION_DELAY = 1500;
    public static final int MIN_ANIMATION_DURATION = 1000;
    public static final int MAX_ANIMATION_DURATION = 8000;
    private int mLevel;
    private int mScore;

    private Button mGoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.modern_background);

        mContentView = (ViewGroup) findViewById(R.id.activity_main);
        mContentView.setOnClickListener((view) -> setToFullScreen());
        mGoButton = (Button) findViewById(R.id.go_button);

        setBalloonColors();
        setToFullScreen();
        setEventListener();
        setGoButtonClickListener();

//        setContentViewTouchListener();
    }

    private void setGoButtonClickListener() {
        mGoButton.setOnClickListener((v) -> {
            startLevel();
        });
    }

    private void setEventListener() {
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            initializeViewTreeListener(viewTreeObserver);
        }
    }

    private void initializeViewTreeListener(ViewTreeObserver viewTreeObserver) {
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mScreenHeight = mContentView.getHeight();
                mScreenWidth = mContentView.getWidth();
            }
        });
    }

    private void setContentViewTouchListener() {
        mContentView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                initializeBalloonObject(motionEvent);
            }
            return false;
        });
    }

    private void initializeBalloonObject(MotionEvent motionEvent) {
        Balloon b = new Balloon(MainActivity.this, mBalloonColors[mNextColor], 100);
        b.setX(motionEvent.getX());
        b.setY(mScreenHeight); // start at the bottom of the screen
        mContentView.addView(b);
        b.releaseBalloon(mScreenHeight, 3000);
        if (mNextColor + 1 == mBalloonColors.length) {
            mNextColor = 0;
        } else {
            mNextColor++;
        }
    }

    private void setBalloonColors() {
        mBalloonColors[0] = Color.argb(255, 255, 0, 0); // red
        mBalloonColors[1] = Color.argb(255, 0, 255, 0); // green
        mBalloonColors[2] = Color.argb(255, 0, 0, 255); // blue
    }

    private void setToFullScreen() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.activity_main);
        rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void startLevel() {
        mLevel++;
        // AsyncTask
        BalloonLauncher launcher = new BalloonLauncher();
        launcher.execute(mLevel);
    }

    @Override
    public void onResume() {
        super.onResume();
        setToFullScreen();
    }

    @Override
    public void popBalloon(Balloon balloon, boolean userTouch) {
        mContentView.removeView(balloon);
        if (userTouch) {
            mScore++;
        }
        updateDisplay();
    }

    private void updateDisplay() {
        // TODO: 7/13/2017 Update the display with the score 
    }


    /**
     * AsyncTask to start the difficulty of the level
     */
    private class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... params) {

            if (params.length != 1) {
                throw new AssertionError(
                        "Expected 1 param for current level");
            }

            int level = params[0];
            int maxDelay = Math.max(MIN_ANIMATION_DELAY,
                    (MAX_ANIMATION_DELAY - ((level - 1) * 500)));
            int minDelay = maxDelay / 2;

            int balloonsLaunched = 0;
            while (balloonsLaunched < 3) {

                // Get a random horizontal position for the next balloon
                Random random = new Random(new Date().getTime());
                int xPosition = random.nextInt(mScreenWidth - 200);
                publishProgress(xPosition);
                balloonsLaunched++;

                // Wait a random number of milliseconds before looping
                int delay = random.nextInt(minDelay) + minDelay;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int xPosition = values[0];
            launchBalloon(xPosition);
        }

    }

    private void launchBalloon(int x) {

        Balloon balloon = new Balloon(this, mBalloonColors[mNextColor], 150);

        if (mNextColor + 1 == mBalloonColors.length) {
            mNextColor = 0;
        } else {
            mNextColor++;
        }

        // Set balloon vertical position and dimensions, add to container
        balloon.setX(x);
        balloon.setY(mScreenHeight + balloon.getHeight());
        mContentView.addView(balloon);

        // Let 'er fly
        int duration = Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 1000));
        balloon.releaseBalloon(mScreenHeight, duration);

    }

}
