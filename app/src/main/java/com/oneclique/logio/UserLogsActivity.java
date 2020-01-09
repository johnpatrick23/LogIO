package com.oneclique.logio;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.oneclique.logio.LogIOSQLite.LogIOSQLite;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UserAchievementsModel;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UserLogsModel;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UsersModel;
import com.oneclique.logio.LogIOSQLite.SQLITE_VARIABLES;
import com.oneclique.logio.LogIOSQLite.SQLITE_VARIABLES.*;
import com.oneclique.logio.model.PlayerInGameModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserLogsActivity extends AppCompatActivityHelper {

    private PlayerInGameModel playerInGameModel;
    private List<UserLogsModel> userLogsModelList;
    private UsersModel usersModel;

    private LogIOSQLite logIOSQLite;

    private TextView mTextViewLogsDropoutRate;
    private TextView mTextViewLogsMessageDisplayTime;
    private TextView mTextViewLogsNumberOfTry;
    private TextView mTextViewLogsBounceRate;
    private TextView mTextViewLogsReturnVisits;

    private UserAchievementsModel userAchievementsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_user_logs);

        usersModel = new UsersModel();
        userLogsModelList = new ArrayList<>();
        userAchievementsModel = new UserAchievementsModel();

        mTextViewLogsDropoutRate = findViewById(R.id.mTextViewLogsDropoutRate);
        mTextViewLogsMessageDisplayTime = findViewById(R.id.mTextViewLogsMessageDisplayTime);
        mTextViewLogsNumberOfTry = findViewById(R.id.mTextViewLogsNumberOfTry);
        mTextViewLogsBounceRate = findViewById(R.id.mTextViewLogsBounceRate);
        mTextViewLogsReturnVisits = findViewById(R.id.mTextViewLogsReturnVisits);

        logIOSQLite = new LogIOSQLite(UserLogsActivity.this);
        logIOSQLite.createDatabase();

        Intent intent = getIntent();
        playerInGameModel = (PlayerInGameModel) Objects.requireNonNull(intent.getExtras()).getSerializable(PLAYER_IN_GAME_MODEL);
        if (playerInGameModel != null) {
            Log.i(TAG, "playerInGameModel.getUsername: " + playerInGameModel.getUsername());
        }
        try {
            if (playerInGameModel != null) {
                Cursor cursor = logIOSQLite.executeReader("SELECT * FROM " + Table_Users.DB_TABLE_NAME + " " +
                        "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                Log.i(TAG, "logIOSQLite.executeReader: " + cursor.getCount());
                if(cursor.getCount() != 0){
                    while (cursor.moveToNext()){
                        usersModel.setA_id(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_ID)));
                        usersModel.setA_username(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_USERNAME)));
                        usersModel.setA_last_used(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_LAST_USED)));
                        usersModel.setA_level_1_stars(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_LEVEL_1_STARS)));
                        usersModel.setA_level_2_stars(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_LEVEL_2_STARS)));
                        usersModel.setA_level_3_stars(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_LEVEL_3_STARS)));
                        usersModel.setA_level_4_stars(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_LEVEL_4_STARS)));
                        usersModel.setA_level_5_stars(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_LEVEL_5_STARS)));
                        usersModel.setA_level_6_stars(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_LEVEL_6_STARS)));
                        usersModel.setA_level_7_stars(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_LEVEL_7_STARS)));
                        usersModel.setA_number_of_access(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_NUMBER_OF_ACCESS)));
                        usersModel.setA_hint(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_HINT)));
                        usersModel.setA_add_time(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_ADD_TIME)));
                        usersModel.setA_slow_time(cursor.getString(cursor.getColumnIndex(Table_Users.DB_COL_SLOW_TIME)));
                    }
                }
            }
            UsersModelLog(usersModel);
        }catch (SQLiteException ex){
            Log.i(TAG, "logIOSQLite.executeReader: " + ex.getMessage());
        }

        try {
            Cursor cursor = logIOSQLite.executeReader(
                    "SELECT * FROM " + Table_User_Logs.DB_TABLE_NAME + " " +
                            "WHERE " + Table_User_Logs.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");

            Log.i(TAG, "logIOSQLite.executeReader: " + cursor.getCount());
            if(cursor.getCount() != 0){
                Log.i(TAG, "UserLogsModelLog(userLogsModel):");
                while (cursor.moveToNext()){
                    UserLogsModel userLogsModel = new UserLogsModel();

                    userLogsModel.setA_id(cursor.getString(cursor.getColumnIndex(Table_User_Logs.DB_COL_ID)));
                    userLogsModel.setA_username(cursor.getString(cursor.getColumnIndex(Table_User_Logs.DB_COL_USERNAME)));
                    userLogsModel.setA_level(cursor.getString(cursor.getColumnIndex(Table_User_Logs.DB_COL_LEVEL)));
                    userLogsModel.setA_popup_message_time(cursor.getString(cursor.getColumnIndex(Table_User_Logs.DB_COL_POPUP_MESSAGE_TIME)));
                    userLogsModel.setA_star(cursor.getString(cursor.getColumnIndex(Table_User_Logs.DB_COL_STAR)));
                    userLogsModel.setA_average_time(cursor.getString(cursor.getColumnIndex(Table_User_Logs.DB_COL_AVERAGE_TIME)));

                    UserLogsModelLog(userLogsModel);

                    userLogsModelList.add(userLogsModel);
                }
            }
        }catch (SQLiteException ex){
            Log.i(TAG, "logIOSQLite.executeReader: " + ex.getMessage());
        }

        try {
            Cursor cursor = logIOSQLite.executeReader(
                    "SELECT * FROM " + Table_User_Achievements.DB_TABLE_NAME + " " +
                            "WHERE " + Table_User_Achievements.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");

            Log.i(TAG, "logIOSQLite.executeReader: " + cursor.getCount());

            if(cursor.getCount() != 0){
                Log.i(TAG, "UserLogsModelLog(userLogsModel):");
                while (cursor.moveToNext()){

                    userAchievementsModel.setA_id(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_ID)));
                    userAchievementsModel.setA_level(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_LEVEL)));
                    userAchievementsModel.setA_stars(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_STARS)));
                    userAchievementsModel.setA_time_finished(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_TIME_FINISHED)));
                    userAchievementsModel.setA_description(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_DESCRIPTION)));
                    userAchievementsModel.setA_number_of_tries(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_NUMBER_OF_TRIES)));
                    userAchievementsModel.setA_username(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_USERNAME)));

                    UserAchievementsModelLog(userAchievementsModel);
                }
            }
        }catch (SQLiteException ex){
            Log.i(TAG, "logIOSQLite.executeReader: " + ex.getMessage());
        }


        try {
            int messageDisplayTime = 0;

            for (UserLogsModel userLogsModel: userLogsModelList) {
                messageDisplayTime += Integer.parseInt(userLogsModel.getA_popup_message_time());
            }
            float numberOfTries = (Integer.parseInt(userAchievementsModel.getA_number_of_tries()) * 100);
            float numberOfAccess = (Integer.parseInt(usersModel.getA_number_of_access()) * 100);
            float dropOutRate =  numberOfTries / numberOfAccess;

            Log.i(TAG, "numberOfTries: " + numberOfTries);
            Log.i(TAG, "numberOfAccess: " + numberOfAccess);
            Log.i(TAG, "dropOutRate: " + dropOutRate);
            Log.i(TAG, "(numberOfAccess == numberOfTries ? 0 : 100): " + (numberOfAccess == numberOfTries ? 0 : 100));
            dropOutRate *= (numberOfAccess == numberOfTries ? 0 : 100);

            Log.i(TAG, "dropOutRate: " + dropOutRate);

            String dropOutRateString = String.format("%.02f", dropOutRate) + "%";

            mTextViewLogsDropoutRate.setText(dropOutRateString);
            mTextViewLogsMessageDisplayTime.setText(String.valueOf(messageDisplayTime));
            mTextViewLogsNumberOfTry.setText(userAchievementsModel.getA_number_of_tries());
            mTextViewLogsBounceRate.setText(usersModel.getA_number_of_access());
            mTextViewLogsReturnVisits.setText(usersModel.getA_number_of_access());
        }catch (Exception e){
            Toast.makeText(UserLogsActivity.this, "Nothing to display!", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_PLAYER_IN_GAME_MODEL){
            if(resultCode == Activity.RESULT_OK){
                playerInGameModel = (PlayerInGameModel) Objects.requireNonNull(
                        Objects.requireNonNull(data).getExtras()).getSerializable(PLAYER_IN_GAME_MODEL);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(PLAYER_IN_GAME_MODEL, playerInGameModel);
        Log.i(TAG, "onBackPressed: UserLogsActivity");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
