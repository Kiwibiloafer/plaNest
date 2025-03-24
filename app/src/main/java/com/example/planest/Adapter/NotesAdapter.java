package com.example.planest.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planest.Model.Notes;
import com.example.planest.NotesActivity;
import com.example.planest.databinding.HomeListBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private ArrayList<Notes> arrayList;
    private Context ctx;
    private FirebaseDatabase database;

    public NotesAdapter(ArrayList<Notes> arrayList, Context ctx) {
        this.arrayList = arrayList;
        this.ctx = ctx;
        this.database = FirebaseDatabase.getInstance("https://planest-b8a65-default-rtdb.asia-southeast1.firebasedatabase.app");
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        HomeListBinding binding = HomeListBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        Notes currentItem = arrayList.get(position);
        holder.bind(currentItem);

        holder.binding.llCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, NotesActivity.class);
                i.putExtra("title", arrayList.get(position).getTitle());
                i.putExtra("content", arrayList.get(position).getNotes());
                i.putExtra("id", arrayList.get(position).getNotes_id());
                ctx.startActivity(i);
            }
        });

        holder.binding.btnOptionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ctx)
                        .setTitle("Delete Notes")
                        .setMessage("You sure to Delete this Notes")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            String notesId = arrayList.get(position).getNotes_id();
                            database.getReference().child("notes").child(notesId).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ctx, "Note Deleted", Toast.LENGTH_SHORT).show();
                                            arrayList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, arrayList.size());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("FirebaseError", "Failed to Delete", e);
                                            Toast.makeText(ctx, "Failed to Delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final HomeListBinding binding;

        public ViewHolder(HomeListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Notes notes){
            binding.tvHeader.setText(notes.getTitle());
            binding.tvSubtitle.setText(notes.getNotes());
            binding.tvTime.setText(notes.getLast_modified());
        }
    }
}
