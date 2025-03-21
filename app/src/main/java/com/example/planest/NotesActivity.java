package com.example.planest;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planest.Model.Notes;
import com.example.planest.databinding.ActivityNotesBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotesActivity extends AppCompatActivity {

    private ActivityNotesBinding binding;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://planest-b8a65-default-rtdb.asia-southeast1.firebasedatabase.app/");

    private FirebaseAuth firebaseAuth;

    private static final long DEBOUNCE_DELAY = 300;

    String UID;

    private Runnable searchRunnable;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initUI();
    }
    private void initUI(){

        String title = getIntent().getStringExtra("title");
        binding.etHeader.setText(title);

        String content = getIntent().getStringExtra("content");
        binding.etContent.setText(content);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!= null){
            UID = firebaseUser.getUid();
        }

        binding.btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notes notes = new Notes(binding.etHeader.getText().toString(), binding.etContent.getText().toString(), getCurrentDate(), "", UID);
                database.getReference().child("notes").push().setValue(notes).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(NotesActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FirebaseError", "Failed to Sync", e);
                        Toast.makeText(NotesActivity.this, "Failed to Sync: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.btnBackNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchRunnable != null){
                    handler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();

                searchRunnable = () -> {
                    Log.wtf("Debounce content", s.toString());

                };
                handler.postDelayed(searchRunnable, DEBOUNCE_DELAY);
            }
        });

    }

    public String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
        return simpleDateFormat.format(calendar.getTime());
    }
}