/*
 * Copyright 2013 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androidtutorialpoint.glogin;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class Pedometer extends AppCompatActivity {
    private static final int MILES = 1;
    protected void onStart() {
        super.onStart();
        Log.i("Pedometer Activity", "Now running onStart");
    }

    @Override
    protected void onResume() {
        Log.i("Pedometer Activity", "Now running onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("Pedometer Activity", "Now running onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("Pedometer Activity", "Now running onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i("Pedometer Activity", "Now running onRestart");
        super.onRestart();
    }

    protected void onCreate(final Bundle b) {
        super.onCreate(b);
        Log.i("Pedometer Activity", "Now running onCreate");
        startService(new Intent(this, SensorListener.class));
        if (b == null) {
            // Create new fragment and transaction
            Fragment newFragment = new Fragment_Overview();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this
            // fragment,
            // and add the transaction to the back stack
            transaction.replace(android.R.id.content, newFragment);

            // Commit the transaction
            transaction.commit();

        }

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 23 && PermissionChecker
                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        } else {
            finish();
        }
    }

    public boolean optionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.action_settings:
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new Fragment_Settings()).addToBackStack(null)
                        .commit();
                break;
        }
        return true;
    }


}