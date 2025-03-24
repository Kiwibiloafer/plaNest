package com.example.planest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://planest-b8a65-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private FirebaseAuth firebaseAuth;
    private String UID;
    private static final long DEBOUNCE_DELAY = 400;
    private NotesAdapter notesAdapter;
    private ArrayList<Notes> notes = new ArrayList<>();
    private Timer searchTimer = new Timer();

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
        refreshData("");
    }

    private void initUI() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            UID = firebaseUser.getUid();
        }

        binding.btnCreateNote.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, NotesActivity.class);
            startActivity(i);
        });

        binding.fabNotes.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, NotesActivity.class);
            startActivity(i);
        });

        binding.btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutPopup();
            }
        });

        binding.etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTimer.cancel();
                searchTimer = new Timer();
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> refreshData(s.toString()));
                    }
                }, DEBOUNCE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etSearchBar.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            return actionId == EditorInfo.IME_ACTION_SEARCH;
        });
    }

    private void showLogoutPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("You sure to Logout");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void refreshData(String query) {
        notes.clear();
        database.getReference().child("notes").orderByChild("user_id").equalTo(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Notes note = snapshot1.getValue(Notes.class);
                    note.setNotes_id(snapshot1.getKey());
                    if (note.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        notes.add(note);
                    }
                }

                if (notes.isEmpty()) {
                    binding.llEmpty.setVisibility(View.VISIBLE);
                    binding.llFill.setVisibility(View.GONE);
                    binding.fabNotes.setVisibility(View.GONE);
                } else {
                    binding.llEmpty.setVisibility(View.GONE);
                    binding.llFill.setVisibility(View.VISIBLE);
                    binding.fabNotes.setVisibility(View.VISIBLE);
                    binding.tvCount.setText(String.valueOf(notes.size()));

                    notesAdapter = new NotesAdapter(notes, HomeActivity.this);
                    binding.rvNotes.setAdapter(notesAdapter);
                    binding.rvNotes.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
