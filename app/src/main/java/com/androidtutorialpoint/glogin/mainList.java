package com.androidtutorialpoint.glogin;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class mainList extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("mainList tag", "now running onCreate");
        setContentView(R.layout.list_feature);
        List<Feature> image_details = getListData();
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new CustomListAdapter(this, image_details));

        // When user clicks on item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Feature feature = (Feature) o;
                if (position == 0){
                    Intent intent = new Intent(mainList.this,Login.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
                else if (position == 1){

                    Intent intent = new Intent(mainList.this,FlashLight.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
                else if (position == 2){
                    Intent intent = new Intent(mainList.this,CurrentWeather.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
                else if (position == 3){
                    Intent intent = new Intent(mainList.this,MapsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
                else if (position == 4){
                    Intent intent = new Intent(mainList.this, Pedometer.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
                final Toast toast = Toast.makeText(mainList.this, "Selected :" + " " + feature, Toast.LENGTH_SHORT);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 1000);

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("mainList tag", "now running onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("mainList tag", "now running onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("mainList tag", "now running onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("mainList tag", "now running onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("mainList tag", "now running onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("mainList tag", "now running onRestart");
    }


    private  List<Feature> getListData() {
        List<Feature> list = new ArrayList<Feature>();
        Feature chat = new Feature("Chatting App", "chatting", "Feel free to chat with others");
        Feature weather = new Feature("Current Weather", "weather", "How is weather today? Check it out");
        Feature maps = new Feature("Maps", "maps", "Where are you now? View it now");
        Feature flash = new Feature("Flashlight", "flashlight", "Is there too dark? Let's light it up");
        Feature pedometer = new Feature("Pedometer", "steps","How many steps have you done?");
        list.add(chat);
        list.add(flash);
        list.add(weather);
        list.add(maps);
        list.add(pedometer);

        return list;
    }
}