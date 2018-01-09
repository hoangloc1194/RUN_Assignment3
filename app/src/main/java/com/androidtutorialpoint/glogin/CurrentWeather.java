package com.androidtutorialpoint.glogin;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidtutorialpoint.glogin.model.Weather;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class CurrentWeather extends Activity {
    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;
    private EditText insertText;
    private TextView hum;
    private ImageView imgView;
    private Button submit;
    private Spinner spinner;

    String city;
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("CurrentWeather", "Now running onStart");
    }

    @Override
    protected void onResume() {
        Log.i("CurrentWeather", "Now running onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("CurrentWeather", "Now running onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("CurrentWeather", "Now running onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i("CurrentWeather", "Now running onRestart");
        super.onRestart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("CurrentWeather", "Now running onCreate");
        setContentView(R.layout.activity_current_weather);
        cityText = (TextView) findViewById(R.id.cityText);
        condDescr = (TextView) findViewById(R.id.condDescr);
        temp = (TextView) findViewById(R.id.temp);
        hum = (TextView) findViewById(R.id.hum);
        press = (TextView) findViewById(R.id.press);
        windSpeed = (TextView) findViewById(R.id.windSpeed);
        windDeg = (TextView) findViewById(R.id.windDeg);
        imgView = (ImageView) findViewById(R.id.condIcon);
        submit = findViewById(R.id.submit);
        spinner = (Spinner)findViewById(R.id.spinner);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = spinner.getSelectedItem().toString();
                switch (text) {
                    case "HoChiMinh": {
                        city = "Thanh%20Pho%20Ho%20Chi%20Minh,VN" + "&appid=4c7a39dfab4f90078f28de5b82810b3d";
                        Toast.makeText(CurrentWeather.this, "Successfully Loaded Current Weather HoChiMinh", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "Hanoi":{
                        city = "Hanoi,VN" + "&appid=4c7a39dfab4f90078f28de5b82810b3d";
                        Toast.makeText(CurrentWeather.this, "Successfully Loaded Current Weather Hanoi", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "London":{
                        city = "London,UK" + "&appid=4c7a39dfab4f90078f28de5b82810b3d";
                        Toast.makeText(CurrentWeather.this, "Successfully Loaded Current Weather London", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "Melbourne":{
                        city = "Melbourne,AU" + "&appid=4c7a39dfab4f90078f28de5b82810b3d";
                        Toast.makeText(CurrentWeather.this, "Successfully Loaded Current Weather Melbourne", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "Manila":{
                        city = "Manila,PH" + "&appid=4c7a39dfab4f90078f28de5b82810b3d";
                        Toast.makeText(CurrentWeather.this, "Successfully Loaded Current Weather Manila", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                int newHeight = 400;
                int newWidth = 350;
                imgView.requestLayout();
                // Apply the new height for ImageView programmatically
                imgView.getLayoutParams().height = newHeight;

                // Apply the new width for ImageView programmatically
                imgView.getLayoutParams().width = newWidth;

                // Set the scale type for ImageView image scaling
                imgView.setScaleType(ImageView.ScaleType.FIT_XY);
                new JSONWeatherTask().execute(new String[]{city});
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {
        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                // Let's retrieve the icon
                weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(img);
            }

            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + " Celsius");
            hum.setText(": " + weather.currentCondition.getHumidity() + " %");
            press.setText(": " + weather.currentCondition.getPressure() + " hPa");
            windSpeed.setText(": " + weather.wind.getSpeed() + " MPS");
            windDeg.setText(" " + weather.wind.getDeg() + " Wind Degree");

        }

    }

}
