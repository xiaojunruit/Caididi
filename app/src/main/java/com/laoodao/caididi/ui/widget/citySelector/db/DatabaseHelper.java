package com.laoodao.caididi.ui.widget.citySelector.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.laoodao.caididi.retrofit.main.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by next on 2016/3/24.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String name = "history_city";
    private static final int version = 3;

    public DatabaseHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS HISTORY_CITY (id integer primary key autoincrement, n varchar(40),i varchar(40),loc varchar(50) ,date INTEGER)");
        db.execSQL("create table if not exists history_city (id integer primary key autoincrement, d varchar(5),i  varchar(40),n varchar(40),p varchar(40) ,py varchar(80),date integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS HISTORY_CITY");

        onCreate(db);
    }


    public List<City> getHistoryCity(String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM HISTORY_CITY  WHERE D='" + type + "' ORDER BY DATE DESC  LIMIT 0,9", null);
        List<City> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            String n = cursor.getString(cursor.getColumnIndex("n"));
            String i = cursor.getString(cursor.getColumnIndex("i"));
            String d = cursor.getString(cursor.getColumnIndex("d"));
            String p = cursor.getString(cursor.getColumnIndex("p"));
            String pinyin = cursor.getString(cursor.getColumnIndex("py"));

            result.add(new City(d, i, n, p, pinyin));
        }
        return result;
    }

    //添加到最近城市
    public void addToHistoryCity(City city) {
        if (city == null) return;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM HISTORY_CITY WHERE N = '" + city.n + "'", null);
        if (cursor.getCount() > 0) {
            db.delete("HISTORY_CITY", "N = ?", new String[]{city.n});
        }
        db.execSQL("INSERT INTO HISTORY_CITY(D, I, N , P , PY ,DATE)  VALUES('" + city.d + "','" + city.i + "','" + city.n + "', '" + city.p + "','" + city.py + "',  " + System.currentTimeMillis() + ")");
        db.close();
    }
}
