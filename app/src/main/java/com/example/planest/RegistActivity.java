package com.example.planest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planest.databinding.ActivityRegistBinding;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class RegistActivity extends AppCompatActivity {

    private ActivityRegistBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.btnRegistAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = binding.etEmailRegis.getText().toString().trim();
        String password = binding.etPasswordRegis.getText().toString().trim();
        String confirmPassword = binding.etPasswordRegisConfirm.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            displayToast("All coloumn must be filled");
            return;
        }

        if (!password.equals(confirmPassword)) {
            displayToast("Fill the Password correctly");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            displayToast("Regist success");
                            startActivity(new Intent(RegistActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            displayToast("Regist failed:S " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
