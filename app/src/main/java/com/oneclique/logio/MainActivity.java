package com.oneclique.logio;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivityHelper {

    private Button mImageButtonStart;
    private Button mImageButtonAchievements;
    private Button mImageButtonExit;

    private ImageButton mImageButtonLeaderboard;
    private ImageButton mImageButtonSettings;
    private ImageButton mImageButtonUserIcon;

    private TextView mTextViewUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        requestPermission(MainActivity.this);
        setContentView(R.layout.activity_main);

        mImageButtonStart = findViewById(R.id.mImageButtonStart);
        mImageButtonUserIcon = findViewById(R.id.mImageButtonUserIcon);
        mImageButtonAchievements = findViewById(R.id.mImageButtonAchievements);
        mImageButtonExit = findViewById(R.id.mImageButtonExit);
        mImageButtonSettings = findViewById(R.id.mImageButtonSettings);
        mImageButtonLeaderboard = findViewById(R.id.mImageButtonLeaderboard);


        mImageButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mImageButtonUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


}
