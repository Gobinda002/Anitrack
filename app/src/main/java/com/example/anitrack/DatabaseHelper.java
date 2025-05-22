package com.example.anitrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "anime_db";
    private static final int DATABASE_VERSION = 1;

    // Tables
    private static final String TABLE_ONGOING = "ongoing_anime_table";
    private static final String TABLE_COMPLETED = "completed_anime_table";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "anime_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createOngoingTable = "CREATE TABLE " + TABLE_ONGOING + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT UNIQUE)";
        db.execSQL(createOngoingTable);

        String createCompletedTable = "CREATE TABLE " + TABLE_COMPLETED + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT UNIQUE)";
        db.execSQL(createCompletedTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ONGOING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPLETED);
        onCreate(db);
    }

    // ---- Ongoing Anime Methods ----

    public boolean addOngoingAnime(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        long result = db.insert(TABLE_ONGOING, null, values);
        return result != -1;
    }

    public boolean ongoingAnimeExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM " + TABLE_ONGOING + " WHERE " + COLUMN_NAME + " = ? LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{name});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public int getOngoingAnimeCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_ONGOING;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public String getOngoingAnimeAt(int index) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_ONGOING + " LIMIT 1 OFFSET " + index;
        Cursor cursor = db.rawQuery(query, null);
        String name = null;
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    public boolean deleteOngoingAnime(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ONGOING, COLUMN_NAME + " = ?", new String[]{name});
        return rows > 0;
    }

    public boolean updateOngoingAnime(String oldName, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);
        int rows = db.update(TABLE_ONGOING, values, COLUMN_NAME + " = ?", new String[]{oldName});
        return rows > 0;
    }

    // ---- Completed Anime Methods ----

    public boolean addCompletedAnime(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        long result = db.insert(TABLE_COMPLETED, null, values);
        return result != -1;
    }

    public boolean completedAnimeExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM " + TABLE_COMPLETED + " WHERE " + COLUMN_NAME + " = ? LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{name});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public ArrayList<String> getAllOngoingAnime() {
        ArrayList<String> animeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_ONGOING;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                animeList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return animeList;
    }

    public ArrayList<String> getAllCompletedAnime() {
        ArrayList<String> animeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_COMPLETED;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                animeList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return animeList;
    }

    // ✅ ADD THESE METHODS FOR CompletedFragment SUPPORT

    public boolean updateCompletedAnime(String oldName, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);
        int rows = db.update(TABLE_COMPLETED, values, COLUMN_NAME + " = ?", new String[]{oldName});
        return rows > 0;
    }

    public boolean deleteCompletedAnime(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_COMPLETED, COLUMN_NAME + " = ?", new String[]{name});
        return rows > 0;
    }
}
