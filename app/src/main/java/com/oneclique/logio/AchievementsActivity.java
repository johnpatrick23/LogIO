package com.oneclique.logio;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.oneclique.logio.LogIOSQLite.LogIOSQLite;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UserAchievementsModel;
import com.oneclique.logio.LogIOSQLite.SQLITE_VARIABLES.*;
import com.oneclique.logio.adapter.UserAchievementsListViewAdapter;
import com.oneclique.logio.model.PlayerInGameModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AchievementsActivity extends AppCompatActivityHelper {

    private ListView mListViewUserAchievements;

    private UserAchievementsListViewAdapter userAchievementsListViewAdapter;

    private List<UserAchievementsModel> userAchievementsModelList;

    private PlayerInGameModel playerInGameModel;

    private LogIOSQLite logIOSQLite;

    private TextView mTextViewUserAchievementsNoDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_achievements);

        logIOSQLite = new LogIOSQLite(AchievementsActivity.this);
        logIOSQLite.createDatabase();

        mListViewUserAchievements = findViewById(R.id.mListViewUserAchievements);

        mTextViewUserAchievementsNoDisplay = findViewById(R.id.mTextViewUserAchievementsNoDisplay);

        userAchievementsModelList = new ArrayList<>();

        Intent intent = getIntent();
        playerInGameModel = (PlayerInGameModel) Objects.requireNonNull(intent.getExtras()).getSerializable(PLAYER_IN_GAME_MODEL);


        Cursor cursor = logIOSQLite.executeReader(("SELECT * FROM " + Table_User_Achievements.DB_TABLE_NAME + " " +
                "WHERE " + Table_User_Achievements.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';"));
        Log.i(TAG, "playerInGameModel.getUsername(): " + playerInGameModel.getUsername());
        Log.i(TAG, "cursor.getCount(): " + cursor.getCount());

        if(cursor.getCount() != 0){

            mTextViewUserAchievementsNoDisplay.setVisibility(View.GONE);
            while (cursor.moveToNext()){
                UserAchievementsModel userAchievementsModel = new UserAchievementsModel();
                userAchievementsModel.setA_id(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_ID)));
                userAchievementsModel.setA_time_finished(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_TIME_FINISHED)));
                userAchievementsModel.setA_username(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_USERNAME)));
                userAchievementsModel.setA_stars(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_STARS)));
                userAchievementsModel.setA_level(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_LEVEL)));
                userAchievementsModel.setA_number_of_tries(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_NUMBER_OF_TRIES)));
                userAchievementsModel.setA_description(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_DESCRIPTION)));
                userAchievementsModelList.add(userAchievementsModel);
            }

            userAchievementsListViewAdapter = new UserAchievementsListViewAdapter((AchievementsActivity.this), userAchievementsModelList);

            mListViewUserAchievements.setAdapter(userAchievementsListViewAdapter);
        }else{
            mTextViewUserAchievementsNoDisplay.setVisibility(View.VISIBLE);
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
        Log.i(TAG, "onBackPressed: AchievementsActivity");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
