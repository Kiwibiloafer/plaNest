package com.example.planest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.text.Transliterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planest.Model.Notes;
import com.example.planest.NotesActivity;
import com.example.planest.databinding.HomeListBinding;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private ArrayList<Notes> arrayList;
    private Context ctx;

    public NotesAdapter(ArrayList<Notes> arrayList, Context ctx) {
        this.arrayList = arrayList;
        this.ctx = ctx;
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
