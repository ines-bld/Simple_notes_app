package com.example.mynotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    List<Note> notesList;
    Context context;

    public RecycleViewAdapter(List<Note> notesList, Context context) {
        this.notesList = notesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_line_note, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //holder.tv_content.setText(notesList.get(position).getText());
        Note note = notesList.get(position);
        holder.tv_content.setText(getNotePreview(note));
        Glide.with(context)
                .load(R.drawable.pic)
                .into(holder.iv_notePicture);
    }
    private String getNotePreview(Note note) {
        String content = note.getText();
        int previewLength = 10;
        if (content.length() <= previewLength) {
            return content;
        }
        String preview = content.substring(0, previewLength) + "...";
        return preview;
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public List<Note> getNotes() {
        return notesList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv_notePicture;
        TextView tv_content;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_notePicture = itemView.findViewById(R.id.iv_notePicture);
        }
    }
}
