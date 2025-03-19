package com.example.planest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.planest.Adapter.NotesAdapter;
import com.example.planest.Model.Notes;
import com.example.planest.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://planest-b8a65-default-rtdb.asia-southeast1.firebasedatabase.app/");

    private FirebaseAuth firebaseAuth;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initUI();


    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void initUI(){

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!= null){
            UID = firebaseUser.getUid();
        }

        binding.btnCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, NotesActivity.class);
                startActivity(i);
            }
        });

        binding.fabNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, NotesActivity.class);
                startActivity(i);
            }
        });
        refreshData();
    }

    private void refreshData(){
        ArrayList<Notes> notes = new ArrayList<>();
        database.getReference().child("notes").orderByChild("user_id").equalTo(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Notes note = snapshot1.getValue(Notes.class);
                    notes.add(note);
                }

                if(notes.size() == 0){
                    binding.llEmpty.setVisibility(View.VISIBLE);
                    binding.llFill.setVisibility(View.GONE);
                    binding.fabNotes.setVisibility(View.GONE);
                }else {
                    binding.llEmpty.setVisibility(View.GONE);
                    binding.llFill.setVisibility(View.VISIBLE);
                    binding.fabNotes.setVisibility(View.VISIBLE);

                    binding.tvCount.setText(notes.size());

                    NotesAdapter notesAdapter = new NotesAdapter(notes, HomeActivity.this);
                    binding.rvNotes.setAdapter(notesAdapter);
                    binding.rvNotes.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}