package com.example.baitapquatrinh;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "your_database_name";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_NOTES = "notes";
    private static final String TABLE_NOTE_IMAGES = "note_images";

    // Common column names
    private static final String KEY_ID = "id";

    // Notes table column names
    private static final String KEY_TIME = "time";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";

    // Note Images table column names
    private static final String KEY_NOTE_ID = "note_id";
    private static final String KEY_IMAGE_URI = "image_uri";

    // Constructor
    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tables
        String createNotesTable = "CREATE TABLE " + TABLE_NOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TIME + " TEXT,"
                + KEY_TITLE + " TEXT,"
                + KEY_CONTENT + " TEXT"
                + ")";

        String createNoteImagesTable = "CREATE TABLE " + TABLE_NOTE_IMAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NOTE_ID + " INTEGER,"
                + KEY_IMAGE_URI + " TEXT,"
                + "FOREIGN KEY (" + KEY_NOTE_ID + ") REFERENCES " + TABLE_NOTES + "(" + KEY_ID + ")"
                + ")";

        db.execSQL(createNotesTable);
        db.execSQL(createNoteImagesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE_IMAGES);

        // Create new tables
        onCreate(db);
    }

    // Insert a new note and its images
    public long insertNote(note n) {
        SQLiteDatabase db = this.getWritableDatabase();
        long noteId;
        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(KEY_TIME, n.getSaveDate());
            values.put(KEY_TITLE, n.getTitle());
            values.put(KEY_CONTENT, n.getContent());

            // Insert the note
            noteId = db.insert(TABLE_NOTES, null, values);

            // Insert the images with the note_id
            for (String imageUri : n.getUris()) {
                ContentValues imageValues = new ContentValues();
                imageValues.put(KEY_NOTE_ID, noteId);
                imageValues.put(KEY_IMAGE_URI, imageUri);
                db.insert(TABLE_NOTE_IMAGES, null, imageValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return noteId;
    }


    // Update a note
    public int updateNote(note n) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            // Update the note
            ContentValues values = new ContentValues();
            values.put(KEY_TIME, n.getSaveDate());
            values.put(KEY_TITLE, n.getTitle());
            values.put(KEY_CONTENT, n.getContent());

            // Updating the row in the notes table
            int rowsAffected = db.update(TABLE_NOTES, values, KEY_ID + " = ?", new String[]{String.valueOf(n.getId())});

            // Delete existing image URIs for the note
            db.delete(TABLE_NOTE_IMAGES, KEY_NOTE_ID + " = ?", new String[]{String.valueOf(n.getId())});

            // Insert the new image URIs
            for (String imageUri : n.getUris()) {
                ContentValues imageValues = new ContentValues();
                imageValues.put(KEY_NOTE_ID, n.getId());
                imageValues.put(KEY_IMAGE_URI, imageUri);
                db.insert(TABLE_NOTE_IMAGES, null, imageValues);
            }

            db.setTransactionSuccessful();
            return rowsAffected;
        } finally {
            db.endTransaction();
        }
    }


    // Delete a note and its images
    public void deleteNote(long noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            // Delete associated images first
            db.delete(TABLE_NOTE_IMAGES, KEY_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});

            // Delete the note
            db.delete(TABLE_NOTES, KEY_ID + " = ?", new String[]{String.valueOf(noteId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @SuppressLint("Range")
    private List<String> getImagesForNote(long noteId) {
        List<String> imageUris = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTE_IMAGES + " WHERE " + KEY_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});

        try {
            if (cursor.moveToFirst()) {
                do {
                    String imageUri = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URI));
                    imageUris.add(imageUri);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return imageUris;
    }


    @SuppressLint("Range")
    public List<note> getAllNotes() {
        List<note> notes = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    long noteId = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                    String time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
                    String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                    String content = cursor.getString(cursor.getColumnIndex(KEY_CONTENT));

                    // Get associated images for the note
                    List<String> imageUris = getImagesForNote(noteId);

                    // Create Note object
                    note n = new note(noteId, title, content, time, imageUris);

                    // Add the note to the list
                    notes.add(n);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return notes;
    }

    @SuppressLint("Range")
    public note getNoteWithImages(long noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        note note = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " WHERE " + KEY_ID + " = ?", new String[]{String.valueOf(noteId)});

        try {
            if (cursor.moveToFirst()) {
                String time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
                String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                String content = cursor.getString(cursor.getColumnIndex(KEY_CONTENT));

                // Get associated images for the note
                List<String> imageUris = getImagesForNote(noteId);

                // Create Note object
                note = new note(noteId, time, title, content, imageUris);
            }
        } finally {
            cursor.close();
        }

        return note;
    }

}

