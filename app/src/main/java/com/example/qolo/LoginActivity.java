package com.example.qolo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.qolo)
                        .setTheme(R.style.ThemeOverlay_MaterialComponents_Dark)
                        .build(),
                RC_SIGN_IN);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(getApplicationContext(),gso);

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            Intent intent=new Intent(getApplicationContext(),MainScreenActivity.class);
            startActivity(intent);
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    FirebaseGoogleAuth(account);
                }catch (Exception e){
                    e.printStackTrace();
                    FirebaseGoogleAuth(null);
                }
            } else {
            }
        }
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        if (requestCode==RC_SIGN_IN){
            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                FirebaseGoogleAuth(account);
            }catch (Exception e){
                e.printStackTrace();
                FirebaseGoogleAuth(null);
            }
        }
    }*/
    private void FirebaseGoogleAuth(GoogleSignInAccount acc){
        AuthCredential authCredential= GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Sign In Successfull", Toast.LENGTH_SHORT).show();
                    FirebaseUser user=mAuth.getCurrentUser();
                    UpdateUI(user);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                    UpdateUI(null);
                }
            }
        });
    }
    private void UpdateUI(FirebaseUser firebaseUser){
        GoogleSignInAccount googleSignInAccount=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (googleSignInAccount!=null){
            Log.i("Result",googleSignInAccount.toString());
            String PersonName=googleSignInAccount.getDisplayName();
            String GivenName=googleSignInAccount.getGivenName();
            String Email=googleSignInAccount.getEmail();
            Uri photo=googleSignInAccount.getPhotoUrl();
            Toast.makeText(this, PersonName+" Log In As"+Email, Toast.LENGTH_SHORT).show();
        }
    }
}