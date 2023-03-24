package com.svcp.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoListDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "TodoList.db";
    private static final int DB_VERSION = 1;

    private static final String CREATE_TABLE =
            "CREATE TABLE " + MainActivity.TABLE_NAME + " (" +
                    MainActivity.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MainActivity.COL_ITEM + " TEXT, " +
                    MainActivity.COL_DATE + " INTEGER);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + MainActivity.TABLE_NAME;

    public TodoListDbHelper(Context context, String dbName) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
