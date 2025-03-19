package com.example.planest;

import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;

public class NotesActivity extends AppCompatActivity {

    private ActivityNotesBinding binding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        initUI();
    }
    private void initUI(){
        binding.btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notes notes = new Notes(binding.etHeader.getText().toString(), binding.etContent.getText().toString(), "test", "");
                database.getReference().child("notes").push().setValue(notes).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(NotesActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotesActivity.this, "Failed to Sync", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}