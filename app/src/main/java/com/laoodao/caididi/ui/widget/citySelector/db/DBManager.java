package com.laoodao.caididi.ui.widget.citySelector.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.laoodao.caididi.retrofit.main.City;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ezy.lite.util.LogUtil;

/**
 * author Bro0cL on 2016/1/26.
 */
public class DBManager {
    private static final String ASSETS_NAME = "china_cities.db";
    private static final String DB_NAME = "area4.db";
    private static final String TABLE_NAME = "city";
    private static final String N = "n";
    private static final String I = "i";
    private static final String D = "d";
    private static final String P = "p";//900227
    private static final String PINYIN = "py";
    private static final int BUFFER_SIZE = 1024;
    private String DB_PATH;
    private Context mContext;


    public DBManager(Context context) {
        this.mContext = context;
        DB_PATH = File.separator + "data"
                + Environment.getDataDirectory().getAbsolutePath() + File.separator
                + context.getPackageName() + File.separator + "databases" + File.separator;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void copyDBFile() {
        File dir = new File(DB_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dbFile = new File(DB_PATH + DB_NAME);
        if (!dbFile.exists()) {
            InputStream is;
            OutputStream os;
            try {
                is = mContext.getResources().getAssets().open(ASSETS_NAME);
                os = new FileOutputStream(dbFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                while ((length = is.read(buffer, 0, buffer.length)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<City> getAllArea(String level) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where d='" + level + "'", null);
        List<City> result = fillData(db, cursor);
        Collections.sort(result, new CityComparator());
        return result;
    }

    public List<City> searchCity(String level, String keyword) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        String sql = "select * from " + TABLE_NAME + " where d='" + level + "' and n like '%" + keyword + "%' or d='" + level + "' and py like '%" + keyword + "%'";
        Cursor cursor1 = db.rawQuery(sql, null);
        List<City> result = fillData(db, cursor1);
        Collections.sort(result, new CityComparator());
        return result;
    }


    public List<City> search(final String sql) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery(sql, null);
        List<City> result = fillData(db, cursor);
        Collections.sort(result, new CityComparator());
        return result;
    }

    public List<City> searchByI(final String i) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from city where i='" + i + "'", null);
        return fillData(db, cursor);
    }

    public List<City> searchByP(final String p) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from city where p='" + p + "'", null);
        return fillData(db, cursor);
    }

    public List<City> searchByLevel(final String d) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from city where d='" + d + "'", null);
        return fillData(db, cursor);
    }


    private List<City> fillData(SQLiteDatabase db, Cursor cursor) {
        List<City> result = new ArrayList<>();
        City city;
        while (cursor.moveToNext()) {
            String n = cursor.getString(cursor.getColumnIndex(N));
            String i = cursor.getString(cursor.getColumnIndex(I));
            String d = cursor.getString(cursor.getColumnIndex(D));
            String p = cursor.getString(cursor.getColumnIndex(P));
            String pinyin = cursor.getString(cursor.getColumnIndex(PINYIN));
            city = new City(d, i, n, p, pinyin);
            result.add(city);
        }
        cursor.close();
        db.close();

        return result;
    }


    /**
     * sort by a-z
     */
    private class CityComparator implements Comparator<City> {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.py.substring(0, 1);
            String b = rhs.py.substring(0, 1);
            return a.compareTo(b);
        }
    }
}
