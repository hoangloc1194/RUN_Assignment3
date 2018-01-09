package com.androidtutorialpoint.glogin;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
//http://techaxioms.com/add-animated-gif-android-application/

public class EndingPage extends AppCompatActivity {
    static AnimationDrawable winnieAnimation;
    MediaPlayer mp = null;
    protected void onStart() {
        super.onStart();
        managerOfSound();
        Log.i("Ending Page", "Now running onStart");
    }

    @Override
    protected void onResume() {
        Log.i("Ending Page", "Now running onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("Ending Page", "Now running onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("Ending Page", "Now running onStop");
        super.onStop();
        mp.stop();
    }

    @Override
    protected void onRestart() {
        Log.i("Ending Page", "Now running onRestart");
        super.onRestart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ending_page);

        TextView myText = (TextView) findViewById(R.id.well_done);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        myText.startAnimation(anim);
        new Timer().schedule(new TimerTask(){
            public void run() {
                com.androidtutorialpoint.glogin.EndingPage.this.runOnUiThread(new Runnable() {
                    public void run() {
                        startActivity(new Intent(com.androidtutorialpoint.glogin.EndingPage.this, mainList.class));
                        finish();
                    }
                });
            }
        }, 7000);

    }
    protected void managerOfSound() {
        if (mp != null) {
            mp.reset();
            mp.release();
        }
        mp = MediaPlayer.create(com.androidtutorialpoint.glogin.EndingPage.this, R.raw.move);
        mp.start();
    }

}
