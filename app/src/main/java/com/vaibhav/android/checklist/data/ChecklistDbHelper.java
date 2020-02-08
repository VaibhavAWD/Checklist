package com.vaibhav.android.checklist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.vaibhav.android.checklist.data.ChecklistContract.ChecklistEntry;
import com.vaibhav.android.checklist.data.ChecklistContract.ItemEntry;

public class ChecklistDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "checklist.db";

    private static final int DATABASE_VERSION = 1;

    public ChecklistDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String SQL_CREATE_TABLE_CHECKLISTS = "CREATE TABLE " + ChecklistEntry.TABLE_NAME + " (" +
                ChecklistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ChecklistEntry.COLUMN_TITLE + " TEXT NOT NULL);";
        database.execSQL(SQL_CREATE_TABLE_CHECKLISTS);

        String SQL_CREATE_TABLE_ITEMS = "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemEntry.COLUMN_CHECKLIST_ID + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_ITEM + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_STATUS + " INTEGER DEFAULT 0);";
        database.execSQL(SQL_CREATE_TABLE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
