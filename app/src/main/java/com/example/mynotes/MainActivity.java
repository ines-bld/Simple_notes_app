package com.example.mynotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Note> notesList = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final int REQUEST_CODE_EDIT_NOTE = 1;

    public static final String NOTES_LIST_KEY = "notes_list";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(NOTES_LIST_KEY, (ArrayList<? extends Parcelable>) notesList);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            notesList = savedInstanceState.getParcelableArrayList(NOTES_LIST_KEY);
        }

        ImageView addNote = findViewById(R.id.modify_icon);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show an input dialog to write a note
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter a note");

                // Add an EditText view to the dialog
                final EditText input = new EditText(MainActivity.this);
                builder.setView(input);

                // Add a confirm button to the dialog
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the text from the input field
                        String note = input.getText().toString();
                        int id = -1;
                        boolean uniqueIdFound = false;
                        while (!uniqueIdFound) {
                            // Generate a new ID
                            id = View.generateViewId();
                            // Check if the ID already exists in the list of notes
                            uniqueIdFound = true;
                            for (Note n : notesList) {
                                if (n.getId() == id) {
                                    uniqueIdFound = false;
                                    break;
                                }
                            }
                        }
                        Calendar calendar = Calendar.getInstance();
                        Date currentDate = calendar.getTime();
                        Note n = new Note(id, note, currentDate);
                        notesList.add(n);
                    }
                });
                // Add the Cancel button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                // Show the dialog
                builder.show();
            }
        });

        recyclerView = findViewById(R.id.lv_notesList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecycleViewAdapter(notesList, this);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public void onLongPress(MotionEvent e) {
                            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                            if (child != null) {
                                int position = recyclerView.getChildAdapterPosition(child);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Are you sure you want to delete this item?");
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Delete the item from your data source and notify the adapter
                                        // (assuming you have a list of items called "mItems" and an adapter called "mAdapter")
                                        notesList.remove(position);
                                        mAdapter.notifyItemRemoved(position);
                                    }
                                });
                                builder.setNegativeButton("Cancel", null);
                                builder.show();
                            }
                        }
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                            if (child != null) {
                                int position = recyclerView.getChildAdapterPosition(child);
                                // Get the selected note from the adapter
                                RecycleViewAdapter adapter = (RecycleViewAdapter) recyclerView.getAdapter();
                                Note selectedNote = adapter.getNotes().get(position);
                                // Get the note details
                                String content = selectedNote.getText();
                                Date date = selectedNote.getDate();
                                // Launch the new activity with the details of the note
                                Intent intent = new Intent(MainActivity.this, NoteDetailsActivity.class);
                                intent.putExtra("note_content", content);
                                intent.putExtra("note_date", date.getTime());
                                intent.putExtra("note_index", position);
                                startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
                                return true;
                            }
                            return false;
                        }
                    });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
                    });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_NOTE && resultCode == Activity.RESULT_OK) {
            // get the modified note text from the intent
            String modifiedNoteText = data.getStringExtra("modified_note_text");
            int selectedNoteIndex = data.getIntExtra("note_index", -1);
            // update the note text in the adapter
            if (selectedNoteIndex != -1) {
                notesList.get(selectedNoteIndex).setText(modifiedNoteText);
                mAdapter.notifyDataSetChanged();
                // save the updated note in SharedPreferences
                saveNotes();
            }
        }
    }

    private void saveNotes() {
        Gson gson = new Gson();
        String json = gson.toJson(notesList);
        SharedPreferences.Editor editor = getSharedPreferences("notes", MODE_PRIVATE).edit();
        editor.putString("notesList", json);
        editor.apply();
    }


}