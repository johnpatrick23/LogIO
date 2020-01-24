package com.oneclique.logio;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oneclique.logio.LogIOSQLite.LogIOSQLite;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.QuestionModel;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UserAchievementsModel;
import com.oneclique.logio.LogIOSQLite.SQLITE_VARIABLES.*;
import com.oneclique.logio.helper.CountDownTimer;
import com.oneclique.logio.helper.LogicHelper;
import com.oneclique.logio.model.PlayerInGameModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivityHelper {

    private ImageButton mImageButtonGameBack;
    private TextView mTextViewGameNumberOfQuestion;
    private TextView mTextViewGameTimer;
    private ImageButton mImageButtonGamePause;
    private ImageButton mImageButtonGameHelp;
    private ImageButton mImageButtonGameOption;

    private CountDownTimer popupMessageTimer;
    private int popupMessageTime = 0;

    private int selectedLevel;
    private List<QuestionModel> questionModelList;

    private List<Integer> sequenceOfQuestions;

    private LogIOSQLite logIOSQLite;

    private int questionNumber = 0;

    private PlayerInGameModel playerInGameModel;

    //region Multiple Choice views
    private LinearLayout mLinearLayoutMultipleChoice;
    private TextView mTextViewMultipleChoiceQuestion;
    private Button[] mButtonMultipleChoiceChoices;
    private LinearLayout mLinearLayoutMultipleChoiceChoicesContainer;
    //endregion

    //region Truth Table
    private LinearLayout mLinearLayoutTruthTable;
    //endregion

    //region Identification views
    private LinearLayout mLinearLayoutIdentification;
    private TextView mTextViewIdentificationQuestion;
    private EditText mEditTextIdentificationAnswer;
    private ImageButton mImageButtonIdentificationConfirmAnswer;
    //endregion

    //region Diagram views
    private LinearLayout mLinearLayoutDynamicDiagram;
    private LinearLayout mLinearLayoutDiagram;
    private Button[] mButtonMDiagramChoices;
    //endregion

    //region Drag and Drop views
    private LinearLayout mLinearLayoutDragAndDrop;
    private LinearLayout mLinearLayoutDragAndDropDiagramContainer;
    private LinearLayout[] mLinearLayoutDragAndDropChoiceContainers;
    private ImageView[] mImageViewDragAndDropChoices;
    private Button mButtonDragAndDropSubmit;
    //endregion

    private GameItems gameItems;

    private int totalRemainingTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_game);

        /*mLinearLayoutDynamicDiagram = findViewById(R.id.mLinearLayoutDynamicDiagram);
        View wizardView = getLayoutInflater()
                .inflate(R.layout.diagram_or_gate, mLinearLayoutDynamicDiagram, false);

        mLinearLayoutDynamicDiagram.addView(wizardView);*/

        mImageButtonGameBack = findViewById(R.id.mImageButtonGameBack);
        mTextViewGameNumberOfQuestion = findViewById(R.id.mTextViewGameNumberOfQuestion);
        mTextViewGameTimer = findViewById(R.id.mTextViewGameTimer);
        mImageButtonGamePause = findViewById(R.id.mImageButtonGamePause);
        mImageButtonGameHelp = findViewById(R.id.mImageButtonGameHelp);
        mImageButtonGameOption = findViewById(R.id.mImageButtonGameOption);
        mLinearLayoutTruthTable = findViewById(R.id.mLinearLayoutTruthTable);
        mLinearLayoutMultipleChoiceChoicesContainer = findViewById(R.id.mLinearLayoutMultipleChoiceChoicesContainer);

        gameItems = new GameItems();

        questionModelList = new ArrayList<>();
        sequenceOfQuestions = new ArrayList<>();
        logIOSQLite = new LogIOSQLite(GameActivity.this);
        logIOSQLite.createDatabase();

        playerInGameModel = new PlayerInGameModel();

        final Intent intent = getIntent();

        playerInGameModel = (PlayerInGameModel) Objects.requireNonNull(intent.getExtras()).getSerializable(PLAYER_IN_GAME_MODEL);

        selectedLevel = Integer.parseInt(playerInGameModel.getSelectedLevel());

        slowTimeCDT = new CountDownTimer((5000), (1000)) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingSlowTimeCDT = (int)millisUntilFinished/1000;
                countDownTimer.pause();
            }

            @Override
            public void onFinish() {
                countDownTimer.resume();
            }
        };

        popupMessageTimer = new CountDownTimer(Integer.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                popupMessageTime++;
                Log.i(TAG, "popupMessageTimer onTick: " + popupMessageTime);
            }

            @Override
            public void onFinish() {

            }
        };

        Log.i(TAG, "onCreate: playerInGameModel.getUsername() = " + playerInGameModel.getUsername());
        Log.i(TAG, "onCreate: playerInGameModel.getSelectedLevel() = " + (playerInGameModel.getSelectedLevel() + 1));

        gameItems.mTextViewGameHintCount.setText(playerInGameModel.getUsersModel().getA_hint());
        gameItems.mTextViewGameSlowTimeCount.setText(playerInGameModel.getUsersModel().getA_slow_time());
        gameItems.mTextViewGameAddTimeCount.setText(playerInGameModel.getUsersModel().getA_add_time());

        //region Identification views
        mLinearLayoutIdentification = findViewById(R.id.mLinearLayoutIdentification);
        mTextViewIdentificationQuestion = findViewById(R.id.mTextViewIdentificationQuestion);
        mEditTextIdentificationAnswer = findViewById(R.id.mEditTextIdentificationAnswer);
        mImageButtonIdentificationConfirmAnswer = findViewById(R.id.mImageButtonIdentificationConfirmAnswer);
        //endregion

        //region Diagram views
        mLinearLayoutDynamicDiagram = findViewById(R.id.mLinearLayoutDynamicDiagram);
        mLinearLayoutDiagram = findViewById(R.id.mLinearLayoutDiagram);
        mButtonMDiagramChoices = new Button[]{
                findViewById(R.id.mButtonDiagramChoice1),
                findViewById(R.id.mButtonDiagramChoice2),
                findViewById(R.id.mButtonDiagramChoice3),
                findViewById(R.id.mButtonDiagramChoice4)
        };
        //endregion

        //region Multiple Choice views
          mLinearLayoutMultipleChoice = findViewById(R.id.mLinearLayoutMultipleChoice);
          mTextViewMultipleChoiceQuestion = findViewById(R.id.mTextViewMultipleChoiceQuestion);
          mButtonMultipleChoiceChoices = new Button[]{
                  findViewById(R.id.mButtonMultipleChoiceChoice1),
                  findViewById(R.id.mButtonMultipleChoiceChoice2),
                  findViewById(R.id.mButtonMultipleChoiceChoice3),
                  findViewById(R.id.mButtonMultipleChoiceChoice4)
          };
        //endregion

        //region Drag and drop views

        mButtonDragAndDropSubmit = findViewById(R.id.mButtonDragAndDropSubmit);

        mLinearLayoutDragAndDrop = findViewById(R.id.mLinearLayoutDragAndDrop);

        mLinearLayoutDragAndDropDiagramContainer = findViewById(R.id.mLinearLayoutDragAndDropDiagramContainer);

        mLinearLayoutDragAndDropChoiceContainers = new LinearLayout[]{
                findViewById(R.id.mLinearLayoutDragAndDropChoiceContainer1),
                findViewById(R.id.mLinearLayoutDragAndDropChoiceContainer2),
                findViewById(R.id.mLinearLayoutDragAndDropChoiceContainer3),
                findViewById(R.id.mLinearLayoutDragAndDropChoiceContainer4),
                findViewById(R.id.mLinearLayoutDragAndDropChoiceContainer5),
                findViewById(R.id.mLinearLayoutDragAndDropChoiceContainer6),
                findViewById(R.id.mLinearLayoutDragAndDropChoiceContainer7),
                findViewById(R.id.mLinearLayoutDragAndDropChoiceContainer8)
        };

        mImageViewDragAndDropChoices = new ImageView[]{
                findViewById(R.id.mImageViewDragAndDropChoice1),
                findViewById(R.id.mImageViewDragAndDropChoice2),
                findViewById(R.id.mImageViewDragAndDropChoice3),
                findViewById(R.id.mImageViewDragAndDropChoice4),
                findViewById(R.id.mImageViewDragAndDropChoice5),
                findViewById(R.id.mImageViewDragAndDropChoice6),
                findViewById(R.id.mImageViewDragAndDropChoice7),
                findViewById(R.id.mImageViewDragAndDropChoice8)
        };

        //endregion

        //region Get All questions
        Cursor cursor = logIOSQLite.executeReader("select * from " + Table_Questions.DB_TABLE_NAME + " " +
                "where " + Table_Questions.DB_COL_LEVEL + " = '" + (selectedLevel + 1) + "';");
        if(cursor.getCount() != 0){
            questionModelList = new ArrayList<>();
            while(cursor.moveToNext()){
                QuestionModel questionModel = new QuestionModel();
                questionModel.setA_id(cursor.getString(cursor.getColumnIndex(Table_Questions.DB_COL_ID)));
                questionModel.setA_level(cursor.getString(cursor.getColumnIndex(Table_Questions.DB_COL_LEVEL)));
                questionModel.setA_questiontype(cursor.getString(cursor.getColumnIndex(Table_Questions.DB_COL_QUESTION_TYPE)));
                questionModel.setA_question(cursor.getString(cursor.getColumnIndex(Table_Questions.DB_COL_QUESTION)));
                questionModel.setA_choices(cursor.getString(cursor.getColumnIndex(Table_Questions.DB_COL_CHOICES)));
                questionModel.setA_answer(cursor.getString(cursor.getColumnIndex(Table_Questions.DB_COL_ANSWER)));
                questionModel.setA_timeduration(cursor.getString(cursor.getColumnIndex(Table_Questions.DB_COL_TIME_DURATION)));
                questionModel.setA_category(cursor.getString(cursor.getColumnIndex(Table_Questions.DB_COL_CATEGORY)));
                questionModel.setA_instruction(cursor.getString(cursor.getColumnIndex(Table_Questions.DB_COL_INSTRUCTION)));
                questionModelList.add(questionModel);
            }

            //region Randomize the question
            sequenceOfQuestions = LogicHelper.randomNumbers(1, questionModelList.size());
            List<QuestionModel> tmpQuestionModelList = new ArrayList<>();
            for (int i = 0; i < questionModelList.size(); i++) {
                tmpQuestionModelList.add(questionModelList.get(sequenceOfQuestions.get(i)-1));
            }
            questionModelList = tmpQuestionModelList;
            mTextViewGameNumberOfQuestion.setText(( (questionNumber + 1) + "/5"));
            final Introduction introduction = new Introduction(GameActivity.this, selectedLevel);

            introduction.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    introduction.dialog.cancel();
                    popupMessageTimer.pause();
                    Log.i(TAG, "Line 254: popupMessageTimer.pause();");
                }
            });

            introduction.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    introduction.dialog.cancel();
                    popupMessageTimer.pause();
                    Log.i(TAG, "Line 263: popupMessageTimer.pause();");
                    setUpGameQuestions(questionNumber, 0);
                }
            });
            popupMessageTimer.start();
            introduction.dialog.show();
        }
        //endregion

        mImageButtonGameBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.putExtra(PLAYER_IN_GAME_MODEL, playerInGameModel);
                setResult(Activity.RESULT_OK, intent1);
                finish();
            }
        });

    }

    int remainingTime = 0;
    public CountDownTimer countDownTimer = null;
    private int starLogs = 0;
    public class GameItems{
        public TextView mTextViewGameHintCount;
        public TextView mTextViewGameSlowTimeCount;
        public TextView mTextViewGameAddTimeCount;

        public ImageButton mImageButtonGameHint;
        public ImageButton mImageButtonGameSlowTime;
        public ImageButton mImageButtonGameAddTime;

        public GameItems(){
            mTextViewGameHintCount = findViewById(R.id.mTextViewGameHintCount);
            mTextViewGameSlowTimeCount = findViewById(R.id.mTextViewGameSlowTimeCount);
            mTextViewGameAddTimeCount = findViewById(R.id.mTextViewGameAddTimeCount);

            mImageButtonGameHint = findViewById(R.id.mImageButtonGameHint);
            mImageButtonGameSlowTime = findViewById(R.id.mImageButtonGameSlowTime);
            mImageButtonGameAddTime = findViewById(R.id.mImageButtonGameAddTime);
        }

    }

    private void setUpGameQuestions(int questionNumber, int points){
        Log.i(TAG, "points: " + points);
        Log.i(TAG, "popupMessageTime: " + popupMessageTime);
        starLogs = 0;
        popupMessageTimer.pause();
        if(questionNumber == questionModelList.size()){
            final LevelComplete levelComplete = new LevelComplete(GameActivity.this);
            Toast.makeText(GameActivity.this, "Done!", Toast.LENGTH_LONG).show();
            int stars = 0;
            int lastStar = 0;
            String starsToFill = "";
            String nextLevelToUnlock = "";
            switch (points){
                case 5:  { stars = 3; break; }
                case 3: case 4: { stars = 2; break; }
                case 2: case 1: { stars = 1; break; }
                default: { break; }
            }
            switch (selectedLevel + 1){
                case 1:{
                    starsToFill = Table_Users.DB_COL_LEVEL_1_STARS;
                    nextLevelToUnlock = Table_Users.DB_COL_LEVEL_2_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_1_stars());
                    break;
                }
                case 2:{
                    starsToFill = Table_Users.DB_COL_LEVEL_2_STARS;
                    nextLevelToUnlock = Table_Users.DB_COL_LEVEL_3_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_2_stars());
                    break;
                }
                case 3:{
                    starsToFill = Table_Users.DB_COL_LEVEL_3_STARS;
                    nextLevelToUnlock = Table_Users.DB_COL_LEVEL_4_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_3_stars());
                    break;
                }
                case 4:{
                    starsToFill = Table_Users.DB_COL_LEVEL_4_STARS;
                    nextLevelToUnlock = Table_Users.DB_COL_LEVEL_5_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_4_stars());
                    break;
                }
                case 5:{
                    starsToFill = Table_Users.DB_COL_LEVEL_5_STARS;
                    nextLevelToUnlock = Table_Users.DB_COL_LEVEL_6_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_5_stars());
                    break;
                }
                case 6:{
                    starsToFill = Table_Users.DB_COL_LEVEL_6_STARS;
                    nextLevelToUnlock = Table_Users.DB_COL_LEVEL_7_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_6_stars());
                    break;
                }
                case 7:{
                    starsToFill = Table_Users.DB_COL_LEVEL_7_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_7_stars());
                    break;
                }
                default: break;
            }
            try{
                if(lastStar <= stars){
                    int i = logIOSQLite.executeWriter("UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                            "SET " + starsToFill + " = '" + stars + "' " +
                            "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");

                    try{
                        Cursor cursor = logIOSQLite.executeReader(("SELECT * FROM " + Table_User_Achievements.DB_TABLE_NAME + " " +
                                "WHERE " + Table_User_Achievements.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "' AND " +
                                "" + Table_User_Achievements.DB_COL_LEVEL + " = '" + (Integer.parseInt(playerInGameModel.getSelectedLevel()) + 1) + "';"));

                        UserAchievementsModel userAchievementsModel = new UserAchievementsModel();

                        if(cursor.getCount() != 0){
                            while(cursor.moveToNext()){
                                userAchievementsModel.setA_id(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_ID)));
                                userAchievementsModel.setA_level(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_LEVEL)));
                                userAchievementsModel.setA_stars(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_STARS)));
                                userAchievementsModel.setA_time_finished(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_TIME_FINISHED)));
                                userAchievementsModel.setA_description(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_DESCRIPTION)));
                                userAchievementsModel.setA_number_of_tries(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_NUMBER_OF_TRIES)));
                                userAchievementsModel.setA_username(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_USERNAME)));
                            }

                            try{
                                int numberOfTries = Integer.parseInt(userAchievementsModel.getA_number_of_tries());
                                logIOSQLite.executeWriter(("UPDATE " + Table_User_Achievements.DB_TABLE_NAME + " " +
                                        "SET " + Table_User_Achievements.DB_COL_STARS + " = '" + stars + "', " +
                                        "" + Table_User_Achievements.DB_COL_NUMBER_OF_TRIES + " = '" + (numberOfTries + 1) + "'," +
                                        "" + Table_User_Achievements.DB_COL_TIME_FINISHED + " = '" + (totalRemainingTime / questionModelList.size()) + "' " +
                                        "WHERE " + Table_User_Achievements.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "' AND " +
                                        "" + Table_User_Achievements.DB_COL_LEVEL + " = '" + (Integer.parseInt(playerInGameModel.getSelectedLevel()) + 1) + "';"));

                                Log.i(TAG, "Successfully updated new user achievements!");
                            }catch (SQLiteException ex){
                                Log.i(TAG, ex.getMessage());
                                Log.i(TAG, "Failed to update user achievements!");
                            }
                        }else{
                            try{
                                logIOSQLite.executeWriter(("INSERT INTO " + Table_User_Achievements.DB_TABLE_NAME + " " +
                                        "(" + Table_User_Achievements.DB_COL_ID + ", " +
                                        "" + Table_User_Achievements.DB_COL_LEVEL + ", " +
                                        "" + Table_User_Achievements.DB_COL_STARS + ", " +
                                        "" + Table_User_Achievements.DB_COL_TIME_FINISHED + ", " +
                                        "" + Table_User_Achievements.DB_COL_DESCRIPTION + ", " +
                                        "" + Table_User_Achievements.DB_COL_NUMBER_OF_TRIES + ", " +
                                        "" + Table_User_Achievements.DB_COL_USERNAME + " ) " +
                                        "VALUES ( '" + System.currentTimeMillis() + "', " +
                                        "'" + (Integer.parseInt(playerInGameModel.getSelectedLevel()) + 1) + "', " +
                                        "'" + stars + "', " +
                                        "'" + (totalRemainingTime / questionModelList.size()) + "', " +
                                        "'a_description', " +
                                        "'1', " +
                                        "'" + playerInGameModel.getUsername() + "' );"));
                                Log.i(TAG, "Successfully added new user achievements!");
                            }catch (SQLiteException ex){
                                Log.i(TAG, ex.getMessage());
                                Log.i(TAG, "Failed to insert user achievements!");
                            }
                        }

                        Log.i(TAG, "Successfully get all the list of user achievements table!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, ex.getMessage());
                        Log.i(TAG, "Failed to get the list of user achievements table!");
                    }

                    Log.i(TAG, "logIOSQLite.executeWriter: " + i);
                    Log.i(TAG, "playerInGameModel.getUsername(): " + playerInGameModel.getUsername());
                    Log.i(TAG, "starsToFill: " + starsToFill);
                    Log.i(TAG, "stars: " + stars);
                }else {
                    try{
                        Cursor cursor = logIOSQLite.executeReader(("SELECT * FROM " + Table_User_Achievements.DB_TABLE_NAME + " " +
                                "WHERE " + Table_User_Achievements.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "' AND " +
                                "" + Table_User_Achievements.DB_COL_LEVEL + " = '" + (Integer.parseInt(playerInGameModel.getSelectedLevel()) + 1) + "';"));

                        UserAchievementsModel userAchievementsModel = new UserAchievementsModel();

                        if(cursor.getCount() != 0){
                            while(cursor.moveToNext()){
                                userAchievementsModel.setA_id(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_ID)));
                                userAchievementsModel.setA_level(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_LEVEL)));
                                userAchievementsModel.setA_stars(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_STARS)));
                                userAchievementsModel.setA_time_finished(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_TIME_FINISHED)));
                                userAchievementsModel.setA_description(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_DESCRIPTION)));
                                userAchievementsModel.setA_number_of_tries(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_NUMBER_OF_TRIES)));
                                userAchievementsModel.setA_username(cursor.getString(cursor.getColumnIndex(Table_User_Achievements.DB_COL_USERNAME)));
                            }
                        }

                        int numberOfTries = Integer.parseInt(userAchievementsModel.getA_number_of_tries());

                        logIOSQLite.executeWriter(("UPDATE " + Table_User_Achievements.DB_TABLE_NAME + " " +
                                "SET " + Table_User_Achievements.DB_COL_NUMBER_OF_TRIES + " = '" + (numberOfTries + 1) + "' " +
                                "WHERE " + Table_User_Achievements.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "' AND " +
                                "" + Table_User_Achievements.DB_COL_LEVEL + " = '" + (Integer.parseInt(playerInGameModel.getSelectedLevel()) + 1) + "';"));
                        Log.i(TAG, "Successfully updated user achievements!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, ex.getMessage());
                        Log.i(TAG, "Failed to get the list of user achievements table");
                    }
                }

                if(stars != 0){
                    try {
                        logIOSQLite.executeWriter(("UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                "SET " + nextLevelToUnlock + " = '0' " +
                                "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "' AND " +
                                "" + nextLevelToUnlock + " = 'locked';"));
                        Log.i(TAG, "Next level unlocked!");
                    }catch (SQLiteException e){
                        Log.i(TAG, "Failed to unlock next level!");
                    }
                }

                int[] lockedStars = { R.drawable.ic_starleftlocked, R.drawable.ic_starmiddlelocked, R.drawable.ic_starrightlocked };
                int[] unlockedStars = { R.drawable.ic_starleft, R.drawable.ic_starmiddle, R.drawable.ic_starright };

                for (int i = 0; i < lockedStars.length; i++){
                    levelComplete.mImageViewCompletedStars[i].setImageResource(lockedStars[i]);
                }

                for(int i = 0; i < stars; i++){
                    levelComplete.mImageViewCompletedStars[i].setImageResource(unlockedStars[i]);
                }

                levelComplete.mTextViewCompletedSelectedLevel.setText("Level: " + (selectedLevel + 1));

                Log.i(TAG, "lastStar <= stars: " + (lastStar <= stars));
                Log.i(TAG, "lastStar: " + lastStar);
                Log.i(TAG, "stars: " + stars);
                Log.i(TAG, "lastStar - stars: " + (stars - lastStar));
                if(lastStar <= stars){
                    if((stars - lastStar) > 0){
                        final Items items = new Items(GameActivity.this, (stars - lastStar));

                        //region items.mTextViewItemsRemainingPoints.addTextChangedListener
                        items.mTextViewItemsRemainingPoints.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(String.valueOf(s).equals("0")){
                                    items.mImageButtonItemHome.setVisibility(View.VISIBLE);
                                }else {
                                    items.mImageButtonItemHome.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        //endregion

                        items.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                items.dialog.cancel();
                                Intent intent1 = new Intent();
                                intent1.putExtra(PLAYER_IN_GAME_MODEL, playerInGameModel);
                                try{
                                    int hint_ = Integer.parseInt(gameItems.mTextViewGameHintCount.getText().toString().trim());
                                    int slowTime_ = Integer.parseInt(gameItems.mTextViewGameSlowTimeCount.getText().toString().trim());
                                    int addTime_ = Integer.parseInt(gameItems.mTextViewGameAddTimeCount.getText().toString().trim());

                                    hint_ += Integer.parseInt(items.mTextViewHintItemCount.getText().toString().trim());
                                    slowTime_ += Integer.parseInt(items.mTextViewSlowItemCount.getText().toString().trim());
                                    addTime_ += Integer.parseInt(items.mTextViewAddTimeItemCount.getText().toString().trim());

                                    logIOSQLite.executeWriter(
                                            "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                                    "SET " + Table_Users.DB_COL_HINT + " = '" + hint_ + "', " +
                                                    "" + Table_Users.DB_COL_ADD_TIME + " = '" + addTime_ + "', " +
                                                    "" + Table_Users.DB_COL_SLOW_TIME + " = '" + slowTime_ + "' " +
                                                    "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                                    Log.i(TAG, "User items updated!");
                                }catch (SQLiteException e){
                                    Log.i(TAG, "SQLiteException: " + e.getMessage());
                                }
                                setResult(Activity.RESULT_OK, intent1);
                                finish();
                            }
                        });
                        items.mImageButtonItemHome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                items.dialog.cancel();
                            }
                        });
                        levelComplete.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                levelComplete.dialog.cancel();
                                items.dialog.show();
                            }
                        });
                    }else {
                        levelComplete.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                levelComplete.dialog.cancel();
                                Intent intent1 = new Intent();
                                intent1.putExtra(PLAYER_IN_GAME_MODEL, playerInGameModel);
                                setResult(Activity.RESULT_OK, intent1);
                                finish();
                            }
                        });
                    }
                }else {
                    starLogs = stars;
                    levelComplete.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            levelComplete.dialog.cancel();
                            Intent intent1 = new Intent();
                            intent1.putExtra(PLAYER_IN_GAME_MODEL, playerInGameModel);
                            setResult(Activity.RESULT_OK, intent1);
                            finish();
                        }
                    });
                }
                levelComplete.mImageButtonCompletedOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        levelComplete.dialog.cancel();
                    }
                });
                levelComplete.mTextViewCompletedAverageTime.setText("Avg. Time: " + String.valueOf(totalRemainingTime/questionModelList.size()));
                try {
                    Log.i(TAG, "" + Table_User_Logs.DB_COL_ID + ": " + System.currentTimeMillis());
                    Log.i(TAG, "" + Table_User_Logs.DB_COL_USERNAME + ": " + playerInGameModel.getUsername());
                    Log.i(TAG, "" + Table_User_Logs.DB_COL_LEVEL + ": " + (playerInGameModel.getSelectedLevel() + 1));
                    Log.i(TAG, "" + Table_User_Logs.DB_COL_POPUP_MESSAGE_TIME + ": " + popupMessageTime);
                    Log.i(TAG, "" + Table_User_Logs.DB_COL_STAR + ": " + stars);
                    Log.i(TAG, "" + Table_User_Logs.DB_COL_AVERAGE_TIME + ": " + (totalRemainingTime/questionModelList.size()));
                    logIOSQLite.executeWriter(
                            "INSERT INTO " + Table_User_Logs.DB_TABLE_NAME + " " +
                                    "(" + Table_User_Logs.DB_COL_ID + ", " +
                                    "" + Table_User_Logs.DB_COL_USERNAME + ", " +
                                    "" + Table_User_Logs.DB_COL_LEVEL + ", " +
                                    "" + Table_User_Logs.DB_COL_POPUP_MESSAGE_TIME + ", " +
                                    "" + Table_User_Logs.DB_COL_STAR + ", " +
                                    "" + Table_User_Logs.DB_COL_AVERAGE_TIME + ") " +
                                    "VALUES " +
                                    "('" + System.currentTimeMillis() + "', " +
                                    "'" + playerInGameModel.getUsername() + "', " +
                                    "'" + (playerInGameModel.getSelectedLevel() + 1) + "', " +
                                    "'" + popupMessageTime + "', " +
                                    "'" + starLogs + "', " +
                                    "'" + (totalRemainingTime/questionModelList.size()) + "');");
                    Log.i(TAG, "Log saved! <<<<<<<<<<<<<<<<<<<<<<<");
                }catch (SQLiteException ex){
                    Log.i(TAG, "onCancel: " + ex.getMessage());
                }
                levelComplete.dialog.show();

            }catch (SQLiteException e){
                Log.i(TAG, "setUpGameQuestions: " + e.getMessage());
            }
            return;
        }
        QuestionModelLog(questionModelList.get(questionNumber));
        Log.i(TAG, "questionNumber: " + questionNumber);

        mLinearLayoutDiagram.setVisibility(View.GONE);
        mLinearLayoutTruthTable.setVisibility(View.GONE);
        mLinearLayoutMultipleChoice.setVisibility(View.GONE);
        mLinearLayoutIdentification.setVisibility(View.GONE);
        mLinearLayoutDragAndDrop.setVisibility(View.GONE);
        switch (questionModelList.get(questionNumber).getA_questiontype()){
            case QuestionType.MULTIPLE_CHOICE:{
                Log.i(TAG, "onCreate: MULTIPLE_CHOICE");
                if(!questionModelList.get(questionNumber).getA_answer().isEmpty()){
                    setUpMultipleChoice(mLinearLayoutMultipleChoice, mTextViewMultipleChoiceQuestion,
                            mButtonMultipleChoiceChoices, questionModelList.get(questionNumber),
                            mTextViewGameNumberOfQuestion, mTextViewGameTimer, questionNumber,
                            mImageButtonGamePause, mImageButtonGameHelp, mImageButtonGameOption,
                            points, gameItems, mLinearLayoutMultipleChoiceChoicesContainer);
                }else {
                    setUpGameQuestions(questionNumber + 1, points);
                }
                break;
            }
            case QuestionType.DIAGRAM:{
                Log.i(TAG, "onCreate: DIAGRAM");

                if(!questionModelList.get(questionNumber).getA_answer().isEmpty()) {
                    setUpDiagram(mLinearLayoutDiagram, mLinearLayoutDynamicDiagram,
                            mButtonMDiagramChoices, questionModelList.get(questionNumber),
                            mTextViewGameNumberOfQuestion, mTextViewGameTimer, questionNumber,
                            mImageButtonGamePause, mImageButtonGameHelp, mImageButtonGameOption, points, gameItems);
                }else {
                    setUpGameQuestions(questionNumber + 1, points);
                }
                break;
            }
            case QuestionType.DRAG_AND_DROP:{
                Log.i(TAG, "onCreate: DRAG_AND_DROP");
                if(!questionModelList.get(questionNumber).getA_answer().isEmpty()) {
                    setUpDragAndDrop(mLinearLayoutDragAndDrop, questionModelList.get(questionNumber),
                            mLinearLayoutDragAndDropDiagramContainer, mLinearLayoutDragAndDropChoiceContainers,
                            mImageViewDragAndDropChoices, mButtonDragAndDropSubmit,
                            mTextViewGameNumberOfQuestion, mTextViewGameTimer, questionNumber,
                            mImageButtonGamePause, mImageButtonGameHelp, mImageButtonGameOption, points, gameItems);
                }else {
                    setUpGameQuestions(questionNumber + 1, points);
                }
                break;
            }
            case QuestionType.IDENTIFICATION:{
                Log.i(TAG, "onCreate: IDENTIFICATION");

                if(!questionModelList.get(questionNumber).getA_answer().isEmpty()) {
                    setUpIdentification(mLinearLayoutIdentification, mTextViewIdentificationQuestion,
                            mEditTextIdentificationAnswer, mImageButtonIdentificationConfirmAnswer,
                            questionModelList.get(questionNumber), mTextViewGameNumberOfQuestion,
                            mTextViewGameTimer, questionNumber, mImageButtonGamePause,
                            mImageButtonGameHelp, mImageButtonGameOption, points, gameItems);
                }else {
                    setUpGameQuestions(questionNumber + 1, points);
                }
                break;
            }
            case QuestionType.TRUTH_TABLE:{

                if(!questionModelList.get(questionNumber).getA_answer().isEmpty()) {
                    setUpTruthTable(mLinearLayoutTruthTable, questionModelList.get(questionNumber),
                            mTextViewGameTimer, mTextViewGameTimer, questionNumber, mImageButtonGamePause,
                            mImageButtonGameHelp, mImageButtonGameOption, points, gameItems);
                }else {
                    setUpGameQuestions(questionNumber + 1, points);
                }
                break;
            }
        }
    }
    public int remainingTimeInQuestion = 0;
    public int remainingSlowTimeCDT = 0;
    public CountDownTimer slowTimeCDT;

    int hintLimit = 1;
    //region Drag and Drop
    private void setUpDragAndDrop(final LinearLayout mLinearLayoutDragAndDrop,
                                  final QuestionModel questionModel,
                                  final LinearLayout mLinearLayoutDragAndDropDiagramContainer,
                                  LinearLayout[] mLinearLayoutDragAndDropChoiceContainers,
                                  final ImageView[] mImageViewDragAndDropChoices,
                                  final Button mButtonDragAndDropSubmit,
                                  final TextView mTextViewGameNumberOfQuestion,
                                  final TextView mTextViewGameTimer,
                                  final int questionNumber,
                                  ImageButton mImageButtonGamePause,
                                  ImageButton mImageButtonGameHelp,
                                  ImageButton mImageButtonGameOption,
                                  final int points,
                                  final GameItems gameItems){
        final HowToPlay howToPlay = new HowToPlay(GameActivity.this);
        final Paused paused = new Paused(GameActivity.this);
        final Options options = new Options(GameActivity.this);
        final TimesUp timesUp = new TimesUp(GameActivity.this);
        final Correct correct = new Correct(GameActivity.this);
        remainingTimeInQuestion = 0;

        howToPlay.mTextViewInstruction.setText(questionModel.getA_instruction());
        popupMessageTimer.resume();
        mTextViewGameNumberOfQuestion.setText(( (questionNumber + 1) + "/5"));
        countDownTimer = new CountDownTimer((Integer.parseInt(questionModel.getA_timeduration()) * 1000), (1000)) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeInQuestion = (int)millisUntilFinished/1000;
                remainingTime = (int)millisUntilFinished/1000;
                mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                timesUp.mImageButtonTimesUpOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timesUp.dialog.cancel();
                        setUpGameQuestions((questionNumber + 1), points);
                    }
                });

                timesUp.mImageButtonTimesUpRestart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                try{
                    timesUp.dialog.show();
                }catch (Exception e){
                    Log.i(TAG, "onFinish: " + e.getMessage());
                }
            }
        };
        howToPlay.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                howToPlay.dialog.cancel();
                popupMessageTimer.pause();
                Log.i(TAG, "Line 1133: popupMessageTimer.pause();");
            }
        });



        howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                howToPlay.dialog.cancel();
                popupMessageTimer.pause();
                mLinearLayoutDragAndDrop.setVisibility(View.VISIBLE);
                Log.i(TAG, "Line 1149: popupMessageTimer.pause();");
                countDownTimer.start();
            }
        });
        howToPlay.dialog.show();


        for(int i = 0; i < mLinearLayoutDragAndDropChoiceContainers.length; i++){
            mImageViewDragAndDropChoices[i].setVisibility(View.VISIBLE);
            mLinearLayoutDragAndDropChoiceContainers[i].setVisibility(View.VISIBLE);

        }
        LinearLayout[] mLinearLayoutDragAndDropAnswers = null;
        switch (questionModel.getA_category()){
            case QuestionCategory.AND:{
                Log.i(TAG, "setUpDragAndDrop: AND");
                View dragAndDropLayout = getLayoutInflater()
                        .inflate(R.layout.drag_and_drop_and_gate, mLinearLayoutDragAndDropDiagramContainer, false);
                mLinearLayoutDragAndDropDiagramContainer.removeAllViews();
                mLinearLayoutDragAndDropDiagramContainer.addView(dragAndDropLayout);

                mLinearLayoutDragAndDropAnswers = new LinearLayout[]{
                        dragAndDropLayout.findViewById(R.id.mLinearLayoutANDDragAndDropAnswer1),
                        dragAndDropLayout.findViewById(R.id.mLinearLayoutANDDragAndDropAnswer2)
                };

                for (LinearLayout  mLinearLayoutDragAndDropAnswer : mLinearLayoutDragAndDropAnswers) {
                    mLinearLayoutDragAndDropAnswer.setOnDragListener(new MyDragListener());
                }
                break;
            }
            case QuestionCategory.NOT:{
                Log.i(TAG, "setUpDragAndDrop: NOT");
                View dragAndDropLayout = getLayoutInflater()
                        .inflate(R.layout.drag_and_drop_not_gate, mLinearLayoutDragAndDropDiagramContainer, false);
                mLinearLayoutDragAndDropDiagramContainer.removeAllViews();
                mLinearLayoutDragAndDropDiagramContainer.addView(dragAndDropLayout);

                mLinearLayoutDragAndDropAnswers = new LinearLayout[]{
                        dragAndDropLayout.findViewById(R.id.mLinearLayoutNOTDragAndDropAnswer1)
                };

                for (LinearLayout  mLinearLayoutDragAndDropAnswer : mLinearLayoutDragAndDropAnswers) {
                    mLinearLayoutDragAndDropAnswer.setOnDragListener(new MyDragListener());
                }
                break;
            }
            case QuestionCategory.OR:{
                Log.i(TAG, "setUpDragAndDrop: OR");
                View dragAndDropLayout = getLayoutInflater()
                        .inflate(R.layout.drag_and_drop_or_gate, mLinearLayoutDragAndDropDiagramContainer, false);
                mLinearLayoutDragAndDropDiagramContainer.removeAllViews();
                mLinearLayoutDragAndDropDiagramContainer.addView(dragAndDropLayout);

                mLinearLayoutDragAndDropAnswers = new LinearLayout[]{
                        dragAndDropLayout.findViewById(R.id.mLinearLayoutORDragAndDropAnswer1),
                        dragAndDropLayout.findViewById(R.id.mLinearLayoutORDragAndDropAnswer2)
                };
                for (LinearLayout  mLinearLayoutDragAndDropAnswer : mLinearLayoutDragAndDropAnswers) {
                    mLinearLayoutDragAndDropAnswer.setOnDragListener(new MyDragListener());
                }

                break;
            }
            case QuestionCategory.XNOR:{
                Log.i(TAG, "setUpDragAndDrop: XNOR");
                View dragAndDropLayout = getLayoutInflater()
                        .inflate(R.layout.drag_and_drop_xnor_gate, mLinearLayoutDragAndDropDiagramContainer, false);
                mLinearLayoutDragAndDropDiagramContainer.removeAllViews();
                mLinearLayoutDragAndDropDiagramContainer.addView(dragAndDropLayout);

                mLinearLayoutDragAndDropAnswers = new LinearLayout[]{
                        dragAndDropLayout.findViewById(R.id.mLinearLayoutXNORDragAndDropAnswer1),
                        dragAndDropLayout.findViewById(R.id.mLinearLayoutXNORDragAndDropAnswer2),
                        dragAndDropLayout.findViewById(R.id.mLinearLayoutXNORDragAndDropAnswer3)
                };

                for (LinearLayout  mLinearLayoutDragAndDropAnswer : mLinearLayoutDragAndDropAnswers) {
                    mLinearLayoutDragAndDropAnswer.setOnDragListener(new MyDragListener());
                }

                break;
            }
            case QuestionCategory.XOR:{
                View dragAndDropLayout = getLayoutInflater()
                        .inflate(R.layout.drag_and_drop_xor_gate, mLinearLayoutDragAndDropDiagramContainer, false);
                mLinearLayoutDragAndDropDiagramContainer.removeAllViews();
                mLinearLayoutDragAndDropDiagramContainer.addView(dragAndDropLayout);

                mLinearLayoutDragAndDropAnswers = new LinearLayout[]{
                        dragAndDropLayout.findViewById(R.id.mLinearLayoutXORDragAndDropAnswer1),
                        dragAndDropLayout.findViewById(R.id.mLinearLayoutXORDragAndDropAnswer2),
                        dragAndDropLayout.findViewById(R.id.mLinearLayoutXORDragAndDropAnswer3)
                };

                for (LinearLayout  mLinearLayoutDragAndDropAnswer : mLinearLayoutDragAndDropAnswers) {
                    mLinearLayoutDragAndDropAnswer.setOnDragListener(new MyDragListener());
                }

                break;
            }case QuestionCategory.INTEGRATED_CIRCUIT:{
                int dragAndDrop = 0;
                View dragAndDropLayout = null;
                switch (questionModel.getA_question()){
                    case "drag_and_drop_4002":{
                        dragAndDrop =  R.layout.drag_and_drop_4002;
                        dragAndDropLayout = getLayoutInflater()
                                .inflate(dragAndDrop, mLinearLayoutDragAndDropDiagramContainer, false);
                        mLinearLayoutDragAndDropDiagramContainer.removeAllViews();
                        mLinearLayoutDragAndDropDiagramContainer.addView(dragAndDropLayout);
                        mLinearLayoutDragAndDropAnswers = new LinearLayout[]{
                                dragAndDropLayout.findViewById(R.id.mLinearLayout4002Answer1),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout4002Answer2)
                        };
                        break;
                    }
                    case "drag_and_drop_10107":{
                        dragAndDrop =  R.layout.drag_and_drop_10107;
                        dragAndDropLayout = getLayoutInflater()
                                .inflate(dragAndDrop, mLinearLayoutDragAndDropDiagramContainer, false);
                        mLinearLayoutDragAndDropDiagramContainer.removeAllViews();
                        mLinearLayoutDragAndDropDiagramContainer.addView(dragAndDropLayout);
                        mLinearLayoutDragAndDropAnswers = new LinearLayout[]{
                                dragAndDropLayout.findViewById(R.id.mLinearLayout10107Answer1),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout10107Answer2),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout10107Answer3)
                        };
                        break;
                    }
                    case "drag_and_drop_7402":{
                        dragAndDrop =  R.layout.drag_and_drop_7402;
                        dragAndDropLayout = getLayoutInflater()
                                .inflate(dragAndDrop, mLinearLayoutDragAndDropDiagramContainer, false);
                        mLinearLayoutDragAndDropDiagramContainer.removeAllViews();
                        mLinearLayoutDragAndDropDiagramContainer.addView(dragAndDropLayout);
                        mLinearLayoutDragAndDropAnswers = new LinearLayout[]{
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7402Answer1),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7402Answer2),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7402Answer3),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7402Answer4)
                        };
                        break;
                    }
                    case "drag_and_drop_7404":{
                        dragAndDrop =  R.layout.drag_and_drop_7404;
                        dragAndDropLayout = getLayoutInflater()
                                .inflate(dragAndDrop, mLinearLayoutDragAndDropDiagramContainer, false);
                        mLinearLayoutDragAndDropDiagramContainer.removeAllViews();
                        mLinearLayoutDragAndDropDiagramContainer.addView(dragAndDropLayout);
                        mLinearLayoutDragAndDropAnswers = new LinearLayout[]{
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7404Answer1),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7404Answer2),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7404Answer3),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7404Answer4),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7404Answer5),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7404Answer6)
                        };
                        break;
                    }
                    case "drag_and_drop_7400":{
                        dragAndDrop =  R.layout.drag_and_drop_7400;
                        dragAndDropLayout = getLayoutInflater()
                                .inflate(dragAndDrop, mLinearLayoutDragAndDropDiagramContainer, false);
                        mLinearLayoutDragAndDropDiagramContainer.removeAllViews();
                        mLinearLayoutDragAndDropDiagramContainer.addView(dragAndDropLayout);
                        mLinearLayoutDragAndDropAnswers = new LinearLayout[]{
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7400Answer1),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7400Answer2),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7400Answer3),
                                dragAndDropLayout.findViewById(R.id.mLinearLayout7400Answer4)
                        };
                        break;
                    }
                    default:{break;}
                }

                for (LinearLayout  mLinearLayoutDragAndDropAnswer : mLinearLayoutDragAndDropAnswers) {
                    mLinearLayoutDragAndDropAnswer.setOnDragListener(new MyDragListener());
                }
            }
            default:{ break; }

        }
        final List<String> choices = LogicHelper.choiceReBuilder(questionModel.getA_choices());
        final List<Integer> numbers = LogicHelper.randomNumbers(1, choices.size());
        List<String> tmpChoices = new ArrayList<>();
        Log.i(TAG, "setUpDragAndDrop: Choices");
        for (int i = 0; i < choices.size(); i++) {
            tmpChoices.add(choices.get(numbers.get(i)-1));
            Log.i(TAG, "setUpDragAndDrop: " + choices.get(numbers.get(i)-1));
        }

        for (int i = 0; i < mLinearLayoutDragAndDropChoiceContainers.length; i++) {
            if(mLinearLayoutDragAndDropChoiceContainers[i].getChildCount() == 0){
                Log.i(TAG, "mLinearLayoutDragAndDropChoiceContainers[" + i + "]: " + mLinearLayoutDragAndDropChoiceContainers[i].getChildCount());
                ViewGroup owner = (ViewGroup) mImageViewDragAndDropChoices[i].getParent();
                owner.removeAllViews();
                ImageView imageView = mImageViewDragAndDropChoices[i];
                mLinearLayoutDragAndDropChoiceContainers[i].addView(imageView);
            }
        }

        for (ImageView mImageViewDragAndDropChoice : mImageViewDragAndDropChoices){
            mImageViewDragAndDropChoice.setContentDescription("");
            mImageViewDragAndDropChoice.setImageDrawable(null);
            mImageViewDragAndDropChoice.setVisibility(View.GONE);
        }
        for(int i = 0; i < mImageViewDragAndDropChoices.length; i++){
            if(i < choices.size()){
                mLinearLayoutDragAndDropChoiceContainers[i].setVisibility(View.VISIBLE);
                mImageViewDragAndDropChoices[i].setVisibility(View.VISIBLE);
            }else {
                mImageViewDragAndDropChoices[i].setVisibility(View.GONE);
                mLinearLayoutDragAndDropChoiceContainers[i].setVisibility(View.GONE);
            }
        }
        for(int i = 0; i < tmpChoices.size(); i++){
            Log.i(TAG, "setUpDragAndDrop: " + tmpChoices.get(i).toLowerCase());

            mImageViewDragAndDropChoices[i].setVisibility(View.VISIBLE);
            try{

                Log.i(TAG, "setUpDragAndDrop: " + tmpChoices.get(i).toLowerCase());
                mImageViewDragAndDropChoices[i].setContentDescription(tmpChoices.get(i));
                mImageViewDragAndDropChoices[i].setImageDrawable(GetDrawableResource(GameActivity.this, tmpChoices.get(i).toLowerCase()));
            }
            catch (Exception e){
                mImageViewDragAndDropChoices[i].setImageResource(R.drawable.bg_choices);
                mImageViewDragAndDropChoices[i].setContentDescription(tmpChoices.get(i));
                Log.i(TAG, "setUpDragAndDrop: " + remainingTime );
                Log.i(TAG, "setUpDragAndDrop: " + e.getMessage());
            }

            mImageViewDragAndDropChoices[i].setOnTouchListener(new MyTouchListener());
            mLinearLayoutDragAndDropChoiceContainers[i].setOnDragListener(new MyDragListener());

        }
        final LinearLayout[] mLinearLayoutDragAndDropAnswers_ = mLinearLayoutDragAndDropAnswers;
        final List<String> answers = LogicHelper.choiceReBuilder(questionModel.getA_answer());

        mButtonDragAndDropSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "onClick: Check answer");
                int correctPoints = 0;
                try {
                    for(int i = 0; i < mLinearLayoutDragAndDropAnswers_.length; i++){
                        String answer = mLinearLayoutDragAndDropAnswers_[i].getChildAt(0).getContentDescription().toString();
                        Log.i(TAG, "onClick: " + i + " = " + answer);
                        if(answers.get(i).toLowerCase().equals(answer.toLowerCase())){
                            correctPoints++;
                        }
                    }
                }catch (Exception ex){
                    Log.i(TAG, "mButtonDragAndDropSubmit: " + ex.getMessage());
                }
                Log.i(TAG, "correctPoints: " + correctPoints);
                if(correctPoints == answers.size()){
                    correct.mTextViewCorrectMessage.setText("You've got the perfect score");
                    countDownTimer.pause();
                    correct.mImageButtonCorrectOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {countDownTimer.pause();
                            remainingTime = Integer.parseInt(mTextViewGameTimer.getText().toString());
                            countDownTimer.cancel();
                            setUpGameQuestions(questionNumber + 1, points + 1);
                            correct.dialog.cancel();
                        }
                    });
                    correct.dialog.show();
                }else {
                    correct.mTextViewCorrectMessage.setText(("You've got the " + correctPoints + " score"));
                    correct.mImageButtonCorrectOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {countDownTimer.pause();
                            remainingTime = Integer.parseInt(mTextViewGameTimer.getText().toString());
                            countDownTimer.cancel();
                            setUpGameQuestions(questionNumber + 1, points);
                            correct.dialog.cancel();
                        }
                    });
                    correct.dialog.show();
                }
                mLinearLayoutDragAndDrop.setVisibility(View.GONE);
                totalRemainingTime += remainingTime;
            }
        });

        Log.i(TAG, "setUpDragAndDrop: getA_timeduration = " + Integer.parseInt(questionModel.getA_timeduration()));

        hintLimit = 1;

        //region gameItems.mImageButtonGameHint.setOnClickListener
        gameItems.mImageButtonGameHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hintLimit != 0){
                    int hint_ = Integer.parseInt(gameItems.mTextViewGameHintCount.getText().toString());
                    if(hint_ != 0){
                        hint_--;
                        gameItems.mTextViewGameHintCount.setText(String.valueOf(hint_));
                        try{
                            logIOSQLite.executeWriter(
                                    "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                            "SET " + Table_Users.DB_COL_HINT + " = '" + hint_ + "' " +
                                            "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                            Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                        }catch (SQLiteException ex){
                            Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                        }
                        int hidden = 1;
                        for (int i = 0; i < mImageViewDragAndDropChoices.length; i++) {
                            ImageView choice_ = mImageViewDragAndDropChoices[i];
                            if(hidden != 0){
                                if(!choice_.getContentDescription().toString().trim().toLowerCase().equals(questionModel.getA_answer().trim().toLowerCase())){
                                    choice_.setVisibility(View.INVISIBLE);
                                    hidden--;
                                }
                            }
                        }

                    }
                    hintLimit--;
                    //SnackBarMessage("Hint Item -1");
                    Toast.makeText(GameActivity.this, "Hint Item -1", Toast.LENGTH_SHORT).show();
                }else {
                    SnackBarMessage("You can only use hint once per question.");
                }
            }
        });
        //endregion

        //region gameItems.mImageButtonGameSlowTime.setOnClickListener
        gameItems.mImageButtonGameSlowTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int slowTime = Integer.parseInt(gameItems.mTextViewGameSlowTimeCount.getText().toString());
                if(slowTime != 0){
                    slowTime--;
                    gameItems.mTextViewGameSlowTimeCount.setText(String.valueOf(slowTime));

                    countDownTimer.pause();
                    slowTimeCDT.cancel();

                    try{
                        logIOSQLite.executeWriter(
                                "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + Table_Users.DB_COL_SLOW_TIME + " = '" + slowTime + "' " +
                                        "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                        Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                    }

                    remainingSlowTimeCDT += 6;

                    slowTimeCDT = new CountDownTimer((remainingSlowTimeCDT * 1000), (1000)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingSlowTimeCDT = (int)millisUntilFinished/1000;
                        }

                        @Override
                        public void onFinish() {
                            countDownTimer.resume();
                        }
                    };
                    //SnackBarMessage("Slow Time Item -1");
                    Toast.makeText(GameActivity.this, "Slow Time Item -1", Toast.LENGTH_SHORT).show();
                    slowTimeCDT.start();
                }
            }
        });
        //endregion

        //region gameItems.mImageButtonGameAddTime.setOnClickListener
        gameItems.mImageButtonGameAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int addTime = Integer.parseInt(gameItems.mTextViewGameAddTimeCount.getText().toString());
                if(addTime != 0){
                    addTime--;
                    gameItems.mTextViewGameAddTimeCount.setText(String.valueOf(addTime));

                    countDownTimer.cancel();

                    try{
                        logIOSQLite.executeWriter(
                                "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + Table_Users.DB_COL_ADD_TIME + " = '" + addTime + "' " +
                                        "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                        Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                    }

                    remainingTimeInQuestion += 5;
                    countDownTimer = new CountDownTimer((remainingTimeInQuestion * 1000), (1000)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingTimeInQuestion = (int)millisUntilFinished/1000;
                            mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
                        }

                        @Override
                        public void onFinish() {
                            timesUp.mImageButtonTimesUpOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    timesUp.dialog.cancel();
                                    setUpGameQuestions((questionNumber + 1), points);
                                }
                            });

                            timesUp.mImageButtonTimesUpRestart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            try{
                                timesUp.dialog.show();
                            }catch (Exception e){
                                Log.i(TAG, "onFinish: " + e.getMessage());
                            }
                        }
                    };
                    //SnackBarMessage("Add Time Item -1");
                    Toast.makeText(GameActivity.this, "Add Time Item -1", Toast.LENGTH_SHORT).show();
                    countDownTimer.start();
                }
            }
        });
        //endregion

        //region Game buttons

        paused.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                paused.dialog.cancel();
                popupMessageTimer.pause();
                Log.i(TAG, "Line 1124: popupMessageTimer.pause();");
            }
        });
        paused.mButtonPausedRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paused.dialog.cancel();
            }
        });


        options.mButtonOptionResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.resume();
                mLinearLayoutDragAndDrop.setVisibility(View.VISIBLE);
                options.dialog.cancel();
            }
        });

        paused.mButtonPausedResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.resume();
                mLinearLayoutDragAndDrop.setVisibility(View.VISIBLE);
                paused.dialog.cancel();
            }
        });

        paused.mButtonPausedExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                popupMessageTimer.cancel();
                finish();
            }
        });

        mImageButtonGamePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutDragAndDrop.setVisibility(View.GONE);
                popupMessageTimer.resume();
                Log.i(TAG, "Line 1186: popupMessageTimer.resume()");
                paused.dialog.show();
            }
        });

        mImageButtonGameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutDragAndDrop.setVisibility(View.GONE);
                popupMessageTimer.resume();
                Log.i(TAG, "Line 1197: popupMessageTimer.resume();");
                howToPlay.mTextViewInstruction.setText(questionModel.getA_instruction());
                howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDownTimer.resume();
                        popupMessageTimer.pause();
                        Log.i(TAG, "Line 1205: popupMessageTimer.pause();");
                        mLinearLayoutDragAndDrop.setVisibility(View.VISIBLE);
                        howToPlay.dialog.cancel();
                    }
                });
                popupMessageTimer.resume();
                Log.i(TAG, "Line 1210: popupMessageTimer.resume()");
                howToPlay.dialog.show();
            }
        });

        mImageButtonGameOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutDragAndDrop.setVisibility(View.GONE);
                options.dialog.show();
            }
        });

        paused.mButtonPausedLevels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                popupMessageTimer.cancel();
                finish();
            }
        });

        //endregion
        Log.i(TAG, "Line 1237: popupMessageTimer.resume()");
        //mImageButtonGameHelp.callOnClick();

    }


    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                Log.i(TAG, "onTouch: " + view.getContentDescription().toString());
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                if(view.startDrag(data, shadowBuilder, view, 0)){
                    view.setVisibility(View.VISIBLE);
                }
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        Drawable enterShape = getResources().getDrawable(
                R.drawable.bg_choices);
        Drawable normalShape = getResources().getDrawable(R.drawable.bg_choices);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setBackgroundDrawable(enterShape);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackgroundDrawable(normalShape);
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    LinearLayout container = (LinearLayout) v;
                    if(container.getChildCount() >= 1){
                        return false;
                    }else {
                        owner.removeView(view);
                        container.addView(view);
                        view.setVisibility(View.VISIBLE);
                        Log.i(TAG, "onDrag: ACTION_DROP" + container.getChildCount());
                        return true;
                    }

                case DragEvent.ACTION_DRAG_ENDED:
                    //v.setBackgroundDrawable(normalShape);
                    return true;
                default:
                    return false;
            }
        }
    }
    //endregion

    //region Truth table
    private void setUpTruthTable(final LinearLayout mLinearLayoutTruthTable,
                                 final QuestionModel questionModel,
                                 final TextView mTextViewGameNumberOfQuestion,
                                 final TextView mTextViewGameTimer,
                                 final int questionNumber,
                                 ImageButton mImageButtonGamePause,
                                 ImageButton mImageButtonGameHelp,
                                 ImageButton mImageButtonGameOption,
                                 final int points,
                                 final GameItems gameItems){

        final HowToPlay howToPlay = new HowToPlay(GameActivity.this);
        final Paused paused = new Paused(GameActivity.this);
        final Options options = new Options(GameActivity.this);
        final TimesUp timesUp = new TimesUp(GameActivity.this);
        final Correct correct = new Correct(GameActivity.this);

        mTextViewGameNumberOfQuestion.setText(( (questionNumber + 1) + "/5"));

        howToPlay.mTextViewInstruction.setText(questionModel.getA_instruction());
        popupMessageTimer.resume();
        Log.i(TAG, "Line 1782: popupMessageTimer.resume()");

        countDownTimer = new CountDownTimer((Integer.parseInt(questionModel.getA_timeduration()) * 1000), (1000)) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {

                timesUp.mImageButtonTimesUpOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timesUp.dialog.cancel();
                        setUpGameQuestions((questionNumber + 1), points);
                    }
                });

                timesUp.mImageButtonTimesUpRestart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                try{
                    timesUp.dialog.show();
                }catch (Exception e){
                    Log.i(TAG, "onFinish: " + e.getMessage());
                }
            }
        };
        howToPlay.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                howToPlay.dialog.cancel();
                popupMessageTimer.pause();
                Log.i(TAG, "Line 1687: popupMessageTimer.pause();");
            }
        });

        howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                howToPlay.dialog.cancel();
                popupMessageTimer.pause();
                mLinearLayoutTruthTable.setVisibility(View.VISIBLE);
                Log.i(TAG, "Line 1696: popupMessageTimer.pause();");
                countDownTimer.start();
            }
        });

        howToPlay.dialog.show();

        /*mTextViewMultipleChoiceQuestion.setText(questionModel.getA_question());
        if(questionModel.getA_question().toLowerCase().isEmpty()){
            Log.i(TAG, "setUpMultipleChoice: null question" );
            return;
        }*/
        Button mButtonTruthTableSubmit = null;
        TextView[] mTextViewTruthTableAnswers = null;
        switch (questionModel.getA_category()){
            case QuestionCategory.AND:{
                Log.i(TAG, "setUpTruthTable: AND");
                View diagramLayout = getLayoutInflater()
                        .inflate(R.layout.truth_table_and_gate, mLinearLayoutTruthTable, false);
                mLinearLayoutTruthTable.removeAllViews();
                mLinearLayoutTruthTable.addView(diagramLayout);

                mButtonTruthTableSubmit = diagramLayout.findViewById(R.id.mButtonTruthTableANDSubmit);
                mTextViewTruthTableAnswers = new TextView[]{
                        diagramLayout.findViewById(R.id.mTextViewTruthTableANDAnswer1),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableANDAnswer2),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableANDAnswer3),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableANDAnswer4)
                };

                break;
            }
            case QuestionCategory.NOT:{
                Log.i(TAG, "setUpTruthTable: NOT");
                View diagramLayout = getLayoutInflater()
                        .inflate(R.layout.truth_table_not_gate, mLinearLayoutTruthTable, false);
                mLinearLayoutTruthTable.removeAllViews();
                mLinearLayoutTruthTable.addView(diagramLayout);

                mButtonTruthTableSubmit = diagramLayout.findViewById(R.id.mButtonTruthTableNOTSubmit);
                mTextViewTruthTableAnswers = new TextView[]{
                        diagramLayout.findViewById(R.id.mTextViewTruthTableNotAnswer1),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableNotAnswer2)
                };

                break;
            }
            case QuestionCategory.OR:{
                Log.i(TAG, "setUpTruthTable: OR");
                View diagramLayout = getLayoutInflater()
                        .inflate(R.layout.truth_table_or_gate, mLinearLayoutTruthTable, false);
                mLinearLayoutTruthTable.removeAllViews();
                mLinearLayoutTruthTable.addView(diagramLayout);

                mButtonTruthTableSubmit = diagramLayout.findViewById(R.id.mButtonTruthTableORSubmit);
                mTextViewTruthTableAnswers = new TextView[]{
                        diagramLayout.findViewById(R.id.mTextViewTruthTableORAnswer1),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableORAnswer2),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableORAnswer3),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableORAnswer4)
                };
                break;
            }
            case QuestionCategory.XNOR:{
                Log.i(TAG, "setUpTruthTable: XNOR");

                View diagramLayout = getLayoutInflater()
                        .inflate(R.layout.truth_table_xnor_gate, mLinearLayoutTruthTable, false);
                mLinearLayoutTruthTable.removeAllViews();
                mLinearLayoutTruthTable.addView(diagramLayout);

                mButtonTruthTableSubmit = diagramLayout.findViewById(R.id.mButtonTruthTableXNORSubmit);
                mTextViewTruthTableAnswers = new TextView[]{
                        diagramLayout.findViewById(R.id.mTextViewTruthTableXNORAnswer1),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableXNORAnswer2),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableXNORAnswer3),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableXNORAnswer4)
                };
                break;
            }
            case QuestionCategory.XOR:{
                Log.i(TAG, "setUpTruthTable: XOR");

                View diagramLayout = getLayoutInflater()
                        .inflate(R.layout.truth_table_xor_gate, mLinearLayoutTruthTable, false);
                mLinearLayoutTruthTable.removeAllViews();
                mLinearLayoutTruthTable.addView(diagramLayout);

                mButtonTruthTableSubmit = diagramLayout.findViewById(R.id.mButtonTruthTableXORSubmit);
                mTextViewTruthTableAnswers = new TextView[]{
                        diagramLayout.findViewById(R.id.mTextViewTruthTableXORAnswer1),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableXORAnswer2),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableXORAnswer3),
                        diagramLayout.findViewById(R.id.mTextViewTruthTableXORAnswer4)
                };
                break;
            }
        }

        if(mTextViewTruthTableAnswers != null){
            for (final TextView mTextViewTruthTableAnswer :
                    mTextViewTruthTableAnswers) {
                mTextViewTruthTableAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mTextViewTruthTableAnswer.getText().equals("1")){
                            mTextViewTruthTableAnswer.setText("0");
                        }else {
                            mTextViewTruthTableAnswer.setText("1");
                        }
                    }
                });
            }
        }

        if(mButtonTruthTableSubmit != null){
            final TextView[] finalMTextViewTruthTableAnswers = mTextViewTruthTableAnswers;
            mButtonTruthTableSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    List<String> answer = LogicHelper.choiceReBuilder(questionModel.getA_answer());
                    int point = 0;

                    for(int i = 0; i < answer.size(); i++){
                        Log.i(TAG, "onClick: Answer: " + answer.get(i));
                        if(answer.get(i).equals(finalMTextViewTruthTableAnswers[i].getText().toString())){
                            point += 1;
                        }
                    }

                    if(point == answer.size()){
                        correct.mTextViewCorrectMessage.setText("You've got the perfect score");
                        countDownTimer.pause();
                        correct.mImageButtonCorrectOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {countDownTimer.pause();
                                remainingTime = Integer.parseInt(mTextViewGameTimer.getText().toString());
                                countDownTimer.cancel();
                                setUpGameQuestions(questionNumber + 1, points + 1);
                                correct.dialog.cancel();
                            }
                        });
                        correct.dialog.show();
                    }else {
                        correct.mTextViewCorrectMessage.setText(("You've got the " + point + " score"));
                        correct.mImageButtonCorrectOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {countDownTimer.pause();
                                remainingTime = Integer.parseInt(mTextViewGameTimer.getText().toString());
                                countDownTimer.cancel();
                                setUpGameQuestions(questionNumber + 1, points);
                                correct.dialog.cancel();
                            }
                        });
                        correct.dialog.show();
                    }
                    mLinearLayoutTruthTable.setVisibility(View.GONE);
                    totalRemainingTime += remainingTime;
                }
            });
            correct.mImageButtonCorrectOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {countDownTimer.pause();
                    remainingTime = Integer.parseInt(mTextViewGameTimer.getText().toString());
                    countDownTimer.cancel();
                    setUpGameQuestions(questionNumber + 1, points + 1);
                    correct.dialog.cancel();
                }
            });
        }


        hintLimit = 1;

        //region gameItems.mImageButtonGameHint.setOnClickListener
        gameItems.mImageButtonGameHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(hintLimit != 0){
                    int hint_ = Integer.parseInt(gameItems.mTextViewGameHintCount.getText().toString());
                    if(hint_ != 0){
                        hint_--;
                        gameItems.mTextViewGameHintCount.setText(String.valueOf(hint_));
                        try{
                            logIOSQLite.executeWriter(
                                    "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                            "SET " + Table_Users.DB_COL_HINT + " = '" + hint_ + "' " +
                                            "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                            Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                        }catch (SQLiteException ex){
                            Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                        }
                        int hidden = 1;
                        for (int i = 0; i < mLinearLayoutMultipleChoiceChoicesContainer.getChildCount(); i++) {
                            Button choice_ = (Button)mLinearLayoutMultipleChoiceChoicesContainer.getChildAt(i);
                            if(hidden != 0){
                                if(!choice_.getText().toString().trim().toLowerCase().equals(questionModel.getA_answer().trim().toLowerCase()) ||
                                        !choice_.getContentDescription().toString().trim().toLowerCase().equals(questionModel.getA_answer().trim().toLowerCase())){
                                    choice_.setVisibility(View.INVISIBLE);
                                    hidden--;
                                }
                            }
                        }

                    }
                    hintLimit--;
                }else {
                    SnackBarMessage("You can only use hint once per question.");
                }*/
                SnackBarMessage("Hint is not applicable to this question");
            }
        });
        //endregion

        //region gameItems.mImageButtonGameSlowTime.setOnClickListener
        gameItems.mImageButtonGameSlowTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int slowTime = Integer.parseInt(gameItems.mTextViewGameSlowTimeCount.getText().toString());
                if(slowTime != 0){
                    slowTime--;
                    gameItems.mTextViewGameSlowTimeCount.setText(String.valueOf(slowTime));

                    countDownTimer.pause();
                    slowTimeCDT.cancel();

                    try{
                        logIOSQLite.executeWriter(
                                "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + Table_Users.DB_COL_SLOW_TIME + " = '" + slowTime + "' " +
                                        "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                        Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                    }

                    remainingSlowTimeCDT += 6;

                    slowTimeCDT = new CountDownTimer((remainingSlowTimeCDT * 1000), (1000)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingSlowTimeCDT = (int)millisUntilFinished/1000;
                        }

                        @Override
                        public void onFinish() {
                            countDownTimer.resume();
                        }
                    };
                    //SnackBarMessage("Slow Time Item -1");
                    Toast.makeText(GameActivity.this, "Slow Time Item -1", Toast.LENGTH_SHORT).show();
                    slowTimeCDT.start();
                }
            }
        });
        //endregion

        //region gameItems.mImageButtonGameAddTime.setOnClickListener
        gameItems.mImageButtonGameAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int addTime = Integer.parseInt(gameItems.mTextViewGameAddTimeCount.getText().toString());
                if(addTime != 0){
                    addTime--;
                    gameItems.mTextViewGameAddTimeCount.setText(String.valueOf(addTime));

                    countDownTimer.cancel();

                    try{
                        logIOSQLite.executeWriter(
                                "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + Table_Users.DB_COL_ADD_TIME + " = '" + addTime + "' " +
                                        "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                        Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                    }

                    remainingTime += 5;
                    countDownTimer = new CountDownTimer((remainingTime * 1000), (1000)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingTime = (int)millisUntilFinished/1000;
                            mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
                        }

                        @Override
                        public void onFinish() {
                            timesUp.mImageButtonTimesUpOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    timesUp.dialog.cancel();
                                    setUpGameQuestions((questionNumber + 1), points);
                                }
                            });

                            timesUp.mImageButtonTimesUpRestart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            try{
                                timesUp.dialog.show();
                            }catch (Exception e){
                                Log.i(TAG, "onFinish: " + e.getMessage());
                            }
                        }
                    };
                    //SnackBarMessage("Add Time Item -1");
                    Toast.makeText(GameActivity.this, "Add Time Item -1", Toast.LENGTH_SHORT).show();
                    countDownTimer.start();
                }
            }
        });
        //endregion
        paused.mButtonPausedRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paused.dialog.cancel();
            }
        });

        //region Game buttons

        paused.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                paused.dialog.cancel();
                popupMessageTimer.pause();
                Log.i(TAG, "Line 1678: popupMessageTimer.pause();");
            }
        });



        options.mButtonOptionResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.resume();
                mLinearLayoutTruthTable.setVisibility(View.VISIBLE);
                options.dialog.cancel();
            }
        });

        paused.mButtonPausedResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.resume();
                mLinearLayoutTruthTable.setVisibility(View.VISIBLE);
                paused.dialog.cancel();
            }
        });

        paused.mButtonPausedExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                popupMessageTimer.cancel();
                finish();
            }
        });

        mImageButtonGamePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutTruthTable.setVisibility(View.GONE);
                popupMessageTimer.resume();
                Log.i(TAG, "Line 1734: popupMessageTimer.resume()");
                paused.dialog.show();
            }
        });

        mImageButtonGameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutTruthTable.setVisibility(View.GONE);
                howToPlay.mTextViewInstruction.setText(questionModel.getA_instruction());
                howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDownTimer.resume();
                        popupMessageTimer.pause();
                        Log.i(TAG, "Line 1750: popupMessageTimer.pause();");
                        mLinearLayoutTruthTable.setVisibility(View.VISIBLE);
                        howToPlay.dialog.cancel();
                    }
                });
                popupMessageTimer.resume();
                Log.i(TAG, "Line 1756: popupMessageTimer.resume()");
                howToPlay.dialog.show();
            }
        });

        mImageButtonGameOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutTruthTable.setVisibility(View.GONE);
                options.dialog.show();
            }
        });

        paused.mButtonPausedLevels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                popupMessageTimer.cancel();
                finish();
            }
        });

        //endregion

    }
    //endregion

    //region Diagram
    private void setUpDiagram(final LinearLayout mLinearLayoutDiagram,
                              final LinearLayout mLinearLayoutDynamicDiagram,
                              final Button[] mButtonMDiagramChoices,
                              final QuestionModel questionModel,
                              TextView mTextViewGameNumberOfQuestion,
                              final TextView mTextViewGameTimer,
                              final int questionNumber,
                              ImageButton mImageButtonGamePause,
                              ImageButton mImageButtonGameHelp,
                              ImageButton mImageButtonGameOption,
                              final int points,
                              final GameItems gameItems){

        final HowToPlay howToPlay = new HowToPlay(GameActivity.this);
        final Paused paused = new Paused(GameActivity.this);
        final Options options = new Options(GameActivity.this);
        final TimesUp timesUp = new TimesUp(GameActivity.this);
        final Correct correct = new Correct(GameActivity.this);
        final Wrong wrong = new Wrong(GameActivity.this);


        mTextViewGameNumberOfQuestion.setText(( (questionNumber + 1) + "/5"));

        howToPlay.mTextViewInstruction.setText(questionModel.getA_instruction());
        popupMessageTimer.resume();
        Log.i(TAG, "Line 2200: popupMessageTimer.resume()");

        countDownTimer = new CountDownTimer((Integer.parseInt(questionModel.getA_timeduration()) * 1000), (1000)) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {

                timesUp.mImageButtonTimesUpOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timesUp.dialog.cancel();
                        setUpGameQuestions((questionNumber + 1), points);
                    }
                });

                timesUp.mImageButtonTimesUpRestart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                try{
                    timesUp.dialog.show();
                }catch (Exception e){
                    Log.i(TAG, "onFinish: " + e.getMessage());
                }
            }
        };
        howToPlay.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                howToPlay.dialog.cancel();
                popupMessageTimer.pause();
                Log.i(TAG, "Line 2105: popupMessageTimer.pause();");
            }
        });

        howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                howToPlay.dialog.cancel();
                popupMessageTimer.pause();
                mLinearLayoutDiagram.setVisibility(View.VISIBLE);
                Log.i(TAG, "Line 2114: popupMessageTimer.pause();");
                countDownTimer.start();
            }
        });

        howToPlay.dialog.show();

        /*mTextViewMultipleChoiceQuestion.setText(questionModel.getA_question());
        if(questionModel.getA_question().toLowerCase().isEmpty()){
            Log.i(TAG, "setUpMultipleChoice: null question" );
            return;
        }*/
        int diagramView = 0;

        switch (questionModel.getA_category()){
            case QuestionCategory.AND:{
                Log.i(TAG, "setUpDiagram: AND");
                diagramView = R.layout.diagram_and_gate;
                break;
            }
            case QuestionCategory.NOT:{
                Log.i(TAG, "setUpDiagram: NOT");
                diagramView = R.layout.diagram_not_gate;
                break;
            }
            case QuestionCategory.OR:{
                Log.i(TAG, "setUpDiagram: OR");
                diagramView = R.layout.diagram_or_gate;
                break;
            }
            case QuestionCategory.XNOR:{
                Log.i(TAG, "setUpDiagram: XNOR");
                diagramView = R.layout.diagram_xnor_gate;
                break;
            }
            case QuestionCategory.XOR:{
                Log.i(TAG, "setUpDiagram: XOR");
                diagramView = R.layout.diagram_xor_gate;
                break;
            }
        }

        View diagramLayout = getLayoutInflater()
                .inflate(diagramView, mLinearLayoutDynamicDiagram, false);

        mLinearLayoutDynamicDiagram.addView(diagramLayout);

        final List<String> choices = LogicHelper.choiceReBuilder(questionModel.getA_choices());
        final List<Integer> numbers = LogicHelper.randomNumbers(1, choices.size());

        List<String> tmpChoices = new ArrayList<>();
        Log.i(TAG, "setUpDiagram: Choices");
        for (int i = 0; i < choices.size(); i++) {
            tmpChoices.add(choices.get(numbers.get(i)-1));
            Log.i(TAG, "setUpDiagram: " + choices.get(numbers.get(i)-1));
        }
        for (Button mButtonMultipleChoiceChoice : mButtonMultipleChoiceChoices){
            mButtonMultipleChoiceChoice.setText("");
            mButtonMultipleChoiceChoice.setContentDescription("");
            mButtonMultipleChoiceChoice.setBackground(null);
            mButtonMultipleChoiceChoice.setVisibility(View.GONE);
        }
        for(int i = 0; i < tmpChoices.size(); i++){
            Log.i(TAG, "setUpDiagram: " + tmpChoices.get(i).toLowerCase());
            try{
                Log.i(TAG, "setUpDiagram: " + tmpChoices.get(i).toLowerCase());
                mButtonMDiagramChoices[i].setText("");
                mButtonMDiagramChoices[i].setContentDescription(tmpChoices.get(i));
                mButtonMDiagramChoices[i].setBackground(GetDrawableResource(GameActivity.this, tmpChoices.get(i).toLowerCase()));
            }
            catch (Exception e){
                mButtonMDiagramChoices[i].setBackgroundResource(R.drawable.bg_choices);
                mButtonMDiagramChoices[i].setText(Html.fromHtml(tmpChoices.get(i)));
                mButtonMDiagramChoices[i].setContentDescription(tmpChoices.get(i));
                Log.i(TAG, "setUpDiagram: " + remainingTime );
                Log.i(TAG, "setUpDiagram: " + e.getMessage());
            }
            final int finalI = i;

            mButtonMDiagramChoices[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String answer = Html.fromHtml(questionModel.getA_answer()).toString();
                    if(mButtonMDiagramChoices[finalI].getContentDescription().toString().toLowerCase().equals(questionModel.getA_answer().toLowerCase()) ||
                            mButtonMDiagramChoices[finalI].getText().toString().toLowerCase().equals(answer)){
                        countDownTimer.pause();
                        remainingTime = Integer.parseInt(mTextViewGameTimer.getText().toString());
                        countDownTimer.cancel();
                        //Toast.makeText(GameActivity.this, "Correct", Toast.LENGTH_SHORT).show();
                        correct.dialog.show();
                        correct.mImageButtonCorrectOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setUpGameQuestions(questionNumber + 1, points + 1);
                                correct.dialog.cancel();
                            }
                        });
                    }else {
                        countDownTimer.pause();
                        remainingTime = Integer.parseInt(mTextViewGameTimer.getText().toString());
                        countDownTimer.cancel();
                        wrong.mImageButtonWrongOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                wrong.dialog.cancel();
                                setUpGameQuestions(questionNumber + 1, points);
                            }
                        });
                        wrong.dialog.show();
                        //Toast.makeText(GameActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
                    }
                    mLinearLayoutDiagram.setVisibility(View.GONE);
                    totalRemainingTime += remainingTime;
                }
            });
        }
        Log.i(TAG, "setUpDiagram: getA_timeduration = " + Integer.parseInt(questionModel.getA_timeduration()));

        hintLimit = 1;

        //region gameItems.mImageButtonGameHint.setOnClickListener
        gameItems.mImageButtonGameHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hintLimit != 0){
                    int hint_ = Integer.parseInt(gameItems.mTextViewGameHintCount.getText().toString());
                    if(hint_ != 0){
                        hint_--;
                        gameItems.mTextViewGameHintCount.setText(String.valueOf(hint_));
                        try{
                            logIOSQLite.executeWriter(
                                    "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                            "SET " + Table_Users.DB_COL_HINT + " = '" + hint_ + "' " +
                                            "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                            Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                        }catch (SQLiteException ex){
                            Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                        }
                        int hidden = 1;
                        for (int i = 0; i < mButtonMDiagramChoices.length; i++) {
                            Button choice_ = mButtonMDiagramChoices[i];
                            if(hidden != 0){
                                if(!choice_.getText().toString().trim().toLowerCase().equals(questionModel.getA_answer().trim().toLowerCase()) ||
                                        !choice_.getContentDescription().toString().trim().toLowerCase().equals(questionModel.getA_answer().trim().toLowerCase())){
                                    choice_.setVisibility(View.INVISIBLE);
                                    hidden--;
                                }
                            }
                        }

                    }
                    //SnackBarMessage("Hint Item -1");
                    Toast.makeText(GameActivity.this, "Hint Item -1", Toast.LENGTH_SHORT).show();
                    hintLimit--;
                }else {
                    SnackBarMessage("You can only use hint once per question.");
                }
            }
        });
        //endregion

        //region gameItems.mImageButtonGameSlowTime.setOnClickListener
        gameItems.mImageButtonGameSlowTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int slowTime = Integer.parseInt(gameItems.mTextViewGameSlowTimeCount.getText().toString());
                if(slowTime != 0){
                    slowTime--;
                    gameItems.mTextViewGameSlowTimeCount.setText(String.valueOf(slowTime));

                    countDownTimer.pause();
                    slowTimeCDT.cancel();

                    try{
                        logIOSQLite.executeWriter(
                                "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + Table_Users.DB_COL_SLOW_TIME + " = '" + slowTime + "' " +
                                        "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                        Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                    }

                    remainingSlowTimeCDT += 6;

                    slowTimeCDT = new CountDownTimer((remainingSlowTimeCDT * 1000), (1000)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingSlowTimeCDT = (int)millisUntilFinished/1000;
                        }

                        @Override
                        public void onFinish() {
                            countDownTimer.resume();
                        }
                    };
                    //SnackBarMessage("Slow Time Item -1");
                    Toast.makeText(GameActivity.this, "Slow Time Item -1", Toast.LENGTH_SHORT).show();
                    slowTimeCDT.start();
                }
            }
        });
        //endregion

        //region gameItems.mImageButtonGameAddTime.setOnClickListener
        gameItems.mImageButtonGameAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int addTime = Integer.parseInt(gameItems.mTextViewGameAddTimeCount.getText().toString());
                if(addTime != 0){
                    addTime--;
                    gameItems.mTextViewGameAddTimeCount.setText(String.valueOf(addTime));

                    countDownTimer.cancel();

                    try{
                        logIOSQLite.executeWriter(
                                "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + Table_Users.DB_COL_ADD_TIME + " = '" + addTime + "' " +
                                        "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                        Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                    }

                    remainingTime += 5;
                    countDownTimer = new CountDownTimer((remainingTime * 1000), (1000)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingTime = (int)millisUntilFinished/1000;
                            mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
                        }

                        @Override
                        public void onFinish() {
                            timesUp.mImageButtonTimesUpOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    timesUp.dialog.cancel();
                                    setUpGameQuestions((questionNumber + 1), points);
                                }
                            });

                            timesUp.mImageButtonTimesUpRestart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            try{
                                timesUp.dialog.show();
                            }catch (Exception e){
                                Log.i(TAG, "onFinish: " + e.getMessage());
                            }
                        }
                    };
                    //SnackBarMessage("Add Time Item -1");
                    Toast.makeText(GameActivity.this, "Add Time Item -1", Toast.LENGTH_SHORT).show();
                    countDownTimer.start();
                }
            }
        });
        //endregion

        //region Game buttons

        paused.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                paused.dialog.cancel();
                popupMessageTimer.pause();
                Log.i(TAG, "Line 2096: popupMessageTimer.pause();");
            }
        });

        options.mButtonOptionResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.resume();
                mLinearLayoutDiagram.setVisibility(View.VISIBLE);
                options.dialog.cancel();
            }
        });

        paused.mButtonPausedResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.resume();
                mLinearLayoutDiagram.setVisibility(View.VISIBLE);
                paused.dialog.cancel();
            }
        });

        paused.mButtonPausedExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                popupMessageTimer.cancel();
                finish();
            }
        });

        mImageButtonGamePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutDiagram.setVisibility(View.GONE);
                popupMessageTimer.resume();
                Log.i(TAG, "Line 2152: popupMessageTimer.resume()");
                paused.dialog.show();
            }
        });

        mImageButtonGameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutDiagram.setVisibility(View.GONE);
                howToPlay.mTextViewInstruction.setText(questionModel.getA_instruction());
                howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDownTimer.resume();
                        popupMessageTimer.pause();
                        Log.i(TAG, "Line 2168: popupMessageTimer.pause();");
                        mLinearLayoutDiagram.setVisibility(View.VISIBLE);
                        howToPlay.dialog.cancel();
                    }
                });
                popupMessageTimer.resume();
                Log.i(TAG, "Line 2174: popupMessageTimer.resume()");
                howToPlay.dialog.show();
            }
        });

        mImageButtonGameOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutDiagram.setVisibility(View.GONE);
                options.dialog.show();
            }
        });

        paused.mButtonPausedLevels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                popupMessageTimer.cancel();
                finish();
            }
        });

        //endregion

    }
    //endregion

    //region Multiple choice
    private void setUpMultipleChoice(final LinearLayout mLinearLayoutMultipleChoice,
                                     TextView mTextViewMultipleChoiceQuestion,
                                     final Button[] mButtonMultipleChoiceChoices,
                                     final QuestionModel questionModel,
                                     TextView mTextViewGameNumberOfQuestion,
                                     final TextView mTextViewGameTimer,
                                     final int questionNumber,
                                     ImageButton mImageButtonGamePause,
                                     ImageButton mImageButtonGameHelp,
                                     ImageButton mImageButtonGameOption,
                                     final int points,
                                     final GameItems gameItems,
                                     final LinearLayout mLinearLayoutMultipleChoiceChoicesContainer){

        final HowToPlay howToPlay = new HowToPlay(GameActivity.this);
        final Paused paused = new Paused(GameActivity.this);
        final Options options = new Options(GameActivity.this);
        final TimesUp timesUp = new TimesUp(GameActivity.this);
        final Correct correct = new Correct(GameActivity.this);

        mTextViewGameNumberOfQuestion.setText(( (questionNumber + 1) + "/5"));
        //mLinearLayoutMultipleChoice.setVisibility(View.VISIBLE);
        howToPlay.mTextViewInstruction.setText(questionModel.getA_instruction());
        popupMessageTimer.resume();
        Log.i(TAG, "Line 2628: popupMessageTimer.resume()");
        final Wrong wrong = new Wrong(GameActivity.this);
        countDownTimer = new CountDownTimer((Integer.parseInt(questionModel.getA_timeduration()) * 1000), (1000)) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = (int) (millisUntilFinished/1000);
                mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {

                timesUp.mImageButtonTimesUpOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timesUp.dialog.cancel();
                        setUpGameQuestions((questionNumber + 1), points);

                    }
                });
                timesUp.mImageButtonTimesUpRestart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                try{
                    timesUp.dialog.show();
                }catch (Exception e){
                    Log.i(TAG, "onFinish: " + e.getMessage());
                }
            }
        };
        howToPlay.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                howToPlay.dialog.cancel();
                popupMessageTimer.pause();
                Log.i(TAG, "Line 2532: popupMessageTimer.pause();");
            }
        });

        howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                howToPlay.dialog.cancel();
                popupMessageTimer.pause();
                mLinearLayoutMultipleChoice.setVisibility(View.VISIBLE);
                Log.i(TAG, "Line 2541: popupMessageTimer.pause();");
                countDownTimer.start();
            }
        });
        howToPlay.dialog.show();

        mTextViewMultipleChoiceQuestion.setText(questionModel.getA_question());

        if(questionModel.getA_question().toLowerCase().isEmpty()){
            Log.i(TAG, "setUpMultipleChoice: null question" );
            setUpGameQuestions(questionNumber + 1, points);
            return;
        }
        List<String> choices = LogicHelper.choiceReBuilder(questionModel.getA_choices());
        final List<Integer> numbers = LogicHelper.randomNumbers(1, choices.size());

        List<String> tmpChoices = new ArrayList<>();
        Log.i(TAG, "setUpMultipleChoice: Choices");
        for (int i = 0; i < choices.size(); i++) {
            tmpChoices.add(choices.get(numbers.get(i)-1));
            Log.i(TAG, "setUpMultipleChoice: " + choices.get(numbers.get(i)-1));
        }

        for (Button mButtonMultipleChoiceChoice : mButtonMultipleChoiceChoices){
            mButtonMultipleChoiceChoice.setText("");
            mButtonMultipleChoiceChoice.setContentDescription("");
            mButtonMultipleChoiceChoice.setBackgroundResource(R.drawable.bg_choices);
            mButtonMultipleChoiceChoice.setVisibility(View.GONE);
        }

        for(int i = 0; i < tmpChoices.size(); i++){
            mButtonMultipleChoiceChoices[i].setVisibility(View.VISIBLE);
            Log.i(TAG, "setUpMultipleChoice: " + tmpChoices.get(i).toLowerCase());
            mButtonMultipleChoiceChoices[i].setBackground(null);
            try{
                mButtonMultipleChoiceChoices[i].setText("");
                mButtonMultipleChoiceChoices[i].setContentDescription(tmpChoices.get(i));
                mButtonMultipleChoiceChoices[i].setBackground(GetDrawableResource(GameActivity.this, tmpChoices.get(i).toLowerCase()));
            }
            catch (Exception e){
                mButtonMultipleChoiceChoices[i].setBackgroundResource(R.drawable.bg_choices);
                mButtonMultipleChoiceChoices[i].setText(Html.fromHtml(tmpChoices.get(i)));
                countDownTimer.pause();
                countDownTimer.cancel();
                Log.i(TAG, "setUpMultipleChoice: " + e.getMessage());
            }
            final int finalI = i;

            mButtonMultipleChoiceChoices[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mButtonMultipleChoiceChoices[finalI].getContentDescription().toString().toLowerCase().equals(questionModel.getA_answer().toLowerCase()) ||
                            mButtonMultipleChoiceChoices[finalI].getText().toString().toLowerCase().equals(questionModel.getA_answer().toLowerCase())){
                        countDownTimer.pause();
                        remainingTime = Integer.parseInt(mTextViewGameTimer.getText().toString());
                        countDownTimer.cancel();
                        //Toast.makeText(GameActivity.this, "Correct", Toast.LENGTH_SHORT).show();
                        correct.dialog.show();
                        correct.mImageButtonCorrectOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setUpGameQuestions(questionNumber + 1, points + 1);
                                correct.dialog.cancel();
                            }
                        });
                    }else {
                        countDownTimer.pause();
                        remainingTime = Integer.parseInt(mTextViewGameTimer.getText().toString());
                        countDownTimer.cancel();
                        wrong.mImageButtonWrongOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                wrong.dialog.cancel();
                                setUpGameQuestions(questionNumber + 1, points);
                            }
                        });
                        wrong.dialog.show();
                    }
                    mLinearLayoutMultipleChoice.setVisibility(View.GONE);
                    totalRemainingTime += remainingTime;
                }
            });
        }
        Log.i(TAG, "setUpMultipleChoice: getA_timeduration = " + Integer.parseInt(questionModel.getA_timeduration()));

        hintLimit = 1;

        //region gameItems.mImageButtonGameHint.setOnClickListener
        gameItems.mImageButtonGameHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hintLimit != 0){
                    int hint_ = Integer.parseInt(gameItems.mTextViewGameHintCount.getText().toString());
                    if(hint_ != 0){
                        hint_--;
                        gameItems.mTextViewGameHintCount.setText(String.valueOf(hint_));
                        try{
                            logIOSQLite.executeWriter(
                                    "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                            "SET " + Table_Users.DB_COL_HINT + " = '" + hint_ + "' " +
                                            "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                            Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                        }catch (SQLiteException ex){
                            Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                        }
                        int hidden = 1;
                        for (int i = 0; i < mButtonMultipleChoiceChoices.length; i++) {
                            Button choice_ = mButtonMultipleChoiceChoices[i];
                            if(hidden != 0){
                                if(!choice_.getText().toString().trim().equals(questionModel.getA_answer().trim()) ||
                                        !choice_.getContentDescription().toString().trim().equals(questionModel.getA_answer().trim())){
                                    choice_.setVisibility(View.INVISIBLE);
                                    hidden--;
                                }
                            }
                        }

                    }
                    hintLimit--;
                    Toast.makeText(GameActivity.this, "Hint Item -1", Toast.LENGTH_SHORT).show();
                    //SnackBarMessage("Hint Item -1");
                }else {
                    SnackBarMessage("You can only use hint once per question.");
                }
            }
        });
        //endregion

        //region gameItems.mImageButtonGameSlowTime.setOnClickListener
        gameItems.mImageButtonGameSlowTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int slowTime = Integer.parseInt(gameItems.mTextViewGameSlowTimeCount.getText().toString());
                if(slowTime != 0){
                    slowTime--;
                    gameItems.mTextViewGameSlowTimeCount.setText(String.valueOf(slowTime));

                    countDownTimer.pause();
                    slowTimeCDT.cancel();

                    try{
                        logIOSQLite.executeWriter(
                                "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + Table_Users.DB_COL_SLOW_TIME + " = '" + slowTime + "' " +
                                        "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                        Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                    }

                    remainingSlowTimeCDT += 6;

                    /*countDownTimer = new CountDownTimer((remainingTimeInQuestion * 1000), (1000)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingTimeInQuestion = (int)millisUntilFinished/1000;
                            mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
                        }

                        @Override
                        public void onFinish() {
                            timesUp.mImageButtonTimesUpOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    timesUp.dialog.cancel();
                                    setUpGameQuestions((questionNumber + 1), points);
                                }
                            });

                            timesUp.mImageButtonTimesUpRestart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            try{
                                timesUp.dialog.show();
                            }catch (Exception e){
                                Log.i(TAG, "onFinish: " + e.getMessage());
                            }
                        }
                    };*/

                    slowTimeCDT = new CountDownTimer((remainingSlowTimeCDT * 1000), (1000)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingSlowTimeCDT = (int)millisUntilFinished/1000;
//                            countDownTimer.pause();
                        }

                        @Override
                        public void onFinish() {
//                            countDownTimer.resume();

                            countDownTimer.resume();
                        }
                    };

                    //SnackBarMessage("Slow Time Item -1");
                    Toast.makeText(GameActivity.this, "Slow Time Item -1", Toast.LENGTH_SHORT).show();
                    slowTimeCDT.start();
                }
            }
        });
        //endregion

        //region gameItems.mImageButtonGameAddTime.setOnClickListener
        gameItems.mImageButtonGameAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int addTime = Integer.parseInt(gameItems.mTextViewGameAddTimeCount.getText().toString());
                if(addTime != 0){
                    addTime--;
                    gameItems.mTextViewGameAddTimeCount.setText(String.valueOf(addTime));

                    countDownTimer.cancel();

                    try{
                        logIOSQLite.executeWriter(
                                "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + Table_Users.DB_COL_ADD_TIME + " = '" + addTime + "' " +
                                        "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                        Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                    }

                    remainingTime += 5;
                    countDownTimer = new CountDownTimer((remainingTime * 1000), (1000)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingTime = (int)millisUntilFinished/1000;
                            mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
                        }

                        @Override
                        public void onFinish() {
                            timesUp.mImageButtonTimesUpOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    timesUp.dialog.cancel();
                                    setUpGameQuestions((questionNumber + 1), points);
                                }
                            });

                            timesUp.mImageButtonTimesUpRestart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            try{
                                timesUp.dialog.show();
                            }catch (Exception e){
                                Log.i(TAG, "onFinish: " + e.getMessage());
                            }
                        }
                    };
                    //SnackBarMessage("Add Time Item -1");
                    Toast.makeText(GameActivity.this, "Add Time Item -1", Toast.LENGTH_SHORT).show();
                    countDownTimer.start();
                }
            }
        });
        //endregion

        //region Game buttons

        paused.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                paused.dialog.cancel();
                popupMessageTimer.pause();
                Log.i(TAG, "Line 2523: popupMessageTimer.pause();");
            }
        });



        options.mButtonOptionResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.resume();
                mLinearLayoutMultipleChoice.setVisibility(View.VISIBLE);
                options.dialog.cancel();
            }
        });

        paused.mButtonPausedResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.resume();
                mLinearLayoutMultipleChoice.setVisibility(View.VISIBLE);
                paused.dialog.cancel();
            }
        });

        paused.mButtonPausedExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                popupMessageTimer.cancel();
                finish();
            }
        });

        mImageButtonGamePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutMultipleChoice.setVisibility(View.GONE);
                popupMessageTimer.resume();
                Log.i(TAG, "Line 2579: popupMessageTimer.resume()");
                paused.dialog.show();
            }
        });

        mImageButtonGameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutMultipleChoice.setVisibility(View.GONE);
                howToPlay.mTextViewInstruction.setText(questionModel.getA_instruction());
                howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDownTimer.resume();
                        popupMessageTimer.pause();
                        Log.i(TAG, "Line 2897: popupMessageTimer.pause();");
                        mLinearLayoutMultipleChoice.setVisibility(View.VISIBLE);
                        howToPlay.dialog.cancel();
                    }
                });
                popupMessageTimer.resume();
                Log.i(TAG, "Line 2601: popupMessageTimer.resume()");
                howToPlay.dialog.show();
            }
        });

        mImageButtonGameOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutMultipleChoice.setVisibility(View.GONE);
                options.dialog.show();
            }
        });

        paused.mButtonPausedLevels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                popupMessageTimer.cancel();
                finish();
            }
        });

        //endregion


    }
    //endregion

    //region Identification
    private void setUpIdentification(final LinearLayout mLinearLayoutIdentification,
                                     TextView mTextViewIdentificationQuestion,
                                     final EditText mEditTextIdentificationAnswer,
                                     ImageButton mImageButtonIdentificationConfirmAnswer,
                                     final QuestionModel questionModel,
                                     TextView mTextViewGameNumberOfQuestion,
                                     final TextView mTextViewGameTimer,
                                     final int questionNumber,
                                     ImageButton mImageButtonGamePause,
                                     ImageButton mImageButtonGameHelp,
                                     ImageButton mImageButtonGameOption,
                                     final int points,
                                     final GameItems gameItems){

        final HowToPlay howToPlay = new HowToPlay(GameActivity.this);
        final Paused paused = new Paused(GameActivity.this);
        final Options options = new Options(GameActivity.this);
        final TimesUp timesUp = new TimesUp(GameActivity.this);
        final Correct correct = new Correct(GameActivity.this);
        final Wrong wrong = new Wrong(GameActivity.this);
        mTextViewGameNumberOfQuestion.setText(( (questionNumber + 1) + "/5"));
        mEditTextIdentificationAnswer.setText("");

        howToPlay.mTextViewInstruction.setText(questionModel.getA_instruction());
        popupMessageTimer.resume();
        Log.i(TAG, "Line 2985: popupMessageTimer.resume();");
        countDownTimer = new CountDownTimer((Integer.parseInt(questionModel.getA_timeduration()) * 1000), (1000)) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = (int) (millisUntilFinished/1000);
                mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {

                timesUp.mImageButtonTimesUpOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timesUp.dialog.cancel();
                        setUpGameQuestions((questionNumber + 1), points);
                    }
                });
                timesUp.mImageButtonTimesUpRestart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                try{
                    timesUp.dialog.show();
                }catch (Exception e){
                    Log.i(TAG, "onFinish: " + e.getMessage());
                }
            }
        };
        howToPlay.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                howToPlay.dialog.cancel();
                popupMessageTimer.pause();
                Log.i(TAG, "Line 2888: popupMessageTimer.pause();");
            }
        });

        howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                howToPlay.dialog.cancel();
                popupMessageTimer.pause();
                mLinearLayoutIdentification.setVisibility(View.VISIBLE);
                Log.i(TAG, "Line 2897: popupMessageTimer.pause();");
                countDownTimer.start();

            }
        });
        howToPlay.dialog.show();
        mTextViewIdentificationQuestion.setText(questionModel.getA_question());
        if(questionModel.getA_question().toLowerCase().isEmpty()){
            Log.i(TAG, "setUpIdentification: null question" );
            return;
        }

        mImageButtonIdentificationConfirmAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = mEditTextIdentificationAnswer.getText().toString().toLowerCase();
                List<String> answers = LogicHelper.choiceReBuilder(questionModel.getA_answer());
                int __point = 0;
                for(int i = 0; i < answers.size(); i++){
                    if(answer.contains(answers.get(i).trim().toLowerCase()) && answer.length() >= answers.get(i).length() ){
                        __point++;
                         Log.i(TAG, "" + i + ": " + answer + " has " + answers.get(i));
                    }
                }
                Log.i(TAG, "__point: " + __point);
                if(__point != 0){
                    //region correct answer
                    countDownTimer.pause();
                    countDownTimer.cancel();
                    correct.dialog.show();
                    correct.mImageButtonCorrectOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            correct.dialog.cancel();
                            setUpGameQuestions(questionNumber + 1, points + 1);
                        }
                    });
                    //Toast.makeText(GameActivity.this, "Correct", Toast.LENGTH_SHORT).show();
                    //endregion
                }
                else {
                    countDownTimer.pause();
                    remainingTime = Integer.parseInt(mTextViewGameTimer.getText().toString());
                    countDownTimer.cancel();
                    //Toast.makeText(GameActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
                    wrong.mImageButtonWrongOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            wrong.dialog.cancel();
                            setUpGameQuestions(questionNumber + 1, points);
                        }
                    });
                    wrong.dialog.show();
                }
                mLinearLayoutIdentification.setVisibility(View.GONE);
                totalRemainingTime += remainingTime;
            }
        });

        Log.i(TAG, "setUpIdentification: getA_timeduration = " + Integer.parseInt(questionModel.getA_timeduration()));

        hintLimit = 1;

        //region gameItems.mImageButtonGameHint.setOnClickListener
        gameItems.mImageButtonGameHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(hintLimit != 0){
                    int hint_ = Integer.parseInt(gameItems.mTextViewGameHintCount.getText().toString());
                    if(hint_ != 0){
                        hint_--;
                        gameItems.mTextViewGameHintCount.setText(String.valueOf(hint_));
                        try{
                            logIOSQLite.executeWriter(
                                    "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                            "SET " + Table_Users.DB_COL_HINT + " = '" + hint_ + "' " +
                                            "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                            Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                        }catch (SQLiteException ex){
                            Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                        }
                        int hidden = 1;
                        for (int i = 0; i < mLinearLayoutMultipleChoiceChoicesContainer.getChildCount(); i++) {
                            Button choice_ = (Button)mLinearLayoutMultipleChoiceChoicesContainer.getChildAt(i);
                            if(hidden != 0){
                                if(!choice_.getText().toString().trim().toLowerCase().equals(questionModel.getA_answer().trim().toLowerCase()) ||
                                        !choice_.getContentDescription().toString().trim().toLowerCase().equals(questionModel.getA_answer().trim().toLowerCase())){
                                    choice_.setVisibility(View.INVISIBLE);
                                    hidden--;
                                }
                            }
                        }

                    }
                    hintLimit--;
                }else {
                    SnackBarMessage("You can only use hint once per question.");
                }*/
                SnackBarMessage("Hint is not applicable to this question");
            }
        });
        //endregion

        //region gameItems.mImageButtonGameSlowTime.setOnClickListener
        gameItems.mImageButtonGameSlowTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int slowTime = Integer.parseInt(gameItems.mTextViewGameSlowTimeCount.getText().toString());
                if(slowTime != 0){
                    slowTime--;
                    gameItems.mTextViewGameSlowTimeCount.setText(String.valueOf(slowTime));

                    countDownTimer.pause();
                    slowTimeCDT.cancel();

                    try{
                        logIOSQLite.executeWriter(
                                "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + Table_Users.DB_COL_SLOW_TIME + " = '" + slowTime + "' " +
                                        "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                        Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                    }

                    remainingSlowTimeCDT += 6;

                    slowTimeCDT = new CountDownTimer((remainingSlowTimeCDT * 1000), (1000)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingSlowTimeCDT = (int)millisUntilFinished/1000;
                        }

                        @Override
                        public void onFinish() {
                            countDownTimer.resume();
                        }
                    };
                    //SnackBarMessage("Slow Time Item -1");
                    Toast.makeText(GameActivity.this, "Slow Time Item -1", Toast.LENGTH_SHORT).show();
                    slowTimeCDT.start();
                }
            }
        });
        //endregion

        //region gameItems.mImageButtonGameAddTime.setOnClickListener
        gameItems.mImageButtonGameAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int addTime = Integer.parseInt(gameItems.mTextViewGameAddTimeCount.getText().toString());
                if(addTime != 0){
                    addTime--;
                    gameItems.mTextViewGameAddTimeCount.setText(String.valueOf(addTime));

                    countDownTimer.cancel();

                    try{
                        logIOSQLite.executeWriter(
                                "UPDATE " + Table_Users.DB_TABLE_NAME + " " +
                                        "SET " + Table_Users.DB_COL_ADD_TIME + " = '" + addTime + "' " +
                                        "WHERE " + Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                        Log.i(TAG, "logIOSQLite.executeWriter: Time updated!");
                    }catch (SQLiteException ex){
                        Log.i(TAG, "logIOSQLite.executeWriter: " + ex.getMessage());
                    }

                    remainingTime += 5;
                    countDownTimer = new CountDownTimer((remainingTime * 1000), (1000)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingTime = (int)millisUntilFinished/1000;
                            mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
                        }

                        @Override
                        public void onFinish() {
                            timesUp.mImageButtonTimesUpOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    timesUp.dialog.cancel();
                                    setUpGameQuestions((questionNumber + 1), points);
                                }
                            });

                            timesUp.mImageButtonTimesUpRestart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            try{
                                timesUp.dialog.show();
                            }catch (Exception e){
                                Log.i(TAG, "onFinish: " + e.getMessage());
                            }
                        }
                    };
                    //SnackBarMessage("Add Time Item -1");
                    Toast.makeText(GameActivity.this, "Add Time Item -1", Toast.LENGTH_SHORT).show();
                    countDownTimer.start();
                }
            }
        });
        //endregion
        //region Game buttons

        paused.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                paused.dialog.cancel();
                popupMessageTimer.pause();
                Log.i(TAG, "Line 2879: popupMessageTimer.pause();");
            }
        });



        options.mButtonOptionResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.resume();
                mLinearLayoutIdentification.setVisibility(View.VISIBLE);
                options.dialog.cancel();
            }
        });

        paused.mButtonPausedResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.resume();
                mLinearLayoutIdentification.setVisibility(View.VISIBLE);
                paused.dialog.cancel();
            }
        });

        paused.mButtonPausedExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                popupMessageTimer.cancel();
                finish();
            }
        });

        mImageButtonGamePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutIdentification.setVisibility(View.GONE);
                popupMessageTimer.resume();
                Log.i(TAG, "Line 2935: popupMessageTimer.resume()");
                paused.dialog.show();
            }
        });

        mImageButtonGameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutIdentification.setVisibility(View.GONE);
                popupMessageTimer.resume();
                Log.i(TAG, "Line 2946: popupMessageTimer.pause();");
                howToPlay.mTextViewInstruction.setText(questionModel.getA_instruction());
                howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDownTimer.resume();
                        popupMessageTimer.pause();
                        Log.i(TAG, "Line 2953: popupMessageTimer.pause();");
                        mLinearLayoutIdentification.setVisibility(View.VISIBLE);
                        howToPlay.dialog.cancel();
                    }
                });
                popupMessageTimer.resume();
                Log.i(TAG, "Line 2959: popupMessageTimer.resume()");
                howToPlay.dialog.show();
            }
        });

        mImageButtonGameOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutIdentification.setVisibility(View.GONE);
                options.dialog.show();
            }
        });

        paused.mButtonPausedLevels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                popupMessageTimer.cancel();
                finish();
            }
        });

        //endregion
    }
    //endregion
}
