package com.androidtutorialpoint.glogin;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);


        if (fragment == null) {
            fragment = new GPlusFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();


        }
        Log.i("LoginActivity tag","now running onCreate");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("LoginActivity tag", "now running onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("LoginActivity tag", "now running onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("LoginActivity tag", "now running onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("LoginActivity tag", "now running onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("LoginActivity tag", "now running onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("LoginActivity tag", "now running onRestart");
    }

}
