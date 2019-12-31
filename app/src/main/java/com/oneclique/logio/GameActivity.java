package com.oneclique.logio;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
    //region Multiple Choice views
    private LinearLayout mLinearLayoutMultipleChoice;
    private TextView mTextViewMultipleChoiceQuestion;
    private Button[] mButtonMultipleChoiceChoices;
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

        questionModelList = new ArrayList<>();
        sequenceOfQuestions = new ArrayList<>();
        logIOSQLite = new LogIOSQLite(GameActivity.this);
        logIOSQLite.createDatabase();
        Intent intent = getIntent();

        selectedLevel = (int) Objects.requireNonNull(intent.getExtras()).getSerializable(SELECTED_LEVEL);

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
                "where " + SQLITE_VARIABLES.Table_Questions.DB_COL_LEVEL + " = '" + (selectedLevel + 1) + "' and" +
                " " + SQLITE_VARIABLES.Table_Questions.DB_COL_QUESTION_TYPE + " = '" + QuestionType.MULTIPLECHOICE + "';");
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
        setUpGameQuestions();
        //endregion



        //region Multiple choice question

        //endregion

        //region Diagram

        //endregion

        //region Drag and Drop

        //endregion

        //region Identification

        //endregion

        //region Truth table

        //endregion

        mImageButtonGameBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setUpGameQuestions(){
        switch (questionModelList.get(questionNumber).getA_questiontype()){
            case QuestionType.MULTIPLECHOICE:{
                Log.i(TAG, "onCreate: MULTIPLECHOICE");
                mLinearLayoutDiagram.setVisibility(View.GONE);
                mLinearLayoutMultipleChoice.setVisibility(View.VISIBLE);
                mLinearLayoutIdentification.setVisibility(View.GONE);
                setUpMultipleChoice(mLinearLayoutMultipleChoice, mTextViewMultipleChoiceQuestion,
                        mButtonMultipleChoiceChoices, questionModelList.get(questionNumber),
                        mTextViewGameNumberOfQuestion, mTextViewGameTimer, questionNumber,
                        mImageButtonGamePause, mImageButtonGameHelp, mImageButtonGameOption);
                break;
            }
            case QuestionType.DIAGRAM:{
                Log.i(TAG, "onCreate: DIAGRAM");
                break;
            }
            case QuestionType.DRAG_AND_DROP:{
                Log.i(TAG, "onCreate: DRAG_AND_DROP");
                break;
            }
            case QuestionType.IDENTIFICATION:{
                Log.i(TAG, "onCreate: IDENTIFICATION");
                break;
            }
            case QuestionType.TRUTH_TABLE:{
                Log.i(TAG, "onCreate: TRUTH_TABLE");
                break;
            }
        }
    }

    private void setUpMultipleChoice(final LinearLayout mLinearLayoutMultipleChoice,
                                     TextView mTextViewMultipleChoiceQuestion,
                                     final Button[] mButtonMultipleChoiceChoices,
                                     final QuestionModel questionModel,
                                     TextView mTextViewGameNumberOfQuestion,
                                     final TextView mTextViewGameTimer,
                                     int questionNumber,
                                     ImageButton mImageButtonGamePause,
                                     ImageButton mImageButtonGameHelp,
                                     ImageButton mImageButtonGameOption){

        final HowToPlay howToPlay = new HowToPlay(GameActivity.this);
        final Paused paused = new Paused(GameActivity.this);
        final Options options = new Options(GameActivity.this);
        mTextViewGameNumberOfQuestion.setText(( (questionNumber + 1) + "/5"));
        mLinearLayoutMultipleChoice.setVisibility(View.VISIBLE);

        mTextViewMultipleChoiceQuestion.setText(questionModel.getA_question());
        if(questionModel.getA_question().toLowerCase().isEmpty()){
            Log.i(TAG, "setUpMultipleChoice: null question" );
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
            mButtonMultipleChoiceChoices[i].setText(Html.fromHtml(tmpChoices.get(i)));
            final int finalI = i;
            mButtonMultipleChoiceChoices[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mButtonMultipleChoiceChoices[finalI].getText().toString().toLowerCase().equals(questionModel.getA_answer().toLowerCase())){
                        Toast.makeText(GameActivity.this, "Correct", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        Log.i(TAG, "setUpMultipleChoice: getA_timeduration = " + Integer.parseInt(questionModel.getA_timeduration()));
        final CountDownTimer countDownTimer = new CountDownTimer((Integer.parseInt(questionModel.getA_timeduration()) * 1000), (1000)) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTextViewGameTimer.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {

            }
        };

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

        howToPlay.dialog.show();
    }

}
