package com.shehack.medifind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.shehack.medifind.ui.login.LoginActivity;

public class WelcomeActivity extends AppCompatActivity {

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        prefs = getSharedPreferences("MyPref",0);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn",false);

        if(isLoggedIn){
            Intent i = new Intent(WelcomeActivity.this, MapsActivity.class);
            startActivity(i);
        }

        else{
            Intent i = new Intent(WelcomeActivity.this,LoginActivity.class);
            startActivity(i);
        }

    }
}
