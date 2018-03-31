package com.ghj.translation.mytranslation.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ghj.translation.mytranslation.adapter.GlossaryItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseOperation {
    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper databaseHelper;

    public DatabaseOperation(Context context) {
        //this.db = db;
        this.context = context;
        databaseHelper = new DatabaseHelper(context, "NoteStore.db", null, 2);

    }

    public void open() throws SQLException {
        db = databaseHelper.getWritableDatabase();
    }

    /*笔记的数据库操作*/
    /*笔记的插入*/
    public void insert_db(String firstText, String endText) {
        this.open();
        db.execSQL("insert into notes(firstText,endText) values('" + firstText + "','" + endText + "');");
        this.close_db();
    }

    /*笔记的更新*/
    public void update_db(String title, String text, String time, int item_ID) {
        this.open();
        db.execSQL("update notes set content='" + text + "',title='" + title + "',time='" + time + "'where id='" + item_ID + "'");
        this.close_db();
    }


    public ArrayList<GlossaryItem> getWordList() {
        this.open();
        ArrayList<GlossaryItem> items = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from notes", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String firstText = cursor.getString(cursor.getColumnIndex("firstText"));
                String endText = cursor.getString(cursor.getColumnIndex("endText"));
                GlossaryItem glossaryItem = new GlossaryItem(id, firstText, endText);
                items.add(glossaryItem);
                cursor.moveToNext();
            }
        }
        this.close_db();
        return items;
    }

    public void delete_db(String world) {
        this.open();
        db.execSQL("delete from notes where firstText='" + world + "'");
        this.close_db();
    }

    //关闭数据库
    public void close_db() {
        db.close();
    }
}  