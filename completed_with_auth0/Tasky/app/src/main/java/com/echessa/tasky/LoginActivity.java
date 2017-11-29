package com.echessa.tasky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.echessa.tasky.utils.CredentialsManager;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Your own Activity code
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Auth0 account = new Auth0(this);

        String accessToken = CredentialsManager.getCredentials(this).getAccessToken();
        if (accessToken == null) {
            WebAuthProvider.init(account)
                    .withConnectionScope("openid", "offline_access")
                    .start(LoginActivity.this, mCallback);
            return;
        }

        AuthenticationAPIClient aClient = new AuthenticationAPIClient(account);
        aClient.userInfo(accessToken)
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(final UserProfile payload) {
                        showToastText("Automatic Login Success");

                        startActivity(new Intent(LoginActivity.this, TaskListActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        showToastText("Session Expired, please Log In");

                        CredentialsManager.deleteCredentials(LoginActivity.this);

                        WebAuthProvider.init(account)
                                .withConnectionScope("openid", "offline_access")
                                .start(LoginActivity.this, mCallback);
                    }
                });
    }

    private void showToastText(final String text) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final AuthCallback mCallback = new AuthCallback() {
        @Override
        public void onSuccess(Credentials credentials) {
            showToastText("Log In - Success");
            CredentialsManager.saveCredentials(LoginActivity.this, credentials);
            startActivity(new Intent(LoginActivity.this, TaskListActivity.class));
            finish();
        }

        @Override
        public void onFailure(Dialog dialog) {
            showToastText("Log In - Cancelled");
        }

        @Override
        public void onFailure(AuthenticationException exception) {
            showToastText("Log In - Error Occurred");
        }
    };
}
