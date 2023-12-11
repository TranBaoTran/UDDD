package com.example.baitapquatrinh;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Import statements...

public class submain extends AppCompatActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private Button backButton;
    private Button remindButton;
    private Button imgButton;
    private Button saveButton;
    private Button trashButton;
    private EditText titleText;
    private EditText contentText;
    private List<Uri> imgList;
    private ListView listIMG;
    private ImgArrayAdapter imgArrayAdapter;
    private boolean created=false;
    Database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_note);

        initViews();
        setListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        remindButton = findViewById(R.id.remindButton);
        imgButton = findViewById(R.id.imgButton);
        saveButton = findViewById(R.id.saveButton);
        trashButton = findViewById(R.id.trashButton);
        titleText = findViewById(R.id.titleText);
        contentText = findViewById(R.id.contentText);
        listIMG = findViewById(R.id.listIMG);
        imgList = new ArrayList<>();
        imgArrayAdapter = new ImgArrayAdapter(this, R.layout.img_view, imgList);
        listIMG.setAdapter(imgArrayAdapter);
        db=new Database(this);
        display();
    }

    private void setListeners() {
        saveButton.setOnClickListener(view -> saveNote());

        backButton.setOnClickListener(view -> finish());

        remindButton.setOnClickListener(view -> showDateTimePickerDialog());

        imgButton.setOnClickListener(view -> showImagePickerDialog());
    }
    private void display(){
        Intent intent= getIntent();
        long id = intent.getLongExtra("id",0);
        if(id==0){
            trashButton.setVisibility(View.INVISIBLE);
            created=false;
        }else{
            String title= intent.getStringExtra("title");
            String content= intent.getStringExtra("content");
            String[] receivedArray = getIntent().getStringArrayExtra("imgArray");
            List<String> receivedList = Arrays.asList(receivedArray);
            titleText.setText(title);
            contentText.setText(content);
            for( String uri : receivedList){
                imgList.add(Uri.parse(uri));
            }
            imgArrayAdapter.notifyDataSetChanged();
            created=true;
        }
    }
    public void saveNote(){
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d, h:mm a", Locale.US);
        String formattedDateTime = formatter.format(now);
        List<String> val = new ArrayList<>();
        for( Uri uri : imgList){
            val.add(uri.toString());
        }
        note n=new note(titleText.getText().toString(),contentText.getText().toString(),formattedDateTime,val);
        if (created){
            Toast.makeText(this, String.valueOf(db.updateNote(n)), Toast.LENGTH_SHORT).show();
        }else {
            db.insertNote(n);
        }
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
    private void showDateTimePickerDialog() {
        // Get the current date and time
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create Date Picker Dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Set the selected date
                calendar.set(year, monthOfYear, dayOfMonth);

                // Create Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        submain.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set the selected time
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                            }
                        },
                        hour,minute, true);
                timePickerDialog.show();
            }
        }, year, month,day);
        datePickerDialog.show();
    }

    private void showImagePickerDialog() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Intent chooserIntent = Intent.createChooser(intent, "Chọn hình ảnh");
        startActivityForResult(chooserIntent, PICK_IMAGES_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGES_REQUEST && data != null) {
                handleImageSelection(data);
            }
        }
    }

    private void handleImageSelection(Intent data) {
        ClipData clipData = data.getClipData();
        if (clipData != null) {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                Uri selectedImageUri = clipData.getItemAt(i).getUri();
                imgList.add(selectedImageUri);
            }
        } else {
            Uri selectedImageUri = data.getData();
            imgList.add(selectedImageUri);
        }
        displaySelectedImages();
    }

    private void displaySelectedImages() {
        imgArrayAdapter.notifyDataSetChanged();
    }
}

