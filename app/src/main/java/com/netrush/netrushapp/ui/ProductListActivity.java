package com.netrush.netrushapp.ui;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.netrush.netrushapp.R;

public class ProductListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        Log.v("Test", pref.getString("Email", "fail"));
    }
}
