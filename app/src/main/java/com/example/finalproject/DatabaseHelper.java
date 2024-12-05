package com.example.finalproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PotholeDB.db";
    private static final int DATABASE_VERSION = 2; // Updated version
    public static final String TABLE_NAME = "Potholes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_EMAIL = "email"; // New column for email
    public static final String COLUMN_SIZE = "size"; // New column for size

    private final Context context;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LONGITUDE + " REAL, "
                + COLUMN_LATITUDE + " REAL, "
                + COLUMN_EMAIL + " TEXT, " // Add email column
                + COLUMN_SIZE + " TEXT)"; // Add size column
        db.execSQL(CREATE_TABLE);
        runSqlSeed(db, "seed.sql");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db); // Tạo lại bảng và chạy seed
        }
    }
    // Thực thi file seed SQL từ thư mục assets
    private void runSqlSeed(SQLiteDatabase db, String fileName) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sqlScript = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sqlScript.append(line).append("\n");
            }
            reader.close();

            // Thực thi từng lệnh SQL trong file seed
            String[] sqlStatements = sqlScript.toString().split(";");
            for (String statement : sqlStatements) {
                if (!statement.trim().isEmpty()) {
                    db.execSQL(statement.trim());
                }
            }
        } catch (IOException e) {
            Log.e("DatabaseHelper", "Failed to execute seed file: " + fileName, e);
        }
    }
    public int[] getPotholeCounts() {
        SQLiteDatabase db = this.getReadableDatabase();

        int smallCount = 0, mediumCount = 0, bigCount = 0;

        // Truy vấn đếm số lượng theo từng loại kích thước
        Cursor cursor = db.rawQuery(
                "SELECT size, COUNT(*) as count FROM " + TABLE_NAME + " GROUP BY size", null);

        if (cursor != null) {
            Log.d("DatabaseHelper", "Cursor contains " + cursor.getCount() + " rows."); // Debug log
            if (cursor.moveToFirst()) {
                do {
                    int sizeIndex = cursor.getColumnIndex("size");
                    int countIndex = cursor.getColumnIndex("count");

                    // Kiểm tra cột có tồn tại không
                    if (sizeIndex == -1 || countIndex == -1) {
                        Log.e("DatabaseHelper", "Column not found in the result set.");
                        break;
                    }

                    String size = cursor.getString(sizeIndex);
                    int count = cursor.getInt(countIndex);

                    if (size != null) {
                        switch (size.toLowerCase()) {
                            case "small":
                                smallCount = count;
                                break;
                            case "medium":
                                mediumCount = count;
                                break;
                            case "big":
                                bigCount = count;
                                break;
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            Log.e("DatabaseHelper", "Cursor is null. Query may have failed.");
        }

        db.close();
        return new int[]{smallCount, mediumCount, bigCount};
    }

    // Phương thức lấy tất cả dữ liệu từ database
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }
}
