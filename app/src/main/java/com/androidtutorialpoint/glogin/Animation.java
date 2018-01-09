package com.androidtutorialpoint.glogin;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Animation extends AppCompatActivity {
    static AnimationDrawable sonicAnimation;
    MediaPlayer mp = null;
    @Override
    protected void onStart() {
        super.onStart();
        managerOfSound();
        Log.i("Animation Page", "Now running onStart");
    }

    @Override
    protected void onResume() {
        Log.i("Animation Page", "Now running onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("Animation Page", "Now running onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("Animation Page", "Now running onStop");
        super.onStop();
        mp.stop();
    }

    @Override
    protected void onRestart() {
        Log.i("Animation Page", "Now running onRestart");
        super.onRestart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        Log.i("Animation Page", "Now running onCreate");

        ImageView imgView = (ImageView) findViewById(R.id.imgSonic);
        imgView.setBackgroundResource(R.drawable.animation_list_one);
        sonicAnimation = (AnimationDrawable) imgView.getBackground();

        sonicAnimation.stop();
        sonicAnimation.selectDrawable(0);
        sonicAnimation.start();

        Intent intent = getIntent();
        String message = intent.getStringExtra(GPlusFragment.EXTRA_MESSAGE);
        TextView textView = (TextView) findViewById(R.id.current_date_view);
        textView.setText(getTodaysDate() + "\n" + message);
        fadeDate(textView);
        moveCloud();

        new Timer().schedule(new TimerTask(){
            public void run() {
                Animation.this.runOnUiThread(new Runnable() {
                    public void run() {
                        startActivity(new Intent(Animation.this, mainList.class));
                        finish();
                    }
                });
            }
        }, 7000);

    }
//
//    public void showAnimation (View view){
//        sonicAnimation.stop();
//        sonicAnimation.start();
//    }

//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            sonicAnimation.start();
//            return true;
//        }
//        return super.onTouchEvent(event);
//    }



//    public void getCurrentDate(View view) {
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
//        String strDate = mdformat.format(calendar.getTime());
//        display(strDate);
//    }
//
//    private void display(String num) {
//        TextView textView = (TextView) findViewById(R.id.current_date_view);
//        textView.setText(getTodaysDate());
//    }

    private String getTodaysDate() {

        final Calendar c = Calendar.getInstance();

        StringBuilder date = new StringBuilder();
        date.append(c.get(Calendar.DAY_OF_MONTH)).append("-")
                .append(c.get(Calendar.MONTH) + 1).append("-")
                .append(c.get(Calendar.YEAR)).append(" ");
        return date.toString();
    }

    public void fadeDate(final TextView textview) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
        valueAnimator.setDuration(5000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                textview.setAlpha(alpha);
            }
        });
        valueAnimator.start();
    }


    public void moveCloud() {
        final ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);
        final ImageView backgroundThree = (ImageView) findViewById(R.id.background_three);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);

        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();
    }
    protected void managerOfSound() {
        if (mp != null) {
            mp.reset();
            mp.release();
        }
        mp = MediaPlayer.create(Animation.this, R.raw.move);
        mp.start();
    }

//    public static void move(final TextView view){
//        ValueAnimator va = ValueAnimator.ofFloat(0f, 3f);
//        int mDuration = 3000; //in millis
//        va.setDuration(mDuration);
//        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                view.setTranslationX((float)animation.getAnimatedValue());
//            }
//        });
//        va.setRepeatCount(5);
//        va.start();
//    }

}
