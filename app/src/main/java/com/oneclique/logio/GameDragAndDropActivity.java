package com.oneclique.logio;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameDragAndDropActivity extends AppCompatActivityHelper {

    private ImageButton mImageButtonGame1Back;
    private TextView mTextViewGame1NumberOfQuestion;
    private TextView mTextViewGame1Timer;
    private ImageButton mImageButtonGame1Pause;
    private ImageButton mImageButtonGame1Help;
    private ImageButton mImageButtonGame1Option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_game_drag_and_drop);

        mImageButtonGame1Back = findViewById(R.id.mImageButtonGameBack);
        mImageButtonGame1Pause = findViewById(R.id.mImageButtonGamePause);
        mImageButtonGame1Help = findViewById(R.id.mImageButtonGameHelp);
        mImageButtonGame1Option = findViewById(R.id.mImageButtonGameOption);
        mTextViewGame1Timer = findViewById(R.id.mTextViewGameTimer);

        mTextViewGame1Timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelComplete levelComplete = new LevelComplete(GameDragAndDropActivity.this);
                levelComplete.dialog.show();
            }
        });

        mImageButtonGame1Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mImageButtonGame1Pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paused paused = new Paused(GameDragAndDropActivity.this);
                paused.dialog.show();
            }
        });

        mImageButtonGame1Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowToPlay howToPlay = new HowToPlay(GameDragAndDropActivity.this);
                howToPlay.dialog.show();
            }
        });

        mImageButtonGame1Option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = new Options(GameDragAndDropActivity.this);
                options.dialog.show();
            }
        });


    }
}
