package com.svcp.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String DB_NAME = "TodoList.db";
    public static final String TABLE_NAME = "TodoItems";
    public static final String COL_ID = "id";
    public static final String COL_ITEM = "item";
    public static final String COL_DATE = "date";

    private SQLiteDatabase db;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> todoItems;

    private EditText editText;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);
        ListView listView = findViewById(R.id.list_view);

        TodoListDbHelper dbHelper = new TodoListDbHelper(this, DB_NAME);
        db = dbHelper.getWritableDatabase();

        todoItems = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_ID, COL_ITEM, COL_DATE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            todoItems.add(cursor.getString(cursor.getColumnIndex(COL_ITEM)));
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.item_text_view, todoItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Edit or delete item");
                builder.setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Edit item
                            String oldItem = todoItems.get(position);
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Edit item");
                            final EditText input = new EditText(MainActivity.this);
                            input.setText(oldItem);
                            builder.setView(input);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String newItem = input.getText().toString();
                                    if (!newItem.isEmpty() && !newItem.equals(oldItem)) {
                                        ContentValues values = new ContentValues();
                                        values.put(COL_ITEM, newItem);
                                        db.update(TABLE_NAME, values, COL_ITEM + "=?", new String[]{oldItem});
                                        todoItems.set(position, newItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancel", null);
                            builder.show();
                        } else if (which == 1) {
                            // Delete item
                            String item = todoItems.get(position);
                            db.delete(TABLE_NAME, COL_ITEM + "=?", new String[]{item});
                            todoItems.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    public void onAddButtonClick(View view) {
        String item = editText.getText().toString();
        if (item.isEmpty()) {
            Toast.makeText(this, "Please enter an item", Toast.LENGTH_SHORT).show();
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(COL_ITEM, item);
        values.put(COL_DATE, currentTimeMillis);
        db.insert(TABLE_NAME, null, values);
        String itemWithDate = item + " (" + DateFormat.getDateInstance().format(new Date(currentTimeMillis)) + ")";
        todoItems.add(itemWithDate);
        adapter.notifyDataSetChanged();
        editText.setText("");
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
