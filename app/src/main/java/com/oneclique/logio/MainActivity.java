package com.oneclique.logio;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oneclique.logio.LogIOSQLite.LogIOSQLite;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteHelper.LogIOSQLiteHelper;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.QuestionModel;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UsersModel;
import com.oneclique.logio.LogIOSQLite.SQLITE_VARIABLES;
import com.oneclique.logio.adapter.UsernameListViewAdapter;
import com.oneclique.logio.model.PlayerInGameModel;

import org.sufficientlysecure.htmltextview.HtmlFormatter;
import org.sufficientlysecure.htmltextview.HtmlFormatterBuilder;
import org.sufficientlysecure.htmltextview.HtmlResImageGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivityHelper {

    private Button mImageButtonStart;
    private Button mImageButtonAchievements;
    private Button mImageButtonExit;

    private ImageButton mImageButtonLeaderboard;
    private ImageButton mImageButtonSettings;
    private ImageButton mImageButtonUserIcon;

    private PlayerInGameModel playerInGameModel;

    private TextView mTextViewUsername;

    private LogIOSQLite logIOSQLite;

    private int selectedPlayerPosition = 0;
    private String selectedPlayer = "";

    private UsersModel usersModel;
    private LogIOSQLiteHelper logIOSQLiteHelper;
    private List<List<QuestionModel>> listsOfQuestionsPerLevel;
    private List<QuestionModel> questionModelList;
    private AllLevels allLevels;
    private List<Integer> stars;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        requestPermission(MainActivity.this);
        setContentView(R.layout.activity_main);

        allLevels = new AllLevels(MainActivity.this);
        playerInGameModel = new PlayerInGameModel();
        mImageButtonStart = findViewById(R.id.mImageButtonStart);
        mImageButtonUserIcon = findViewById(R.id.mImageButtonUserIcon);
        mImageButtonAchievements = findViewById(R.id.mImageButtonAchievements);
        mImageButtonExit = findViewById(R.id.mImageButtonExit);
        mImageButtonSettings = findViewById(R.id.mImageButtonSettings);
        mImageButtonLeaderboard = findViewById(R.id.mImageButtonLeaderboard);

        mTextViewUsername = findViewById(R.id.mTextViewUsername);
        mTextViewUsername.setText("");
        listsOfQuestionsPerLevel = new ArrayList<>();
        logIOSQLiteHelper = new LogIOSQLiteHelper(MainActivity.this);
        questionModelList = new ArrayList<>();
        usersModel = new UsersModel();

        logIOSQLite = new LogIOSQLite(MainActivity.this);
        logIOSQLite.createDatabase();

        mImageButtonStart.setEnabled(!mTextViewUsername.getText().toString().isEmpty());


        //region mTextViewUsername.addTextChangedListener
        mTextViewUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mImageButtonStart.setEnabled(!mTextViewUsername.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //endregion

        //region Get last used user
        Cursor usernamesCursor = logIOSQLite.executeReader(
                "SELECT * FROM " + SQLITE_VARIABLES.Table_Users.DB_TABLE_NAME +
                        " where " + SQLITE_VARIABLES.Table_Users.DB_COL_LAST_USED + " = '1';");
        stars = new ArrayList<>();
        if(usernamesCursor.getCount() != 0){
            while(usernamesCursor.moveToNext()){
                usersModel.setA_id(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                        SQLITE_VARIABLES.Table_Users.DB_COL_ID)));
                usersModel.setA_username(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                        SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME)));
                usersModel.setA_last_used(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                        SQLITE_VARIABLES.Table_Users.DB_COL_LAST_USED)));
                usersModel.setA_level_1_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                        SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_1_STARS)));
                usersModel.setA_level_2_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                        SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_2_STARS)));
                usersModel.setA_level_3_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                        SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_3_STARS)));
                usersModel.setA_level_4_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                        SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_4_STARS)));
                usersModel.setA_level_5_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                        SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_5_STARS)));
                usersModel.setA_level_6_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                        SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_6_STARS)));
                usersModel.setA_level_7_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                        SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_7_STARS)));
            }
            UserModelLog(usersModel);
            selectedPlayer = usersModel.getA_username();
            mTextViewUsername.setText(usersModel.getA_username());
            stars.add(Integer.parseInt(usersModel.getA_level_1_stars()));
            stars.add(Integer.parseInt(usersModel.getA_level_2_stars()));
            stars.add(Integer.parseInt(usersModel.getA_level_3_stars()));
            stars.add(Integer.parseInt(usersModel.getA_level_4_stars()));
            stars.add(Integer.parseInt(usersModel.getA_level_5_stars()));
            stars.add(Integer.parseInt(usersModel.getA_level_6_stars()));
            stars.add(Integer.parseInt(usersModel.getA_level_7_stars()));
            //playerStatisticModel.setUsername(usersModel.getA_username());
            //playerStatisticModel.setCharacter(usersModel.getA_character());
            for(int i = 0; i < allLevels.mImageViewLevelsStars.length; i++){
                switch (stars.get(i)){
                    case 1:{
                        allLevels.mImageViewLevelsStars[i][0].setImageDrawable(getDrawable(R.drawable.ic_starleft));
                        allLevels.mImageViewLevelsStars[i][1].setImageDrawable(getDrawable(R.drawable.ic_starmiddlelocked));
                        allLevels.mImageViewLevelsStars[i][2].setImageDrawable(getDrawable(R.drawable.ic_starrightlocked));
                        break;
                    }
                    case 2:{
                        allLevels.mImageViewLevelsStars[i][0].setImageDrawable(getDrawable(R.drawable.ic_starleft));
                        allLevels.mImageViewLevelsStars[i][1].setImageDrawable(getDrawable(R.drawable.ic_starmiddle));
                        allLevels.mImageViewLevelsStars[i][2].setImageDrawable(getDrawable(R.drawable.ic_starrightlocked));
                        break;
                    }
                    case 3:{
                        allLevels.mImageViewLevelsStars[i][0].setImageDrawable(getDrawable(R.drawable.ic_starleft));
                        allLevels.mImageViewLevelsStars[i][1].setImageDrawable(getDrawable(R.drawable.ic_starmiddle));
                        allLevels.mImageViewLevelsStars[i][2].setImageDrawable(getDrawable(R.drawable.ic_starright));
                        break;
                    }
                    default: break;
                }
            }
        }
        //endregion

        //region Get All questions
        /*for(int i = 0; i < 7; i++){
            Cursor cursor = logIOSQLite.executeReader("select * from " + SQLITE_VARIABLES.Table_Questions.DB_TABLE_NAME + " " +
                    "where " + SQLITE_VARIABLES.Table_Questions.DB_COL_LEVEL + " = '" + (i + 1) + "';");
            questionModelList = new ArrayList<>();
            while(cursor.moveToNext()){
                QuestionModel questionsModel = new QuestionModel();
                questionsModel.setA_id(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_ID)));
                questionsModel.setA_level(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_LEVEL)));
                questionsModel.setA_questiontype(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_QUESTION_TYPE)));
                questionsModel.setA_question(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_QUESTION)));
                questionsModel.setA_choices(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_CHOICES)));
                questionsModel.setA_answer(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_ANSWER)));
                questionsModel.setA_timeduration(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_TIME_DURATION)));
                questionsModel.setA_category(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_CATEGORY)));
                questionsModel.setA_instruction(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_INSTRUCTION)));
                questionModelList.add(questionsModel);
            }
            Log.i(TAG, "onCreate: Question level " + (i + 1) + " done!");
            listsOfQuestionsPerLevel.add(questionModelList);
        }*/
        //endregion

        for (int i = 0; i < allLevels.mImageButtonLevels.length; i++){
            final int finalI = i;
            allLevels.mImageButtonLevels[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    /*QuestionListModel questionListModel = new QuestionListModel();
                    questionListModel.setQuestionListModelList(listsOfQuestionsPerLevel);
                    intent.putExtra(QUESTIONS, questionListModel);*/
                    playerInGameModel.setSelectedLevel(String.valueOf(finalI));
                    playerInGameModel.setUsername(selectedPlayer);
                    playerInGameModel.setUsersModel(usersModel);
                    Log.i(TAG, "playerInGameModel.getUsername(): " + playerInGameModel.getUsername());
                    Log.i(TAG, "playerInGameModel.getSelectedLevel(): " + playerInGameModel.getSelectedLevel());
                    intent.putExtra(PLAYER_IN_GAME_MODEL, playerInGameModel);
                    startActivityForResult(intent, REQUEST_PLAYER_IN_GAME_MODEL);
                }
            });
        }

        //region mImageButtonExit.setOnClickListener
        mImageButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //endregion

        //region mImageButtonStart.setOnClickListener
        mImageButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i = 0; i < allLevels.mImageViewLevelsStars.length; i++){
                    allLevels.mImageViewLevelsStars[i][0].setImageDrawable(getDrawable(R.drawable.ic_starleftlocked));
                    allLevels.mImageViewLevelsStars[i][1].setImageDrawable(getDrawable(R.drawable.ic_starmiddlelocked));
                    allLevels.mImageViewLevelsStars[i][2].setImageDrawable(getDrawable(R.drawable.ic_starrightlocked));
                }


                if(!mTextViewUsername.getText().toString().isEmpty()){

                    //region Get last used user
                    Cursor usernamesCursor = logIOSQLite.executeReader(
                            "SELECT * FROM " + SQLITE_VARIABLES.Table_Users.DB_TABLE_NAME +
                                    " where " + SQLITE_VARIABLES.Table_Users.DB_COL_LAST_USED + " = '1';");
                    stars = new ArrayList<>();
                    if(usernamesCursor.getCount() != 0){
                        while(usernamesCursor.moveToNext()){
                            usersModel.setA_id(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                    SQLITE_VARIABLES.Table_Users.DB_COL_ID)));
                            usersModel.setA_username(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                    SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME)));
                            usersModel.setA_last_used(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                    SQLITE_VARIABLES.Table_Users.DB_COL_LAST_USED)));
                            usersModel.setA_level_1_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                    SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_1_STARS)));
                            usersModel.setA_level_2_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                    SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_2_STARS)));
                            usersModel.setA_level_3_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                    SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_3_STARS)));
                            usersModel.setA_level_4_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                    SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_4_STARS)));
                            usersModel.setA_level_5_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                    SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_5_STARS)));
                            usersModel.setA_level_6_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                    SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_6_STARS)));
                            usersModel.setA_level_7_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                    SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_7_STARS)));
                        }
                        UserModelLog(usersModel);
                        selectedPlayer = usersModel.getA_username();
                        mTextViewUsername.setText(usersModel.getA_username());
                        stars.add(Integer.parseInt(usersModel.getA_level_1_stars()));
                        stars.add(Integer.parseInt(usersModel.getA_level_2_stars()));
                        stars.add(Integer.parseInt(usersModel.getA_level_3_stars()));
                        stars.add(Integer.parseInt(usersModel.getA_level_4_stars()));
                        stars.add(Integer.parseInt(usersModel.getA_level_5_stars()));
                        stars.add(Integer.parseInt(usersModel.getA_level_6_stars()));
                        stars.add(Integer.parseInt(usersModel.getA_level_7_stars()));
                        //playerStatisticModel.setUsername(usersModel.getA_username());
                        //playerStatisticModel.setCharacter(usersModel.getA_character());
                        for(int i = 0; i < allLevels.mImageViewLevelsStars.length; i++){
                            switch (stars.get(i)){
                                case 1:{
                                    allLevels.mImageViewLevelsStars[i][0].setImageDrawable(getDrawable(R.drawable.ic_starleft));
                                    allLevels.mImageViewLevelsStars[i][1].setImageDrawable(getDrawable(R.drawable.ic_starmiddlelocked));
                                    allLevels.mImageViewLevelsStars[i][2].setImageDrawable(getDrawable(R.drawable.ic_starrightlocked));
                                    break;
                                }
                                case 2:{
                                    allLevels.mImageViewLevelsStars[i][0].setImageDrawable(getDrawable(R.drawable.ic_starleft));
                                    allLevels.mImageViewLevelsStars[i][1].setImageDrawable(getDrawable(R.drawable.ic_starmiddle));
                                    allLevels.mImageViewLevelsStars[i][2].setImageDrawable(getDrawable(R.drawable.ic_starrightlocked));
                                    break;
                                }
                                case 3:{
                                    allLevels.mImageViewLevelsStars[i][0].setImageDrawable(getDrawable(R.drawable.ic_starleft));
                                    allLevels.mImageViewLevelsStars[i][1].setImageDrawable(getDrawable(R.drawable.ic_starmiddle));
                                    allLevels.mImageViewLevelsStars[i][2].setImageDrawable(getDrawable(R.drawable.ic_starright));
                                    break;
                                }
                                default: break;
                            }
                        }
                    }
                    //endregion
                    allLevels.dialog.show();
                }
            }
        });
        //endregion

        //region mImageButtonUserIcon.setOnClickListener
        mImageButtonUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = new User(MainActivity.this);

                user.mTextViewSelectedUsername.setText(mTextViewUsername.getText().toString());

                //region user.mButtonSelectedUserStart.setOnClickListener
                user.mButtonSelectedUserStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTextViewUsername.setText(user.mTextViewSelectedUsername.getText().toString());
                        Cursor cursor = logIOSQLite.executeReader("Select * from " + SQLITE_VARIABLES.Table_Users.DB_TABLE_NAME + " " +
                                "where " + SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME + " = '" + mTextViewUsername.getText() + "';");
                        if(cursor.getCount() != 0){
                            while (cursor.moveToNext()){
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
                            }
                            UserModelLog(usersModel);
                        }
                        user.dialog.cancel();


                    }
                });
                //endregion

                //region user.mTextViewSelectedUsername.setOnClickListener
                user.mTextViewSelectedUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SelectUser selectUser = new SelectUser(user.dialog.getContext());
                        fillUsernames(logIOSQLite, selectUser.mListViewUsername,
                                selectUser.mImageButtonSelectUsernameDelete,
                                selectUser.mImageButtonSelectUsernameSelect, MainActivity.this);
                        //region selectUser.mImageButtonSelectUsernameAdd.setOnClickListener
                        selectUser.mImageButtonSelectUsernameAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectUser.mLinearLayoutAddUser.setVisibility(View.VISIBLE);
                            }
                        });
                        //endregion

                        //region selectUser.mEditTextAddUserUserName.addTextChangedListener
                        selectUser.mEditTextAddUserUserName.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                selectUser.mImageButtonSelectUsernameAddUsername.setVisibility((!selectUser.mEditTextAddUserUserName.getText().toString().equals("")) ?  View.VISIBLE : View.GONE);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        //endregion

                        //region selectUser.mImageButtonSelectUsernameAddUsername.setOnClickListener
                        selectUser.mImageButtonSelectUsernameAddUsername.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try{
                                    String username = selectUser.mEditTextAddUserUserName.getText().toString();
                                    logIOSQLite.executeWriter("INSERT INTO " + SQLITE_VARIABLES.Table_Users.DB_TABLE_NAME + " " +
                                            "(" + SQLITE_VARIABLES.Table_Users.DB_COL_ID + ", " +
                                            SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME + ") " +
                                            "VALUES ( '" + System.currentTimeMillis() + "', " +
                                            "'" + username + "' );");
                                    selectUser.mEditTextAddUserUserName.setText("");
                                    selectUser.mLinearLayoutAddUser.setVisibility(View.GONE);
                                    Toast.makeText(selectUser.dialog.getContext(), "User " + username + " saved!", Toast.LENGTH_SHORT).show();
                                    fillUsernames(logIOSQLite, selectUser.mListViewUsername,
                                            selectUser.mImageButtonSelectUsernameDelete,
                                            selectUser.mImageButtonSelectUsernameSelect, MainActivity.this);
                                }catch (SQLiteException e){
                                    Toast.makeText(selectUser.dialog.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "onClick: " + e.getMessage());
                                }
                            }
                        });
                        //endregion

                        //region selectUser.mListViewUsername.setOnItemClickListener
                        selectUser.mListViewUsername.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                TextView mTextViewSelected = view.findViewById(R.id.mTextViewUsernameItem);
                                for (int j = 0; j < parent.getChildCount(); j++){
                                    TextView mTextView = parent.getChildAt(j).findViewById(R.id.mTextViewUsernameItem);
                                    mTextView.setBackgroundColor(Color.rgb(219, 186, 110));
                                }

                                // change the background color of the selected element
                                mTextViewSelected.setBackgroundColor(Color.rgb(77, 69, 52));

                                Log.i(TAG, "onItemClick: selectedPlayerPosition = " + selectedPlayerPosition);
                                Log.i(TAG, "onItemClick: selectedPlayer = " + selectedPlayer);
                                selectedPlayerPosition = position;
                                selectedPlayer = mTextViewSelected.getText().toString();

                                selectUser.mImageButtonSelectUsernameDelete.setVisibility(View.VISIBLE);
                                selectUser.mImageButtonSelectUsernameSelect.setVisibility(View.VISIBLE);
                            }
                        });
                        //endregion

                        //region selectUser.mImageButtonSelectUsernameSelect.setOnClickListener
                        selectUser.mImageButtonSelectUsernameSelect.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                logIOSQLite.executeWriter("UPDATE " + SQLITE_VARIABLES.Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + SQLITE_VARIABLES.Table_Users.DB_COL_LAST_USED + " = '0' " +
                                        "WHERE " + SQLITE_VARIABLES.Table_Users.DB_COL_LAST_USED + " = '1';");
                                logIOSQLite.executeWriter("UPDATE " + SQLITE_VARIABLES.Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + SQLITE_VARIABLES.Table_Users.DB_COL_LAST_USED + " = '1' " +
                                        "WHERE " + SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME + " = '" + selectedPlayer + "';");
                                selectUser.dialog.cancel();
                                user.mTextViewSelectedUsername.setText(selectedPlayer);
                            }
                        });
                        //endregion

                        //region selectUser.mImageButtonSelectUsernameDelete.setOnClickListener
                        selectUser.mImageButtonSelectUsernameDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    logIOSQLite.executeWriter("DELETE FROM " + SQLITE_VARIABLES.Table_Users.DB_TABLE_NAME +
                                            " WHERE " + SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME + " = '" + selectedPlayer + "';");
                                    Toast.makeText(selectUser.dialog.getContext(), "User " + selectedPlayer + " deleted!", Toast.LENGTH_SHORT).show();

                                    fillUsernames(logIOSQLite, selectUser.mListViewUsername,
                                            selectUser.mImageButtonSelectUsernameDelete,
                                            selectUser.mImageButtonSelectUsernameSelect, MainActivity.this);

                                    selectedPlayerPosition = 0;
                                    selectedPlayer = "";
                                }catch (SQLiteException e){
                                    Toast.makeText(selectUser.dialog.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //endregion
                        selectUser.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                if(selectUser.mImageButtonSelectUsernameSelect.getVisibility() == View.GONE){
                                    user.mTextViewSelectedUsername.setText("");
                                }
                                dialog.cancel();
                            }
                        });
                        selectUser.dialog.show();
                    }
                });
                //endregion

                user.dialog.show();
            }
        });
        //endregion
        //Spanned formattedHtml = HtmlFormatter.formatHtml(new HtmlFormatterBuilder().setHtml("ĀB̅<span style=\"text-decoration: overline; padding-left: 160px; padding-right: 160px;\">A</span><br/>(A)<br/>A&oplus;<br/>A+<br/>").setImageGetter(new HtmlResImageGetter(mTextViewUsername.getContext())));
       /* String hh = "<span style=\" border-top: 1px solid black;\">(A+B)&oplus;<span style=\"font-size: 14px; text-decoration: overline; padding-left: 1pt; padding-right: 1pt; padding-top: 10px;\">C</span></span><br/><span style=\" border-top: 1px solid black;\">(A+B)+<span style=\"font-size: 14px; text-decoration: overline; padding-left: 1pt; padding-right: 1pt; padding-top: 10px;\">C</span></span><br/>(AB)&oplus;<span style=\"text-decoration: overline; padding-left: 1pt; padding-right: 1pt;\">C</span><br/><span style=\" border-top: 1px solid black;\">(A+B)&oplus;C</span><br/>";
        mTextViewUsername.setBackgroundResource(R.drawable.border);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTextViewUsername.setText(Html.fromHtml(hh, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
        }*/


    }

    private void UserModelLog(UsersModel usersModel){
        Log.i(TAG, "getA_id: " + usersModel.getA_id());
        Log.i(TAG, "getA_username: " + usersModel.getA_username());
        Log.i(TAG, "getA_last_used: " + usersModel.getA_last_used());
        Log.i(TAG, "getA_level_1_stars: " + usersModel.getA_level_1_stars());
        Log.i(TAG, "getA_level_2_stars: " + usersModel.getA_level_2_stars());
        Log.i(TAG, "getA_level_3_stars: " + usersModel.getA_level_3_stars());
        Log.i(TAG, "getA_level_4_stars: " + usersModel.getA_level_4_stars());
        Log.i(TAG, "getA_level_5_stars: " + usersModel.getA_level_5_stars());
        Log.i(TAG, "getA_level_6_stars: " + usersModel.getA_level_6_stars());
        Log.i(TAG, "getA_level_7_stars: " + usersModel.getA_level_7_stars());
    }

    private void fillUsernames(LogIOSQLite logIOSQLite,
                               ListView mListViewUsername,
                               ImageButton mImageButtonSelectUsernameDelete,
                               ImageButton mImageButtonSelectUsernameSelect,
                               Activity activity){
        Cursor usernamesCursor = logIOSQLite.executeReader(
                "select " + SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME + " from " +
                        SQLITE_VARIABLES.Table_Users.DB_TABLE_NAME);

        List<String> usernameList = new ArrayList<>();


        if(usernamesCursor.getCount() != 0){
            while(usernamesCursor.moveToNext()){
                usernameList.add(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                        SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME)));
            }
            mListViewUsername.setAdapter(new UsernameListViewAdapter(activity, usernameList));

            mImageButtonSelectUsernameDelete.setVisibility(View.VISIBLE);
            mImageButtonSelectUsernameSelect.setVisibility(View.VISIBLE);
        }
        else {
            mListViewUsername.setAdapter(new UsernameListViewAdapter(activity, usernameList));
            mImageButtonSelectUsernameDelete.setVisibility(View.GONE);
            mImageButtonSelectUsernameSelect.setVisibility(View.GONE);
        }

        mImageButtonSelectUsernameDelete.setVisibility(View.GONE);
        mImageButtonSelectUsernameSelect.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if(requestCode == REQUEST_PLAYER_IN_GAME_MODEL){
            if(resultCode == Activity.RESULT_OK){
                PlayerInGameModel playerInGameModel = (PlayerInGameModel) Objects.requireNonNull(
                        Objects.requireNonNull(data).getExtras()).getSerializable(PLAYER_IN_GAME_MODEL);
                //region Get last used user
                Cursor usernamesCursor = logIOSQLite.executeReader(
                        "SELECT * FROM " + SQLITE_VARIABLES.Table_Users.DB_TABLE_NAME +
                                " where " + SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                List<Integer> stars = new ArrayList<>();
                usersModel = new UsersModel();
                if(usernamesCursor.getCount() != 0){
                    while(usernamesCursor.moveToNext()){
                        usersModel.setA_id(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                SQLITE_VARIABLES.Table_Users.DB_COL_ID)));
                        usersModel.setA_username(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME)));
                        usersModel.setA_last_used(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                SQLITE_VARIABLES.Table_Users.DB_COL_LAST_USED)));
                        usersModel.setA_level_1_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_1_STARS)));
                        usersModel.setA_level_2_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_2_STARS)));
                        usersModel.setA_level_3_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_3_STARS)));
                        usersModel.setA_level_4_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_4_STARS)));
                        usersModel.setA_level_5_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_5_STARS)));
                        usersModel.setA_level_6_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_6_STARS)));
                        usersModel.setA_level_7_stars(usernamesCursor.getString(usernamesCursor.getColumnIndex(
                                SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_7_STARS)));
                    }
                    UserModelLog(usersModel);

                    mTextViewUsername.setText(usersModel.getA_username());
                    stars.add(Integer.parseInt(usersModel.getA_level_1_stars()));
                    stars.add(Integer.parseInt(usersModel.getA_level_2_stars()));
                    stars.add(Integer.parseInt(usersModel.getA_level_3_stars()));
                    stars.add(Integer.parseInt(usersModel.getA_level_4_stars()));
                    stars.add(Integer.parseInt(usersModel.getA_level_5_stars()));
                    stars.add(Integer.parseInt(usersModel.getA_level_6_stars()));
                    stars.add(Integer.parseInt(usersModel.getA_level_7_stars()));
                    //playerStatisticModel.setUsername(usersModel.getA_username());
                    //playerStatisticModel.setCharacter(usersModel.getA_character());
                    for(int i = 0; i < allLevels.mImageViewLevelsStars.length; i++){
                        switch (stars.get(i)){
                            case 1:{
                                allLevels.mImageViewLevelsStars[i][0].setImageDrawable(getDrawable(R.drawable.ic_starleft));
                                allLevels.mImageViewLevelsStars[i][1].setImageDrawable(getDrawable(R.drawable.ic_starmiddlelocked));
                                allLevels.mImageViewLevelsStars[i][2].setImageDrawable(getDrawable(R.drawable.ic_starrightlocked));
                                break;
                            }
                            case 2:{
                                allLevels.mImageViewLevelsStars[i][0].setImageDrawable(getDrawable(R.drawable.ic_starleft));
                                allLevels.mImageViewLevelsStars[i][1].setImageDrawable(getDrawable(R.drawable.ic_starmiddle));
                                allLevels.mImageViewLevelsStars[i][2].setImageDrawable(getDrawable(R.drawable.ic_starrightlocked));
                                break;
                            }
                            case 3:{
                                allLevels.mImageViewLevelsStars[i][0].setImageDrawable(getDrawable(R.drawable.ic_starleft));
                                allLevels.mImageViewLevelsStars[i][1].setImageDrawable(getDrawable(R.drawable.ic_starmiddle));
                                allLevels.mImageViewLevelsStars[i][2].setImageDrawable(getDrawable(R.drawable.ic_starright));
                                break;
                            }
                            default: break;
                        }
                    }
                }
                //endregion
            }
        }
    }
}
