package com.oneclique.logio.LogIOSQLite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.oneclique.logio.LogIOSQLite.SQLITE_VARIABLES.DB_NAME;
import static com.oneclique.logio.LogIOSQLite.SQLITE_VARIABLES.DB_VERSION;

public class LogIOSQLite extends SQLiteOpenHelper {

    private static final String TAG = "LogIOSQLite";

    private SQLiteDatabase db;

    private Context context;

    private static String DB_PATH;

    public LogIOSQLite(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        DB_PATH = context.getDatabasePath(DB_NAME).getAbsolutePath();
        Log.i(TAG, "LogIOSQLite: " + DB_PATH);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            Log.v("Database Upgrade", "Database version higher than old.");
            deleteDatabase();
        }
    }

    //region Internal SQLiteHelper
    //Check database already exist or not
    private boolean checkDataBase()
    {
        boolean checkDB = false;
        try {
            String myPath = DB_PATH;
            File db_file = new File(myPath);
            checkDB = db_file.exists();
        } catch(SQLiteException e) {
            Log.i(TAG, "checkDataBase: " + e.getMessage());
        }
        return checkDB;
    }

    private void copyDataBase() throws IOException {
        InputStream mInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[2024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public void createDatabase() {

        boolean dbExist1 = checkDataBase();
        if(!dbExist1) {
            this.getReadableDatabase();
            try
            {
                this.close();
                copyDataBase();
            }
            catch (IOException e)
            {
                Log.i(TAG, "createDatabase: " + e.getMessage());
            }
        }
    }

    private void deleteDatabase() {
        File file = new File(DB_PATH);
        Log.i(TAG, file.getName() + ": " + file.delete());
    }
    //endregion

    /**
     * Execute the following sql commands: UPDATE, INSERT and DELETE
     * @param sql query for the following commands: UPDATE, INSERT and DELETE
     * @return returns the count of affected rows
     */
    @SuppressLint("Recycle")
    public Integer executeWriter(String sql){//insert, update, delete
        db = this.getWritableDatabase();
        return db.rawQuery(sql, null).getCount();
    }

    /**
     * Execute sql command SELECT
     * @param sql query for sql SELECT command
     * @return returns the cursor as sql reader
     */
    public Cursor executeReader(String sql){//search
        db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public int numberOfRows(String tableName){
        db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, tableName);
    }

}
