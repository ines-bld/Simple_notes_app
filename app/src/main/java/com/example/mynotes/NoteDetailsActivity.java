package com.example.mynotes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;
import java.util.Locale;

public class NoteDetailsActivity extends AppCompatActivity {
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for this activity
        setContentView(R.layout.activity_notes_details);

        // Get the note details from the intent extras
        String content = getIntent().getStringExtra("note_content");
        long dateInMillis = getIntent().getLongExtra("note_date", 0);
        int id = getIntent().getIntExtra("note_index", -1);
        Date date = new Date(dateInMillis);

        // Populate the UI elements with the note details
        TextView contentTextView = findViewById(R.id.note_content);
        contentTextView.setText(content);
        TextView dateTextView = findViewById(R.id.note_date);
        dateTextView.setText(date.toString());

        ImageView modifyButton = findViewById(R.id.modify_icon);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a dialog with an EditText field
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteDetailsActivity.this);
                builder.setTitle("Modify Note");
                final EditText input = new EditText(NoteDetailsActivity.this);
                input.setText(contentTextView.getText().toString());
                builder.setView(input);

                // Add the Modify button
                builder.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newContent = input.getText().toString();
                        contentTextView.setText(newContent);
                        // save the modified text in a variable and pass it back to MainActivity
                        Intent intent = new Intent();
                        intent.putExtra("modified_note_text", newContent);
                        intent.putExtra("note_index", id);
                        setResult(Activity.RESULT_OK, intent);
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


        ImageView vocal = findViewById(R.id.iv_vocal);
        vocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if(i == TextToSpeech.SUCCESS){
                            tts.setLanguage(Locale.US);
                            tts.setSpeechRate(1.0f);
                            tts.speak(contentTextView.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                });
            }

        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
