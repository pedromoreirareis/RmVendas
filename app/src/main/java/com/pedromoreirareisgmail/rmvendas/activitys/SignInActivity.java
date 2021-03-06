package com.pedromoreirareisgmail.rmvendas.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Net;
import com.pedromoreirareisgmail.rmvendas.constant.ConstIntents;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SignInActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mContext = SignInActivity.this;

        // Referencia button no layout
        SignInButton mSignInButton = findViewById(R.id.but_sign_in);

        // Definindo tamanho do Button
        mSignInButton.setSize(SignInButton.SIZE_WIDE);

        // Instancia o Listener
        mSignInButton.setOnClickListener(this);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.but_sign_in:

                if (Net.verifyConnect(mContext)) {

                    signIn();

                } else {

                    Toast.makeText(mContext, getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void signIn() {

        Intent intentSignIn = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intentSignIn, ConstIntents.RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstIntents.RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(Objects.requireNonNull(account));

            } else {

                Toast.makeText(mContext, getString(R.string.error_signin_failed_no_account), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Google Sign In Failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            if (mAuth.getCurrentUser() != null) {

                                startActivity(new Intent(mContext, MainActivity.class));
                                finish();
                            }

                        } else {

                            Toast.makeText(
                                    mContext,
                                    getString(R.string.error_signin_failed),
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(mContext, getString(R.string.msg_connection_failed), Toast.LENGTH_LONG).show();
    }
}
