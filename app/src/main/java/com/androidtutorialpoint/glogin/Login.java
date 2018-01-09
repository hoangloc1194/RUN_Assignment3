package com.androidtutorialpoint.glogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class Login extends AppCompatActivity {
    TextView register;
    EditText username, password;
    Button loginButton;
    String user, pass;

    private CheckBox rem_userpass;
    SharedPreferences sharedPreferences;
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASS = "password";


    public static final String EXTRA_MESSAGE = "com.androidtutorialpoint.glogin.MESSAGE";
    protected void onStart() {
        super.onStart();
        Log.i("Child Login", "Now running onStart");
    }

    @Override
    protected void onResume() {
        Log.i("Child Login", "Now running onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("Child Login", "Now running onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("Child Login", "Now running onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i("Child Login", "Now running onRestart");
        super.onRestart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Child Login", "Now running onCreate");
        setContentView(R.layout.activity_login2);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        rem_userpass = (CheckBox)findViewById(R.id.remember);
        register = (TextView)findViewById(R.id.register);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.loginButton);
        extractPreferences();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences sharedPreferences = getSharedPreferences("myAppRefs", Context.MODE_PRIVATE);
                user = username.getText().toString();
                pass = password.getText().toString();
                sharedPreferences.edit().putString("usernameChildLogin", user).apply();
                if(user.equals("")){
                    username.setError("can't be blank");
                }
                else if(pass.equals("")){
                    password.setError("can't be blank");
                }
                else{
                    String url = "https://fir-demo1-22ce0.firebaseio.com/users.json";
                    final ProgressDialog pd = new ProgressDialog(Login.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            if(s.equals("null")){
                                Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                            }
                            else{
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if(!obj.has(user)){
                                        Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                                    }
                                    else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                        UserDetails.username = user;
                                        UserDetails.password = pass;
                                        managePrefs();
//                                        Intent intent = new Intent(Login.this,Users.class);
//                                        intent.putExtra(EXTRA_MESSAGE, user);
                                        startActivity(new Intent(Login.this,Users.class));
                                        finish();

                                    }
                                    else {
                                        Toast.makeText(Login.this, "incorrect password", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }
                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                    rQueue.add(request);
                }

            }
        });
    }
    private void extractPreferences() {
        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            rem_userpass.setChecked(true);
            username.setText(sharedPreferences.getString(KEY_USERNAME, ""));
            password.setText(sharedPreferences.getString(KEY_PASS, ""));
        } else
            rem_userpass.setChecked(false);
    }
    private void managePrefs() { //EditText ed1, EditText ed2
        if (rem_userpass.isChecked()) {
            sharedPreferences
                    .edit()
                    .putString(KEY_USERNAME, username.getText().toString().trim())
                    .putString(KEY_PASS, password.getText().toString().trim())
                    .putBoolean(KEY_REMEMBER, true)
                    .apply();
        } else {
            sharedPreferences.edit()
                    .putBoolean(KEY_REMEMBER, false)
                    .remove(KEY_PASS)
                    .remove(KEY_USERNAME)
                    .apply();
        }
    }

}
