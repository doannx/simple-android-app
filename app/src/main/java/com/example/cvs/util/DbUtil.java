package com.example.cvs.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by john on 11/6/16.
 */

public class DbUtil extends SQLiteOpenHelper {
    private static final String TAG = "DbUtil";
    private Properties prop;
    private SQLiteDatabase db;

    public static final String DATABASE_NAME = "cvs";
    private static final String SQL_DIR = "sql";
    private static final String CREATE_DB_SCRIPT = "create.sql";
    private static final String UPGRADEFILE_PREFIX = "upgrade-";
    private static final String UPGRADEFILE_SUFFIX = ".sql";

    private Context context;

    public DbUtil(Context context, int dbVersion) {
        super(context, DATABASE_NAME, null, dbVersion);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            execSqlFile(CREATE_DB_SCRIPT, sqLiteDatabase);
        } catch (IOException exception) {
            throw new RuntimeException("Database creation failed", exception);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try {
            Log.i(TAG, String.format("Upgrading db from %d to %d", oldVersion, newVersion));
            for (String sqlFile : AssetUtils.list(SQL_DIR, this.context.getAssets())) {
                if (sqlFile.startsWith(UPGRADEFILE_PREFIX)) {
                    int fileVersion = Integer.parseInt(sqlFile.substring(UPGRADEFILE_PREFIX.length(), sqlFile.length() - UPGRADEFILE_SUFFIX.length()));
                    if (fileVersion > oldVersion && fileVersion <= newVersion) {
                        execSqlFile(sqlFile, db);
                    }
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException("Database upgrade failed", exception);
        }
    }

    protected void execSqlFile(String sqlFile, SQLiteDatabase db) throws SQLException, IOException {
        Log.i(TAG, String.format("Executing sql script: %s", sqlFile));
        for (String sqlInstruction : SqlParser.parseSqlFile(SQL_DIR + "/" + sqlFile, this.context.getAssets())) {
            db.execSQL(sqlInstruction);
        }
    }

    /**
     * open database
     */
    private void open() {
        try {
            db = getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * close database
     */
    public void close() {
        if (db != null && db.isOpen()) {
            try {
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get all row of table with sql command then return cursor
     * cursor move to frist to redy for get data
     */
    public Cursor getAll(String sql) {
        open();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        close();
        return cursor;
    }

    /**
     * insert contentvaluse to table
     *
     * @param values value of data want insert
     * @return index row insert
     */
    public long insert(String table, ContentValues values) {
        open();
        long index = db.insert(table, null, values);
        close();
        Log.i(TAG, "Inserted into " + table);
        return index;
    }

    /**
     * update values to table
     *
     * @return index row update
     */
    public boolean update(String table, ContentValues values, String where) {
        open();
        long index = db.update(table, values, where, null);
        close();
        return index > 0;
    }

    /**
     * delete id row of table
     */
    public boolean delete(String table, String where) {
        open();
        long index = db.delete(table, where, null);
        close();
        return index > 0;
    }
}
