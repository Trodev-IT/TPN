package com.trodev.petdiary.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.api.Context;
import com.trodev.petdiary.R;
import com.trodev.petdiary.activity.NoteDetailsActivity;
import com.trodev.petdiary.model.Note;
import com.trodev.petdiary.util.Utility;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private android.content.Context context;
    private List<Note> notes;

    public NotesAdapter(android.content.Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {

        Note note = notes.get(position);

        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText("Species:- "+note.getContent());
        holder.notes_Descriptions_txtview.setText(note.getDescription());
        holder.timestampTextView.setText("Update time & date:- "+Utility.timestampToString(note.getTimestamp()));

        try {
            Glide.with(context).load(note.getPhotoUrl()).into(holder.icon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener((v)->{
            Intent intent = new Intent(context,NoteDetailsActivity.class);
            intent.putExtra("nID",note.getnID());
            intent.putExtra("title", note.getTitle());
            intent.putExtra("content", note.getContent());
            intent.putExtra("age", note.getAge());
            intent.putExtra("address", note.getAddress());
            intent.putExtra("description", note.getDescription());
            intent.putExtra("feedingTime", note.getFeedingTime());
            intent.putExtra("food", note.getFood());
            intent.putExtra("medicine", note.getMedicine());
            intent.putExtra("notes", note.getNotes());
            intent.putExtra("ownerName", note.getOwnerName());
            intent.putExtra("phone", note.getPhone());
            intent.putExtra("walkingTime", note.getWalkingTime());
            intent.putExtra("photoUrl", note.getPhotoUrl());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView,contentTextView,timestampTextView,notes_Descriptions_txtview;
        ImageView icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.note_title_text_view);
            contentTextView = itemView.findViewById(R.id.note_content_text_view);
            notes_Descriptions_txtview = itemView.findViewById(R.id.notes_Descriptions_txtview);
            timestampTextView = itemView.findViewById(R.id.note_timestamp_text_view);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
