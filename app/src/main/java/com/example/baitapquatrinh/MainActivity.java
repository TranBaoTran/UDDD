package com.example.baitapquatrinh;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    List<note> notes;
    ListView listNotes;
    Button addButton;
    NoteArrayAdapter noteArrayAdapter;
    private final static int SHOW_NOTE_REQUEST=1;
    private final static int CREATE_NOTE_REQUEST=2;
    Database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_note);
        addButton=findViewById(R.id.addButton);
        listNotes=findViewById(R.id.listNotes);
        db=new Database(this);
        notes=db.getAllNotes();
        noteArrayAdapter=new NoteArrayAdapter(this,R.layout.note_view,notes);
        listNotes.setAdapter(noteArrayAdapter);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (!Environment.isExternalStorageManager()){
//                try {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                    intent.addCategory("android.intent.category.DEFAULT");
//                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
//                    startActivityIfNeeded(intent, 101);
//                }catch (Exception ex) {
//                    Intent intent = new Intent();
//                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                    startActivityIfNeeded(intent, 101);
//                }
//            }
//        }

        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                note n = notes.get(position);
                Intent oldNote = new Intent(MainActivity.this, submain.class);
                oldNote.putExtra("id", n.getId());
                oldNote.putExtra("title", n.getTitle());
                oldNote.putExtra("content", n.getContent());
                String[] stringArray = n.getUris().toArray(new String[0]);
                oldNote.putExtra("imgArray", stringArray);
                startActivity(oldNote);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newNote =new Intent(getApplicationContext(),submain.class);
                startActivity(newNote);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            notes=db.getAllNotes();
            noteArrayAdapter.notifyDataSetChanged();
        }
    }
}