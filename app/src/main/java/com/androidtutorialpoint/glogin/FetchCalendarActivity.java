package com.androidtutorialpoint.glogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Color;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import static android.view.Gravity.BOTTOM;


public class FetchCalendarActivity extends Activity {
    String date;
    String steps, km;
    String userChildLogin;
    Button finishIt;
    Calendar selected = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    String formattedDate = df.format(selected.getTime());
    CalendarView calendarView;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("Users");
    DatabaseReference myRef2;
    ArrayList<String> Userlist = new ArrayList<String>();


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("CheckCalendarActivity", "Now running onStart");
    }

    @Override
    protected void onResume() {
        Log.i("CheckCalendarActivity", "Now running onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("CheckCalendarActivity", "Now running onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("CheckCalendarActivity", "Now running onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i("CheckCalendarActivity", "Now running onRestart");
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("CheckCalendarActivity", "Now running onCreate");
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("myAppRefs", Context.MODE_PRIVATE);
        userChildLogin = sharedPreferences.getString("usernameChildLogin", null);
        Log.i("usernameChild", userChildLogin);        setContentView(R.layout.activity_fetch_calendar);
        calendarView = (CalendarView) findViewById(R.id.calendarView1);
        selected.set(Calendar.DAY_OF_YEAR, (int) calendarView.getDate());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2)
            {
                final String dateFormat = i2 + "-" + (i1+1) +"-" +i;
                Userlist.clear();
                myRef2 = myRef.child(userChildLogin).child(dateFormat);
                myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Result will be holded Here
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Userlist.add(String.valueOf(dsp.getValue())); //add result into array list
                        }
                        if (!Userlist.isEmpty()){
                            Log.i("list", Userlist.toString());
                            steps = Userlist.get(1);
                            Log.i("steps", steps);
                            km = Userlist.get(0);
                            Log.i("km", km);
                            date = dateFormat;

                        } else {
                            steps = "No Data Record";
                            Log.i("steps", steps);
                            km = "No Data Record";
                            Log.i("km", km);
                            date = dateFormat;
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), "Username: " + userChildLogin +"\n" + "Steps = " + steps + "\n" + "Kilometers: = " + km + "\n" +"Selected day: " + date, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.FILL_HORIZONTAL|BOTTOM,0, 80);
                        toast.getView().setBackgroundColor(Color.RED);
                        toast.show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        });
        finishIt = (Button) findViewById(R.id.finish);
        finishIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FetchCalendarActivity.this,EndingPage.class);
                startActivity(intent);
            }
        });
    }
}
