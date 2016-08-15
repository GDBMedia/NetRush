package com.netrush.netrushapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.emailInput) EditText mEmail;
    @Bind(R.id.passwordInput) EditText mPasswordInput;
    @Bind(R.id.loginButton) Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    public void onClick(View v) {
        if(v == mLoginButton) {
            String Email = mLoginButton.getText().toString().trim();
            String password = mPasswordInput.getText().toString().trim();

            // do OAuth Amazon log in stuff

            Intent intent = new Intent(LoginActivity.this, ProductsActivity.class);
            startActivity(intent);
        }
    }
}
