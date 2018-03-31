package com.ghj.translation.mytranslation.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by YBC on 2017/12/19 14:52.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_BOOK =
            "create table if not exists notes("
                    + "id integer primary key autoincrement,"
                    + "firstText text,"
                    + "endText text)";

    private Context mContext;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_BOOK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists notes");
        onCreate(sqLiteDatabase);
    }
}
