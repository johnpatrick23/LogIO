package com.oneclique.logio;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oneclique.logio.LogIOSQLite.LogIOSQLite;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.QuestionModel;
import com.oneclique.logio.LogIOSQLite.SQLITE_VARIABLES;
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

        questionModelList = new ArrayList<>();
        sequenceOfQuestions = new ArrayList<>();
        logIOSQLite = new LogIOSQLite(GameActivity.this);
        logIOSQLite.createDatabase();

        playerInGameModel = new PlayerInGameModel();

        final Intent intent = getIntent();

        playerInGameModel = (PlayerInGameModel) Objects.requireNonNull(intent.getExtras()).getSerializable(PLAYER_IN_GAME_MODEL);

        selectedLevel = Integer.parseInt(playerInGameModel.getSelectedLevel());

        Log.i(TAG, "onCreate: playerInGameModel.getUsername() = " + playerInGameModel.getUsername());
        Log.i(TAG, "onCreate: playerInGameModel.getSelectedLevel() = " + playerInGameModel.getSelectedLevel());

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

        //region Get All questions
        Cursor cursor = logIOSQLite.executeReader("select * from " + SQLITE_VARIABLES.Table_Questions.DB_TABLE_NAME + " " +
                "where " + SQLITE_VARIABLES.Table_Questions.DB_COL_LEVEL + " = '" + (selectedLevel + 1) + "';");
        if(cursor.getCount() != 0){
            questionModelList = new ArrayList<>();
            while(cursor.moveToNext()){
                QuestionModel questionModel = new QuestionModel();
                questionModel.setA_id(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_ID)));
                questionModel.setA_level(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_LEVEL)));
                questionModel.setA_questiontype(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_QUESTION_TYPE)));
                questionModel.setA_question(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_QUESTION)));
                questionModel.setA_choices(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_CHOICES)));
                questionModel.setA_answer(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_ANSWER)));
                questionModel.setA_timeduration(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_TIME_DURATION)));
                questionModel.setA_category(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_CATEGORY)));
                questionModel.setA_instruction(cursor.getString(cursor.getColumnIndex(SQLITE_VARIABLES.Table_Questions.DB_COL_INSTRUCTION)));
                questionModelList.add(questionModel);
            }
            //endregion

        /*
        Log.i(TAG, "getA_id: " + questionListModel.getQuestionListModelList().get(selectedLevel).get(0).getA_id());
        Log.i(TAG, "getA_level: " + questionListModel.getQuestionListModelList().get(selectedLevel).get(0).getA_level());
        Log.i(TAG, "getA_questiontype: " + questionListModel.getQuestionListModelList().get(selectedLevel).get(0).getA_questiontype());
        Log.i(TAG, "getA_question: " + questionListModel.getQuestionListModelList().get(selectedLevel).get(0).getA_question());
        Log.i(TAG, "getA_choices: " + questionListModel.getQuestionListModelList().get(selectedLevel).get(0).getA_choices());
        Log.i(TAG, "getA_category: " + questionListModel.getQuestionListModelList().get(selectedLevel).get(0).getA_category());
        Log.i(TAG, "getA_instruction: " + questionListModel.getQuestionListModelList().get(selectedLevel).get(0).getA_instruction());
        Log.i(TAG, "getA_timeduration: " + questionListModel.getQuestionListModelList().get(selectedLevel).get(0).getA_timeduration());
        */

            //region Randomize the question
            sequenceOfQuestions = LogicHelper.randomNumbers(1, questionModelList.size());
            List<QuestionModel> tmpQuestionModelList = new ArrayList<>();
            for (int i = 0; i < questionModelList.size(); i++) {
                tmpQuestionModelList.add(questionModelList.get(sequenceOfQuestions.get(i)-1));
            }
            questionModelList = tmpQuestionModelList;
            setUpGameQuestions(questionNumber, points);
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
    int points = 0;

    private void setUpGameQuestions(int questionNumber, int points){
        Log.i(TAG, "points: " + points);

        if(questionNumber == questionModelList.size()){
            Toast.makeText(GameActivity.this, "Done!", Toast.LENGTH_LONG).show();
            int stars = 0;
            int lastStar = 0;
            String starsToFill = "";
            switch (points){
                case 5: case 4: { stars = 3; break; }
                case 3: { stars = 2; break; }
                case 2: case 1: { stars = 1; break; }
                default: { break; }
            }
            switch (selectedLevel + 1){
                case 1:{
                    starsToFill = SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_1_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_1_stars());
                    break;
                }
                case 2:{
                    starsToFill = SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_2_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_2_stars());
                    break;
                }
                case 3:{
                    starsToFill = SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_3_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_3_stars());
                    break;
                }
                case 4:{
                    starsToFill = SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_4_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_4_stars());
                    break;
                }
                case 5:{
                    starsToFill = SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_5_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_5_stars());
                    break;
                }
                case 6:{
                    starsToFill = SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_6_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_6_stars());
                    break;
                }
                case 7:{
                    starsToFill = SQLITE_VARIABLES.Table_Users.DB_COL_LEVEL_7_STARS;
                    lastStar = Integer.parseInt(playerInGameModel.getUsersModel().getA_level_7_stars());
                    break;
                }
                default: break;
            }
            try{
                if(lastStar <= stars){
                    int i = logIOSQLite.executeWriter("UPDATE " + SQLITE_VARIABLES.Table_Users.DB_TABLE_NAME + " " +
                            "SET " + starsToFill + " = '" + stars + "' " +
                            "WHERE " + SQLITE_VARIABLES.Table_Users.DB_COL_USERNAME + " = '" + playerInGameModel.getUsername() + "';");
                    Log.i(TAG, "logIOSQLite.executeWriter: " + i);
                    Log.i(TAG, "playerInGameModel.getUsername(): " + playerInGameModel.getUsername());
                    Log.i(TAG, "starsToFill: " + starsToFill);
                    Log.i(TAG, "stars: " + stars);
                }

                Intent intent1 = new Intent();
                intent1.putExtra(PLAYER_IN_GAME_MODEL, playerInGameModel);
                setResult(Activity.RESULT_OK, intent1);
                finish();

            }catch (SQLiteException e){
                Log.i(TAG, "setUpGameQuestions: " + e.getMessage());
            }
            return;
        }
        Log.i(TAG, "questionModelList.get(questionNumber).getA_id: " + questionModelList.get(questionNumber).getA_id());
        Log.i(TAG, "questionModelList.get(questionNumber).getA_level: " + questionModelList.get(questionNumber).getA_level());
        Log.i(TAG, "questionModelList.get(questionNumber).getA_questiontype: " + questionModelList.get(questionNumber).getA_questiontype());
        Log.i(TAG, "questionModelList.get(questionNumber).getA_question: " + questionModelList.get(questionNumber).getA_question());
        Log.i(TAG, "questionModelList.get(questionNumber).getA_choices: " + questionModelList.get(questionNumber).getA_choices());
        Log.i(TAG, "questionModelList.get(questionNumber).getA_answer: " + questionModelList.get(questionNumber).getA_answer());
        Log.i(TAG, "questionModelList.get(questionNumber).getA_timeduration: " + questionModelList.get(questionNumber).getA_timeduration());
        Log.i(TAG, "questionModelList.get(questionNumber).getA_category: " + questionModelList.get(questionNumber).getA_category());
        Log.i(TAG, "questionModelList.get(questionNumber).getA_instruction: " + questionModelList.get(questionNumber).getA_instruction());
        Log.i(TAG, "questionNumber: " + questionNumber);
        switch (questionModelList.get(questionNumber).getA_questiontype()){
            case QuestionType.MULTIPLE_CHOICE:{
                Log.i(TAG, "onCreate: MULTIPLE_CHOICE");
                mLinearLayoutDiagram.setVisibility(View.GONE);
                mLinearLayoutTruthTable.setVisibility(View.GONE);
                mLinearLayoutMultipleChoice.setVisibility(View.VISIBLE);
                mLinearLayoutIdentification.setVisibility(View.GONE);
                if(!questionModelList.get(questionNumber).getA_answer().isEmpty()){
                    setUpMultipleChoice(mLinearLayoutMultipleChoice, mTextViewMultipleChoiceQuestion,
                            mButtonMultipleChoiceChoices, questionModelList.get(questionNumber),
                            mTextViewGameNumberOfQuestion, mTextViewGameTimer, questionNumber,
                            mImageButtonGamePause, mImageButtonGameHelp, mImageButtonGameOption, points);
                }else {
                    setUpGameQuestions(questionNumber + 1, points);
                }
                break;
            }
            case QuestionType.DIAGRAM:{
                Log.i(TAG, "onCreate: DIAGRAM");
                mLinearLayoutDiagram.setVisibility(View.VISIBLE);
                mLinearLayoutTruthTable.setVisibility(View.GONE);
                mLinearLayoutMultipleChoice.setVisibility(View.GONE);
                mLinearLayoutIdentification.setVisibility(View.GONE);

                if(!questionModelList.get(questionNumber).getA_answer().isEmpty()) {
                    setUpDiagram(mLinearLayoutDiagram, mLinearLayoutDynamicDiagram,
                            mButtonMDiagramChoices, questionModelList.get(questionNumber),
                            mTextViewGameNumberOfQuestion, mTextViewGameTimer, questionNumber,
                            mImageButtonGamePause, mImageButtonGameHelp, mImageButtonGameOption, points);
                }else {
                    setUpGameQuestions(questionNumber + 1, points);
                }
                break;
            }
            case QuestionType.DRAG_AND_DROP:{
                Log.i(TAG, "onCreate: DRAG_AND_DROP");

                mLinearLayoutDiagram.setVisibility(View.GONE);
                mLinearLayoutTruthTable.setVisibility(View.GONE);
                mLinearLayoutMultipleChoice.setVisibility(View.GONE);
                mLinearLayoutIdentification.setVisibility(View.GONE);
                setUpGameQuestions(questionNumber + 1, points);
                break;
            }
            case QuestionType.IDENTIFICATION:{
                Log.i(TAG, "onCreate: IDENTIFICATION");
                mLinearLayoutDiagram.setVisibility(View.GONE);
                mLinearLayoutTruthTable.setVisibility(View.GONE);
                mLinearLayoutMultipleChoice.setVisibility(View.GONE);
                mLinearLayoutIdentification.setVisibility(View.VISIBLE);

                if(!questionModelList.get(questionNumber).getA_answer().isEmpty()) {
                    setUpIdentification(mLinearLayoutIdentification, mTextViewIdentificationQuestion,
                            mEditTextIdentificationAnswer, mImageButtonIdentificationConfirmAnswer,
                            questionModelList.get(questionNumber), mTextViewGameNumberOfQuestion,
                            mTextViewGameTimer, questionNumber, mImageButtonGamePause,
                            mImageButtonGameHelp, mImageButtonGameOption, points);
                }else {
                    setUpGameQuestions(questionNumber + 1, points);
                }
                break;
            }
            case QuestionType.TRUTH_TABLE:{
                Log.i(TAG, "onCreate: TRUTH_TABLE");
                mLinearLayoutDiagram.setVisibility(View.GONE);
                mLinearLayoutTruthTable.setVisibility(View.VISIBLE);
                mLinearLayoutMultipleChoice.setVisibility(View.GONE);
                mLinearLayoutIdentification.setVisibility(View.GONE);

                if(!questionModelList.get(questionNumber).getA_answer().isEmpty()) {
                    setUpTruthTable(mLinearLayoutTruthTable, questionModelList.get(questionNumber),
                            mTextViewGameTimer, mTextViewGameTimer, questionNumber, mImageButtonGamePause,
                            mImageButtonGameHelp, mImageButtonGameOption, points);
                }else {
                    setUpGameQuestions(questionNumber + 1, points);
                }
                break;
            }
        }
    }

    private void setUpTruthTable(final LinearLayout mLinearLayoutTruthTable,
                                 final QuestionModel questionModel,
                                 final TextView mTextViewGameNumberOfQuestion,
                                 final TextView mTextViewGameTimer,
                                 final int questionNumber,
                                 ImageButton mImageButtonGamePause,
                                 ImageButton mImageButtonGameHelp,
                                 ImageButton mImageButtonGameOption, final int points){

        final HowToPlay howToPlay = new HowToPlay(GameActivity.this);
        final Paused paused = new Paused(GameActivity.this);
        final Options options = new Options(GameActivity.this);
        final TimesUp timesUp = new TimesUp(GameActivity.this);
        final Correct correct = new Correct(GameActivity.this);

        mTextViewGameNumberOfQuestion.setText(( (questionNumber + 1) + "/5"));
        mLinearLayoutTruthTable.setVisibility(View.VISIBLE);

        final CountDownTimer countDownTimer = new CountDownTimer((Integer.parseInt(questionModel.getA_timeduration()) * 1000), (1000)) {
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

        /*mTextViewMultipleChoiceQuestion.setText(questionModel.getA_question());
        if(questionModel.getA_question().toLowerCase().isEmpty()){
            Log.i(TAG, "setUpMultipleChoice: null question" );
            return;
        }*/
        Button mButtonTruthTableSubmit = null;
        TextView[] mTextViewTruthTableAnswers = null;
        List<String> answer = LogicHelper.choiceReBuilder(questionModel.getA_answer());
        int point = 0;
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
                    }else {
                        correct.mTextViewCorrectMessage.setText(("You've got the " + point + " score"));
                    }
                    correct.dialog.show();
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


        //region Game buttons
        howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                howToPlay.dialog.cancel();
                countDownTimer.start();
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
                finish();
            }
        });

        mImageButtonGamePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutTruthTable.setVisibility(View.GONE);
                paused.dialog.show();
            }
        });

        mImageButtonGameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutTruthTable.setVisibility(View.GONE);
                howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDownTimer.resume();
                        mLinearLayoutTruthTable.setVisibility(View.VISIBLE);
                        howToPlay.dialog.cancel();
                    }
                });
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
        //endregion
        howToPlay.dialog.show();
    }

    private void setUpDiagram(final LinearLayout mLinearLayoutDiagram,
                              final LinearLayout mLinearLayoutDynamicDiagram,
                              final Button[] mButtonMDiagramChoices,
                              final QuestionModel questionModel,
                              TextView mTextViewGameNumberOfQuestion,
                              final TextView mTextViewGameTimer,
                              final int questionNumber,
                              ImageButton mImageButtonGamePause,
                              ImageButton mImageButtonGameHelp,
                              ImageButton mImageButtonGameOption, final int points){

        final HowToPlay howToPlay = new HowToPlay(GameActivity.this);
        final Paused paused = new Paused(GameActivity.this);
        final Options options = new Options(GameActivity.this);
        final TimesUp timesUp = new TimesUp(GameActivity.this);
        final Correct correct = new Correct(GameActivity.this);

        mTextViewGameNumberOfQuestion.setText(( (questionNumber + 1) + "/5"));
        mLinearLayoutDiagram.setVisibility(View.VISIBLE);
        final CountDownTimer countDownTimer = new CountDownTimer((Integer.parseInt(questionModel.getA_timeduration()) * 1000), (1000)) {
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

        for(int i = 0; i < mButtonMDiagramChoices.length; i++){
            Log.i(TAG, "setUpDiagram: " + tmpChoices.get(i).toLowerCase());
            try{
                Log.i(TAG, "setUpDiagram: " + tmpChoices.get(i).toLowerCase());
                mButtonMDiagramChoices[i].setText("");
                mButtonMDiagramChoices[i].setContentDescription(tmpChoices.get(i));
                mButtonMDiagramChoices[i].setBackground(GetDrawableResource(GameActivity.this, tmpChoices.get(i).toLowerCase()));
            }
            catch (Exception e){
                mButtonMDiagramChoices[i].setBackground(null);
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
                        Toast.makeText(GameActivity.this, "Correct", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(GameActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
                        setUpGameQuestions(questionNumber + 1, points);
                        correct.dialog.cancel();
                    }
                }
            });
        }
        Log.i(TAG, "setUpDiagram: getA_timeduration = " + Integer.parseInt(questionModel.getA_timeduration()));
        //region Game buttons
        howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                howToPlay.dialog.cancel();
                countDownTimer.start();
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
                finish();
            }
        });

        mImageButtonGamePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutDiagram.setVisibility(View.GONE);
                paused.dialog.show();
            }
        });

        mImageButtonGameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutDiagram.setVisibility(View.GONE);
                howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDownTimer.resume();
                        mLinearLayoutDiagram.setVisibility(View.VISIBLE);
                        howToPlay.dialog.cancel();
                    }
                });
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
        //endregion
        howToPlay.dialog.show();
    }

    private void setUpMultipleChoice(final LinearLayout mLinearLayoutMultipleChoice,
                                     TextView mTextViewMultipleChoiceQuestion,
                                     final Button[] mButtonMultipleChoiceChoices,
                                     final QuestionModel questionModel,
                                     TextView mTextViewGameNumberOfQuestion,
                                     final TextView mTextViewGameTimer,
                                     final int questionNumber,
                                     ImageButton mImageButtonGamePause,
                                     ImageButton mImageButtonGameHelp,
                                     ImageButton mImageButtonGameOption, final int points){

        final HowToPlay howToPlay = new HowToPlay(GameActivity.this);
        final Paused paused = new Paused(GameActivity.this);
        final Options options = new Options(GameActivity.this);
        final TimesUp timesUp = new TimesUp(GameActivity.this);
        final Correct correct = new Correct(GameActivity.this);

        mTextViewGameNumberOfQuestion.setText(( (questionNumber + 1) + "/5"));
        mLinearLayoutMultipleChoice.setVisibility(View.VISIBLE);

        final CountDownTimer countDownTimer = new CountDownTimer((Integer.parseInt(questionModel.getA_timeduration()) * 1000), (1000)) {
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

        for(int i = 0; i < mButtonMultipleChoiceChoices.length; i++){
            Log.i(TAG, "setUpMultipleChoice: " + tmpChoices.get(i).toLowerCase());
            mButtonMultipleChoiceChoices[i].setBackground(null);
            try{
                mButtonMultipleChoiceChoices[i].setText("");
                mButtonMultipleChoiceChoices[i].setContentDescription(tmpChoices.get(i));
                mButtonMultipleChoiceChoices[i].setBackground(GetDrawableResource(GameActivity.this, tmpChoices.get(i).toLowerCase()));
            }
            catch (Exception e){
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
                        correct.dialog.show();
                        Toast.makeText(GameActivity.this, "Correct", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(GameActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
                        setUpGameQuestions(questionNumber + 1, points);
                        correct.dialog.cancel();
                    }
                }
            });
        }
        Log.i(TAG, "setUpMultipleChoice: getA_timeduration = " + Integer.parseInt(questionModel.getA_timeduration()));
        //region Game buttons
        howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                howToPlay.dialog.cancel();
                countDownTimer.start();
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
                finish();
            }
        });

        mImageButtonGamePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutMultipleChoice.setVisibility(View.GONE);
                paused.dialog.show();
            }
        });

        mImageButtonGameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutMultipleChoice.setVisibility(View.GONE);
                howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDownTimer.resume();
                        mLinearLayoutMultipleChoice.setVisibility(View.VISIBLE);
                        howToPlay.dialog.cancel();
                    }
                });
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
        //endregion
        howToPlay.dialog.show();
    }

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
                                     ImageButton mImageButtonGameOption, final int points){

        final HowToPlay howToPlay = new HowToPlay(GameActivity.this);
        final Paused paused = new Paused(GameActivity.this);
        final Options options = new Options(GameActivity.this);
        final TimesUp timesUp = new TimesUp(GameActivity.this);
        final Correct correct = new Correct(GameActivity.this);

        mTextViewGameNumberOfQuestion.setText(( (questionNumber + 1) + "/5"));
        mLinearLayoutIdentification.setVisibility(View.VISIBLE);
        final CountDownTimer countDownTimer = new CountDownTimer((Integer.parseInt(questionModel.getA_timeduration()) * 1000), (1000)) {
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

        mTextViewIdentificationQuestion.setText(questionModel.getA_question());
        if(questionModel.getA_question().toLowerCase().isEmpty()){
            Log.i(TAG, "setUpIdentification: null question" );
            return;
        }

        mImageButtonIdentificationConfirmAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = mEditTextIdentificationAnswer.getText().toString().toLowerCase();

                if(answer.equals(questionModel.getA_answer().toLowerCase())){
                    //region correct answer
                    countDownTimer.pause();
                    countDownTimer.cancel();
                    correct.dialog.show();
                    correct.mImageButtonCorrectOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setUpGameQuestions(questionNumber + 1, points + 1);
                            correct.dialog.cancel();
                        }
                    });
                    Toast.makeText(GameActivity.this, "Correct", Toast.LENGTH_SHORT).show();
                    //endregion
                }
                else {
                    countDownTimer.pause();
                    remainingTime = Integer.parseInt(mTextViewGameTimer.getText().toString());
                    countDownTimer.cancel();
                    Toast.makeText(GameActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
                    setUpGameQuestions(questionNumber + 1, points);
                    correct.dialog.cancel();
                }
            }
        });

        Log.i(TAG, "setUpIdentification: getA_timeduration = " + Integer.parseInt(questionModel.getA_timeduration()));
        //region Game buttons
        howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                howToPlay.dialog.cancel();
                countDownTimer.start();
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
                finish();
            }
        });

        mImageButtonGamePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutIdentification.setVisibility(View.GONE);
                paused.dialog.show();
            }
        });

        mImageButtonGameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.pause();
                mLinearLayoutIdentification.setVisibility(View.GONE);
                howToPlay.mImageButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDownTimer.resume();
                        mLinearLayoutIdentification.setVisibility(View.VISIBLE);
                        howToPlay.dialog.cancel();
                    }
                });
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
        //endregion
        howToPlay.dialog.show();
    }

}
