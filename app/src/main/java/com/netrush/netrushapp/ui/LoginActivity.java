package com.netrush.netrushapp.ui;

import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.amazon.identity.auth.device.shared.APIListener;
import com.amazon.identity.auth.device.AuthError;
import com.netrush.netrushapp.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

/**
 * Copyright 2012-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License.
 *
 */
public class LoginActivity extends Activity{

    private static final String TAG = LoginActivity.class.getName();
    private static final String[] APP_SCOPES= {"profile"};
    private ImageButton mLoginButton;
    private AmazonAuthorizationManager mAuthManager;
    private boolean mIsLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            mAuthManager = new AmazonAuthorizationManager(this, Bundle.EMPTY);
        }
        catch(IllegalArgumentException e)
        {
            showAuthToast("APIKey is incorrect or does not exist.");
            Log.e(TAG, "Unable to Use Amazon Authorization Manager. APIKey is incorrect or does not exist.", e);
        }
        setContentView(R.layout.activity_login);
        initializeUI();
    }

    /**
     * Initializes all of the UI elements in the activity
     */
    private void initializeUI() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        mLoginButton = (ImageButton) findViewById(R.id.loginButton);
        if (pref.getBoolean("LoggedIn", true)) {
//            setLoggingInState(true);
            Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
            startActivity(intent);
            finish();
        }
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("LoggedIn", true);
                editor.apply();
                setLoggingInState(true);
                mAuthManager.authorize(APP_SCOPES, Bundle.EMPTY, new AuthListener());
            }
        });
    }

    /**
     * {@link AuthorizationListener} which is passed in to authorize calls made on the {@link AmazonAuthorizationManager} member.
     * Starts getToken workflow if the authorization was successful, or displays a toast if the user cancels authorization.
     * @implements {@link AuthorizationListener}
     */
    private class AuthListener implements AuthorizationListener{

        /**
         * Authorization was completed successfully.
         * Display the profile of the user who just completed authorization
         * @param response bundle containing authorization response. Not used.
         */
        @Override
        public void onSuccess(Bundle response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
            mAuthManager.getProfile(new ProfileListener());
        }


        /**
         * There was an error during the attempt to authorize the application.
         * Log the error, and reset the profile text view.
         * @param ae the error that occurred during authorize
         */
        @Override
        public void onError(AuthError ae) {
            Log.e(TAG, "AuthError during authorization", ae);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAuthToast("Error during authorization.  Please try again.");
                    resetProfileView();
                    setLoggingInState(false);
                }
            });
        }

        /**
         * Authorization was cancelled before it could be completed.
         * A toast is shown to the user, to confirm that the operation was cancelled, and the profile text view is reset.
         * @param cause bundle containing the cause of the cancellation. Not used.
         */
        @Override
        public void onCancel(Bundle cause) {
            Log.e(TAG, "User cancelled authorization");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAuthToast("Authorization cancelled");
                    resetProfileView();
                }
            });
        }
    }

    /**
     * Sets the text in the mProfileText {@link TextView} to the prompt it originally displayed.
     */
    private void resetProfileView(){
        setLoggingInState(false);
    }

    /**
     * Sets the state of the application to reflect that the user is not currently authorized.
     */
    private void setLoggedOutState(){
        mLoginButton.setVisibility(Button.VISIBLE);
        mIsLoggedIn = false;
        resetProfileView();
    }

    private void setLoggedInState(){
        mIsLoggedIn = true;
        setLoggingInState(false);
    }

    /**
     * Turns on/off display elements which indicate that the user is currently in the process of logging in
     * @param loggingIn whether or not the user is currently in the process of logging in
     */
    private void setLoggingInState(final boolean loggingIn){
            if(mIsLoggedIn){
                Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                mLoginButton.setVisibility(Button.VISIBLE);
            }
        }
//    }

    private void showAuthToast(String authToastMessage){
        Toast authToast = Toast.makeText(getApplicationContext(), authToastMessage, Toast.LENGTH_LONG);
        authToast.setGravity(Gravity.CENTER, 0, 0);
        authToast.show();
    }

    /**
     * Updates the login state of the user, based on whether or not the user is currently authorized.
     */
    private void updateLoginState(){
        mAuthManager.getToken(APP_SCOPES, new APIListener() {

            /**
             * If the user is logged in, update the view with the user's profile information
             * If the user is logged out, set the logged out state
             */
            @Override
            public void onSuccess(Bundle response) {
                final String authzToken = response.getString(AuthzConstants.BUNDLE_KEY.TOKEN.val);
                mIsLoggedIn = !TextUtils.isEmpty(authzToken);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if(mIsLoggedIn){
                            setLoggingInState(true);
                            mAuthManager.getProfile(new ProfileListener());
                        }
                        else{
                            setLoggedOutState();
                        }
                    }
                });
            }

            /**
             * Handles the case where there is an error in the getProfile call
             */
            @Override
            public void onError(AuthError ae) {
                Log.e(TAG, "AuthError during updateLoginState", ae);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoggedOutState();
                    }
                });
            }
        });
    }

    /**
     * {@link AuthListener} which is passed in to the {@link AmazonAuthorizationManager} getProfile api call
     */
    private class ProfileListener implements APIListener{

        /**
         * Updates the profile view with data from the successful getProfile response.
         * Sets app state to logged in
         */
        @Override
        public void onSuccess(Bundle response) {
            Bundle profileBundle = response.getBundle(AuthzConstants.BUNDLE_KEY.PROFILE.val);
            if(profileBundle == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoggedOutState();
                        String errorMessage = "Error retrieving profile information.\nPlease log in again";
                        Toast errorToast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                        errorToast.setGravity(Gravity.CENTER, 0, 0);
                        errorToast.show();
                    }
                });
            }
            else{
                StringBuilder profileBuilder = new StringBuilder();
                profileBuilder.append(String.format(profileBundle.getString(AuthzConstants.PROFILE_KEY.EMAIL.val)));
                Log.v("Profile User ID", profileBundle.getString(AuthzConstants.PROFILE_KEY.USER_ID.val));
                final String profile = profileBuilder.toString();
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("UserEmail", profile);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoggedInState();
                    }
                });
            }
        }

        /**
         * Updates profile view to reflect that there was an error while retrieving profile information
         */
        @Override
        public void onError(AuthError ae) {
            Log.e(TAG, ae.getMessage(), ae);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLoggingInState(false);
                }
            });
        }
    }
}
