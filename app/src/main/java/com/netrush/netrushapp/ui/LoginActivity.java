package com.netrush.netrushapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.netrush.netrushapp.R;


import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.emailInput) EditText mEmail;
    @Bind(R.id.passwordInput) EditText mPasswordInput;
    @Bind(R.id.loginButton) Button mLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        Log.v("Test", pref.getString("Email", "fail"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mLoginButton.setOnClickListener(this);

        if (pref.getBoolean("LoggedIn", false)) {
            Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == mLoginButton) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            SharedPreferences.Editor editor = pref.edit();
            String Email = mEmail.getText().toString().trim();
            String Password = mPasswordInput.getText().toString().trim();
            editor.putBoolean("LoggedIn", true);
            editor.putString("Email", Email);
            editor.putString("Password", Password);
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
            startActivity(intent);
        }
    }
}

