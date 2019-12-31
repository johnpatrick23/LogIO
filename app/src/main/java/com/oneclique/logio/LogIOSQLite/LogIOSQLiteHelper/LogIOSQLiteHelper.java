package com.oneclique.logio.LogIOSQLite.LogIOSQLiteHelper;

import android.content.Context;
import android.database.Cursor;

import com.oneclique.logio.LogIOSQLite.LogIOSQLite;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UsersModel;
import com.oneclique.logio.LogIOSQLite.SQLITE_VARIABLES;

import java.util.ArrayList;
import java.util.List;

public class LogIOSQLiteHelper {

    private LogIOSQLite logIOSQLite = null;

    public LogIOSQLiteHelper(Context context){
       logIOSQLite = new LogIOSQLite(context);
       logIOSQLite.createDatabase();
    }

    public List<UsersModel> GetUserNames(String selectQuery){
        List<UsersModel> usersModelList = new ArrayList<>();
        Cursor cursor = logIOSQLite.executeReader(selectQuery);
        while (cursor.moveToNext()){
            UsersModel usersModel = new UsersModel();
            usersModel.setA_id(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Users.DB_COL_ID)));
            usersModel.setA_username(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME)));
            usersModel.setA_last_used(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Users.DB_COL_LAST_USED)));
            usersModel.setA_level_1_stars(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_1_STARS)));
            usersModel.setA_level_2_stars(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_2_STARS)));
            usersModel.setA_level_3_stars(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_3_STARS)));
            usersModel.setA_level_4_stars(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_4_STARS)));
            usersModel.setA_level_5_stars(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_5_STARS)));
            usersModel.setA_level_6_stars(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_6_STARS)));
            usersModel.setA_level_7_stars(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_7_STARS)));
            usersModelList.add(usersModel);
        }
        return usersModelList;
    }

}
