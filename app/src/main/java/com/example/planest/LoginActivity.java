package com.example.planest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planest.databinding.ActivityLoginBinding;
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


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private FirebaseAuth firebaseAuth;

    private GoogleSignInClient googleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("167720649129-8m1j9kkrj61s0pv730gdihbtveos9bo4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 100);
            }
        });
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginEmailPassword();
            }
        });
        binding.btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(i);

            }
        });
    }

    private void LoginEmailPassword(){
        String Email = binding.etEmail.getText().toString().trim();
        String Password = binding.etPassword.getText().toString().trim();

        if (Email.isEmpty() || Password.isEmpty()) {
            displayToast("Email and password cannot be empty");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            displayToast("Login Success");
                        } else {
                            displayToast("Login failed: " + task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(signInAccountTask.isSuccessful()){
                String s = "Google Sign in Successful";
                displayToast(s);
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    if(googleSignInAccount != null){
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    displayToast("Success");
                                }else{
                                    displayToast("Authentication Failed: "+ task.getException().getMessage());
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void displayToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}