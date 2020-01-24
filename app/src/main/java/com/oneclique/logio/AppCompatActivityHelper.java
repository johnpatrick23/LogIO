package com.oneclique.logio;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.QuestionModel;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UserAchievementsModel;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UserLogsModel;
import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UsersModel;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class AppCompatActivityHelper extends AppCompatActivity implements RequestVariables, Variables {

    protected static final int SPLASH_DISPLAY_LENGTH = 2000;

    protected String SelectedGameMode;
    protected String SelectedLevel;
    protected String SelectedLetter;

    private String mStringBasePath;
    private String mStringVideoPath;
    private String mStringImagePath;

    protected void fullScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void SnackBarMessage(String Message){
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, Message, Snackbar.LENGTH_SHORT).show();
    }

    protected void FullscreenDialog(Dialog dialog){
        Objects.requireNonNull(dialog.getWindow())
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    protected void requestPermission(Context context){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_SECURE_SETTINGS) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_SECURE_SETTINGS,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);
                try {
                    createPath(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String TAG = "LogIO";

    protected void createPath(Context context) throws IOException {
        final File dir = context.getExternalFilesDir(null);
        mStringBasePath = dir != null ? dir.getAbsolutePath() : null;
        mStringVideoPath = this.mStringBasePath + "/vid/";
        mStringImagePath = this.mStringBasePath + "/img/";
        Log.i(TAG, "mStringVideoPath: " + mStringVideoPath + " " + new File(mStringVideoPath).mkdir());
        Log.i(TAG, "mStringImagePath: " + mStringImagePath + " " + new File(mStringImagePath).mkdir());
    }

    public String getVidPathName(Context context) {
        final File dir = context.getExternalFilesDir(null);
        mStringBasePath = dir != null ? dir.getAbsolutePath() : null;
        mStringVideoPath = this.mStringBasePath + "/vid/";
        return mStringVideoPath;
    }

    public String getImgPathName(Context context){
        final File dir = context.getExternalFilesDir(null);
        mStringBasePath = dir != null ? dir.getAbsolutePath() : null;
        mStringImagePath = this.mStringBasePath + "/img/";
        return mStringImagePath;
    }

    public int GetResourceID(Context context, String resourceName, String resourceType){
        Resources resources = context.getResources();
        return resources.getIdentifier(resourceName, resourceType,
                context.getPackageName());
    }

    public Drawable GetDrawableResource(Context context, String resourceName){
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(resourceName, "drawable",
                context.getPackageName());
        return resources.getDrawable(resourceId);
    }

    public Uri GetRawResource(String resourceName){
        int rawResource = getResources()
                .getIdentifier(resourceName, "raw", getPackageName());
        return Uri.parse("android.resource://" + getPackageName() + "/" + rawResource);
    }

    public Uri GetDrawableResource(String resourceName){
        int drawableResource = getResources()
                .getIdentifier(resourceName, "drawable", getPackageName());
        return Uri.parse("android.resource://" + getPackageName() + "/" + drawableResource);
    }

    public void playSound(Context context, int rawRes){
        SoundPool sounds;
        int sExplosion;
        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        sExplosion = sounds.load(context, rawRes, 1);
        sounds.play(sExplosion, 1.0f, 1.0f, 0, 0, 1.5f);
    }

    public Typeface courierprime_bold (){
        return Typeface.createFromAsset(getAssets(), "courierprime_bold.ttf");
    }

    public Typeface courierprime_regular (){
        return Typeface.createFromAsset(getAssets(), "courierprime_regular.ttf");
    }

    public Typeface courierprime_bolditalic (){
        return Typeface.createFromAsset(getAssets(), "courierprime_bolditalic.ttf");
    }

    public Typeface courierprime_italic (){
        return Typeface.createFromAsset(getAssets(), "courierprime_italic.ttf");
    }

    public void UserAchievementsModelLog(UserAchievementsModel userAchievementsModel){
        String getA_id = userAchievementsModel.getA_id() == null ? "null" : userAchievementsModel.getA_id();
        String getA_time_finished = userAchievementsModel.getA_time_finished() == null ? "null" : userAchievementsModel.getA_time_finished();
        String getA_description = userAchievementsModel.getA_description() == null  ? "null" : userAchievementsModel.getA_description();
        String getA_level = userAchievementsModel.getA_level() == null  ? "null" : userAchievementsModel.getA_level();
        String getA_number_of_tries = userAchievementsModel.getA_number_of_tries() == null  ? "null" : userAchievementsModel.getA_number_of_tries();
        String getA_stars = userAchievementsModel.getA_stars() == null  ? "null" : userAchievementsModel.getA_stars();
        String getA_username = userAchievementsModel.getA_username() == null  ? "null" : userAchievementsModel.getA_username();

        Log.i(TAG, "getA_id: " + getA_id);
        Log.i(TAG, "getA_time_finished: " + getA_time_finished);
        Log.i(TAG, "getA_description: " + getA_description);
        Log.i(TAG, "getA_number_of_tries: " + getA_number_of_tries);
        Log.i(TAG, "getA_stars: " + getA_stars);
        Log.i(TAG, "getA_level: " + getA_level);
        Log.i(TAG, "getA_username: " + getA_username);
    }

    public void UserLogsModelLog(UserLogsModel userLogsModel){
        String getA_id = userLogsModel.getA_id() == null ? "null" : userLogsModel.getA_id();
        String getA_average_time = userLogsModel.getA_average_time() == null ? "null" : userLogsModel.getA_average_time();
        String getA_level = userLogsModel.getA_level() == null  ? "null" : userLogsModel.getA_level();
        String getA_popup_message_time = userLogsModel.getA_popup_message_time() == null  ? "null" : userLogsModel.getA_popup_message_time();
        String getA_star = userLogsModel.getA_star() == null  ? "null" : userLogsModel.getA_star();
        String getA_username = userLogsModel.getA_username() == null  ? "null" : userLogsModel.getA_username();

        Log.i(TAG, "getA_id: " + getA_id);
        Log.i(TAG, "getA_average_time: " + getA_average_time);
        Log.i(TAG, "getA_level: " + getA_level);
        Log.i(TAG, "getA_popup_message_time: " + getA_popup_message_time);
        Log.i(TAG, "getA_star: " + getA_star);
        Log.i(TAG, "getA_username: " + getA_username);
    }

    public void UsersModelLog(UsersModel usersModel){
        String getA_id = usersModel.getA_id() == null ? "null" : usersModel.getA_id();
        String getA_username = usersModel.getA_username() == null ? "null" : usersModel.getA_username();
        String getA_last_used = usersModel.getA_last_used() == null ? "null" : usersModel.getA_last_used();
        String getA_level_1_stars = usersModel.getA_level_1_stars() == null ? "null" : usersModel.getA_level_1_stars();
        String getA_level_2_stars = usersModel.getA_level_2_stars() == null ? "null" : usersModel.getA_level_2_stars();
        String getA_level_3_stars = usersModel.getA_level_3_stars() == null ? "null" : usersModel.getA_level_3_stars();
        String getA_level_4_stars = usersModel.getA_level_4_stars() == null ? "null" : usersModel.getA_level_4_stars();
        String getA_level_5_stars = usersModel.getA_level_5_stars() == null ? "null" : usersModel.getA_level_5_stars();
        String getA_level_6_stars = usersModel.getA_level_6_stars() == null ? "null" : usersModel.getA_level_6_stars();
        String getA_level_7_stars = usersModel.getA_level_7_stars() == null ? "null" : usersModel.getA_level_7_stars();
        String getA_number_of_access = usersModel.getA_number_of_access() == null ? "null" : usersModel.getA_number_of_access();
        String getA_hint = usersModel.getA_hint() == null ? "null" : usersModel.getA_hint();
        String getA_add_time = usersModel.getA_add_time() == null ? "null" : usersModel.getA_add_time();
        String getA_slow_time = usersModel.getA_slow_time() == null ? "null" : usersModel.getA_slow_time();

        Log.i(TAG, "getA_id: " + getA_id);
        Log.i(TAG, "getA_username: " + getA_username);
        Log.i(TAG, "getA_last_used: " + getA_last_used);
        Log.i(TAG, "getA_level_1_stars: " + getA_level_1_stars);
        Log.i(TAG, "getA_level_2_stars: " + getA_level_2_stars);
        Log.i(TAG, "getA_level_3_stars: " + getA_level_3_stars);
        Log.i(TAG, "getA_level_4_stars: " + getA_level_4_stars);
        Log.i(TAG, "getA_level_5_stars: " + getA_level_5_stars);
        Log.i(TAG, "getA_level_6_stars: " + getA_level_6_stars);
        Log.i(TAG, "getA_level_7_stars: " + getA_level_7_stars);
        Log.i(TAG, "getA_number_of_access: " + getA_number_of_access);
        Log.i(TAG, "getA_hint: " + getA_hint);
        Log.i(TAG, "getA_add_time: " + getA_add_time);
        Log.i(TAG, "getA_slow_time: " + getA_slow_time);
    }

    public void QuestionModelLog(QuestionModel questionModel){

        String getA_id = questionModel.getA_id() == null ? "null" : questionModel.getA_id();
        String getA_level = questionModel.getA_level() == null ? "null" : questionModel.getA_level();
        String getA_questiontype = questionModel.getA_questiontype() == null ? "null" : questionModel.getA_questiontype();
        String getA_question = questionModel.getA_question() == null ? "null" : questionModel.getA_question();
        String getA_choices = questionModel.getA_choices() == null ? "null" : questionModel.getA_choices();
        String getA_answer = questionModel.getA_answer() == null ? "null" : questionModel.getA_answer();
        String getA_timeduration = questionModel.getA_timeduration() == null ? "null" : questionModel.getA_timeduration();
        String getA_category = questionModel.getA_category() == null ? "null" : questionModel.getA_category();
        String getA_instruction = questionModel.getA_instruction() == null ? "null" : questionModel.getA_instruction();

        Log.i(TAG, "getA_id: " + getA_id);
        Log.i(TAG, "getA_level: " + getA_level);
        Log.i(TAG, "getA_questiontype: " + getA_questiontype);
        Log.i(TAG, "getA_question: " + getA_question);
        Log.i(TAG, "getA_choices: " + getA_choices);
        Log.i(TAG, "getA_answer: " + getA_answer);
        Log.i(TAG, "getA_timeduration: " + getA_timeduration);
        Log.i(TAG, "getA_category: " + getA_category);
        Log.i(TAG, "getA_instruction: " + getA_instruction);
    }

    class AllLevels {
        Dialog dialog;
        ImageButton[] mImageButtonLevels;
        ImageView[][] mImageViewLevelsStars;
        ImageButton mImageButtonCloseAllLevels;
        ImageButton mImageButtonHome;
        ImageButton mImageButtonPlay;


        AllLevels(Context context){

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_all_levels);
            FullscreenDialog(dialog);
            mImageButtonCloseAllLevels = dialog.findViewById(R.id.mImageButtonCloseAllLevels);
            mImageButtonPlay = dialog.findViewById(R.id.mImageButtonPlay);
            mImageButtonLevels = new ImageButton[]{
                    dialog.findViewById(R.id.mImageButtonLevel1),
                    dialog.findViewById(R.id.mImageButtonLevel2),
                    dialog.findViewById(R.id.mImageButtonLevel3),
                    dialog.findViewById(R.id.mImageButtonLevel4),
                    dialog.findViewById(R.id.mImageButtonLevel5),
                    dialog.findViewById(R.id.mImageButtonLevel6),
                    dialog.findViewById(R.id.mImageButtonLevel7)
            };

            mImageViewLevelsStars = new ImageView[][]{
                    {
                            dialog.findViewById(R.id.mImageViewLevel1Star1),
                            dialog.findViewById(R.id.mImageViewLevel1Star2),
                            dialog.findViewById(R.id.mImageViewLevel1Star3)
                    },
                    {
                            dialog.findViewById(R.id.mImageViewLevel2Star1),
                            dialog.findViewById(R.id.mImageViewLevel2Star2),
                            dialog.findViewById(R.id.mImageViewLevel2Star3)
                    },
                    {
                            dialog.findViewById(R.id.mImageViewLevel3Star1),
                            dialog.findViewById(R.id.mImageViewLevel3Star2),
                            dialog.findViewById(R.id.mImageViewLevel3Star3)
                    },
                    {
                            dialog.findViewById(R.id.mImageViewLevel4Star1),
                            dialog.findViewById(R.id.mImageViewLevel4Star2),
                            dialog.findViewById(R.id.mImageViewLevel4Star3)
                    },
                    {
                            dialog.findViewById(R.id.mImageViewLevel5Star1),
                            dialog.findViewById(R.id.mImageViewLevel5Star2),
                            dialog.findViewById(R.id.mImageViewLevel5Star3)
                    },
                    {
                            dialog.findViewById(R.id.mImageViewLevel6Star1),
                            dialog.findViewById(R.id.mImageViewLevel6Star2),
                            dialog.findViewById(R.id.mImageViewLevel6Star3)
                    },
                    {
                            dialog.findViewById(R.id.mImageViewLevel7Star1),
                            dialog.findViewById(R.id.mImageViewLevel7Star2),
                            dialog.findViewById(R.id.mImageViewLevel7Star3)
                    }
            };


            mImageButtonCloseAllLevels.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

        }

    }

    class User {

        Dialog dialog;
        TextView mTextViewSelectedUsername;
        Button mButtonSelectedUserStart;
        ImageButton mImageButtonUserClose;

        User(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_user);
            FullscreenDialog(dialog);
            mImageButtonUserClose = dialog.findViewById(R.id.mImageButtonUserClose);
            mTextViewSelectedUsername = dialog.findViewById(R.id.mTextViewSelectedUsername);
            mButtonSelectedUserStart = dialog.findViewById(R.id.mButtonSelectedUserStart);
            mImageButtonUserClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }
    }

    class LevelComplete {

        Dialog dialog;

        ImageView[] mImageViewCompletedStars;
        TextView mTextViewCompletedTime;
        TextView mTextViewCompletedAverageTime;

        TextView mTextViewCompletedSelectedLevel;

        ImageButton mImageButtonCompletedOk;
        ImageButton mImageViewCompletedRestart;
        ImageButton mImageButtonCompletedNext;

        LevelComplete(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_level_complete);
            FullscreenDialog(dialog);

            mTextViewCompletedSelectedLevel = dialog.findViewById(R.id.mTextViewCompletedSelectedLevel);
            mImageButtonCompletedNext = dialog.findViewById(R.id.mImageButtonCompletedNext);
            mImageButtonCompletedOk = dialog.findViewById(R.id.mImageButtonCompletedOk);
            mImageViewCompletedRestart = dialog.findViewById(R.id.mImageViewCompletedRestart);
            mTextViewCompletedTime = dialog.findViewById(R.id.mTextViewCompletedTime);
            mTextViewCompletedAverageTime = dialog.findViewById(R.id.mTextViewCompletedAverageTime);

            mImageViewCompletedStars = new ImageView[]{
                    dialog.findViewById(R.id.mImageViewCompletedStar1),
                    dialog.findViewById(R.id.mImageViewCompletedStar2),
                    dialog.findViewById(R.id.mImageViewCompletedStar3)
            };
        }
    }

    class Options {
        Dialog dialog;

        Button mButtonOptionResume;
        Button mButtonOptionReplay;
        Button mButtonOptionSettings;
        Button mButtonOptionExit;

        Options(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_options);
            FullscreenDialog(dialog);

            mButtonOptionResume = dialog.findViewById(R.id.mButtonOptionResume);
            mButtonOptionReplay = dialog.findViewById(R.id.mButtonOptionReplay);
            mButtonOptionSettings = dialog.findViewById(R.id.mButtonOptionSettings);
            mButtonOptionExit = dialog.findViewById(R.id.mButtonOptionExit);
        }

    }

    class Paused {

        Dialog dialog;
        Button mButtonPausedRestart;
        Button mButtonPausedResume;
        Button mButtonPausedLevels;
        Button mButtonPausedExit;


        Paused(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_paused);
            FullscreenDialog(dialog);
            mButtonPausedRestart = dialog.findViewById(R.id.mButtonPausedRestart);
            mButtonPausedResume = dialog.findViewById(R.id.mButtonPausedResume);
            mButtonPausedLevels = dialog.findViewById(R.id.mButtonPausedLevels);
            mButtonPausedExit = dialog.findViewById(R.id.mButtonPausedExit);
        }

    }

    class Items {
        Dialog dialog;

        TextView mTextViewItemsRemainingPoints;
        TextView mTextViewHintItemCount;
        ImageButton mImageButtonHint;
        ImageButton mImageButtonAddHint;
        ImageButton mImageButtonRestartHint;

        TextView mTextViewSlowItemCount;
        ImageButton mImageButtonSlow;
        ImageButton mImageButtonAddSlow;
        ImageButton mImageButtonRestartSlow;

        TextView mTextViewAddTimeItemCount;
        ImageButton mImageButtonAddTime;
        ImageButton mImageButtonAddAddTime;
        ImageButton mImageButtonRestartAddTime;
        ImageButton mImageButtonItemHome;

        int hint = 0;
        int slowTime = 0;
        int addTime = 0;
        int remaining = 0;


        Items(Context context, int points){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_items);
            FullscreenDialog(dialog);

            mTextViewItemsRemainingPoints = dialog.findViewById(R.id.mTextViewItemsRemainingPoints);

            mTextViewHintItemCount = dialog.findViewById(R.id.mTextViewHintItemCount);
            mImageButtonHint = dialog.findViewById(R.id.mImageButtonHint);
            mImageButtonAddHint = dialog.findViewById(R.id.mImageButtonAddHint);
            mImageButtonRestartHint = dialog.findViewById(R.id.mImageButtonRestartHint);

            mTextViewSlowItemCount = dialog.findViewById(R.id.mTextViewSlowItemCount);
            mImageButtonSlow = dialog.findViewById(R.id.mImageButtonSlow);
            mImageButtonAddSlow = dialog.findViewById(R.id.mImageButtonAddSlow);
            mImageButtonRestartSlow = dialog.findViewById(R.id.mImageButtonRestartSlow);

            mTextViewAddTimeItemCount = dialog.findViewById(R.id.mTextViewAddTimeItemCount);
            mImageButtonAddTime = dialog.findViewById(R.id.mImageButtonAddTime);
            mImageButtonAddAddTime = dialog.findViewById(R.id.mImageButtonAddAddTime);
            mImageButtonRestartAddTime = dialog.findViewById(R.id.mImageButtonRestartAddTime);

            mImageButtonItemHome = dialog.findViewById(R.id.mImageButtonItemHome);

            mTextViewItemsRemainingPoints.setText(String.valueOf(points));

            //region Hint
            mImageButtonAddHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remaining = Integer.parseInt(mTextViewItemsRemainingPoints.getText().toString());
                    hint = Integer.parseInt(mTextViewHintItemCount.getText().toString());
                    if(remaining != 0){
                        hint++;
                        remaining--;
                    }
                    mTextViewItemsRemainingPoints.setText(String.valueOf(remaining));
                    mTextViewHintItemCount.setText(String.valueOf(hint));
                }
            });

            mImageButtonRestartHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remaining = Integer.parseInt(mTextViewItemsRemainingPoints.getText().toString());
                    hint = Integer.parseInt(mTextViewHintItemCount.getText().toString());
                    if(hint != 0){
                        remaining += hint;
                        hint = 0;
                    }
                    mTextViewItemsRemainingPoints.setText(String.valueOf(remaining));
                    mTextViewHintItemCount.setText(String.valueOf(hint));
                }
            });
            //endregion

            //region Slow Time
            mImageButtonAddSlow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remaining = Integer.parseInt(mTextViewItemsRemainingPoints.getText().toString());
                    slowTime = Integer.parseInt(mTextViewSlowItemCount.getText().toString());
                    if(remaining != 0){
                        slowTime++;
                        remaining--;
                    }
                    mTextViewItemsRemainingPoints.setText(String.valueOf(remaining));
                    mTextViewSlowItemCount.setText(String.valueOf(slowTime));
                }
            });

            mImageButtonRestartSlow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remaining = Integer.parseInt(mTextViewItemsRemainingPoints.getText().toString());
                    slowTime = Integer.parseInt(mTextViewSlowItemCount.getText().toString());
                    if(slowTime != 0){
                        remaining += slowTime;
                        slowTime = 0;
                    }
                    mTextViewItemsRemainingPoints.setText(String.valueOf(remaining));
                    mTextViewSlowItemCount.setText(String.valueOf(slowTime));
                }
            });
            //endregion

            //region Add Time
            mImageButtonAddAddTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remaining = Integer.parseInt(mTextViewItemsRemainingPoints.getText().toString());
                    addTime = Integer.parseInt(mTextViewAddTimeItemCount.getText().toString());
                    if(remaining != 0){
                        addTime++;
                        remaining--;
                    }
                    mTextViewItemsRemainingPoints.setText(String.valueOf(remaining));
                    mTextViewAddTimeItemCount.setText(String.valueOf(addTime));
                }
            });

            mImageButtonRestartAddTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remaining = Integer.parseInt(mTextViewItemsRemainingPoints.getText().toString());
                    addTime = Integer.parseInt(mTextViewAddTimeItemCount.getText().toString());
                    if(addTime != 0){
                        remaining += addTime;
                        addTime = 0;
                    }
                    mTextViewItemsRemainingPoints.setText(String.valueOf(remaining));
                    mTextViewAddTimeItemCount.setText(String.valueOf(addTime));
                }
            });
            //endregion

        }

    }

    class HowToPlay {
        Dialog dialog;
        TextView mTextViewInstruction;
        ImageButton mImageButtonOk;

        HowToPlay(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_how_to_play);
            FullscreenDialog(dialog);
            mTextViewInstruction = dialog.findViewById(R.id.mTextViewInstruction);
            mImageButtonOk = dialog.findViewById(R.id.mImageButtonOk);
        }
    }

    class Introduction {
        Dialog dialog;
        ImageButton mImageButtonOk;

        Introduction(Context context, int level){
            dialog = new Dialog(context);
            switch (level){
                case 0:{
                    dialog.setContentView(R.layout.dialog_introduction_number_system);
                    mImageButtonOk = dialog.findViewById(R.id.mImageButtonIntroNumSysOk);
                    break;
                }
                case 1:{
                    dialog.setContentView(R.layout.dialog_introduction_not_system);
                    mImageButtonOk = dialog.findViewById(R.id.mImageButtonIntroNOTGateOk);
                    break;
                }
                case 2:{
                    dialog.setContentView(R.layout.dialog_introduction_and_system);
                    mImageButtonOk = dialog.findViewById(R.id.mImageButtonIntroANDGateOk);
                    break;
                }
                case 3:{
                    dialog.setContentView(R.layout.dialog_introduction_or_system);
                    mImageButtonOk = dialog.findViewById(R.id.mImageButtonIntroORGateOk);
                    break;
                }
                case 4:{
                    dialog.setContentView(R.layout.dialog_introduction_xor_system);
                    mImageButtonOk = dialog.findViewById(R.id.mImageButtonIntroXORGateOk);
                    break;
                }
                case 5:{
                    dialog.setContentView(R.layout.dialog_introduction_xnor_system);
                    mImageButtonOk = dialog.findViewById(R.id.mImageButtonIntroXNORGateOk);
                    break;
                }
                case 6:{
                    dialog.setContentView(R.layout.dialog_introduction_integrated_circuit);
                    mImageButtonOk = dialog.findViewById(R.id.mImageButtonIntroIntegratedCircuitOk);
                    break;
                }
            }
            FullscreenDialog(dialog);
        }
    }

    class SelectUser {
        Dialog dialog;
        ListView mListViewUsername;

        ImageButton mImageButtonSelectUsernameAdd;
        ImageButton mImageButtonSelectUsernameSelect;
        ImageButton mImageButtonSelectUsernameDelete;
        ImageButton mImageButtonSelectUsernameClose;
        ImageButton mImageButtonSelectUsernameAddUsername;

        EditText mEditTextAddUserUserName;

        LinearLayout mLinearLayoutAddUser;

        SelectUser(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_select_user);
            FullscreenDialog(dialog);

            mLinearLayoutAddUser = dialog.findViewById(R.id.mLinearLayoutAddUser);
            mListViewUsername = dialog.findViewById(R.id.mListViewUsername);
            mImageButtonSelectUsernameAdd = dialog.findViewById(R.id.mImageButtonSelectUsernameAdd);
            mImageButtonSelectUsernameSelect = dialog.findViewById(R.id.mImageButtonSelectUsernameSelect);
            mImageButtonSelectUsernameDelete = dialog.findViewById(R.id.mImageButtonSelectUsernameDelete);
            mImageButtonSelectUsernameClose = dialog.findViewById(R.id.mImageButtonSelectUsernameClose);
            mImageButtonSelectUsernameAddUsername = dialog.findViewById(R.id.mImageButtonSelectUsernameAddUsername);

            mEditTextAddUserUserName = dialog.findViewById(R.id.mEditTextAddUserUserName);

            mImageButtonSelectUsernameClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }
    }

    class TimesUp {
        Dialog dialog;
        ImageButton mImageButtonTimesUpRestart;
        ImageButton mImageButtonTimesUpOk;

        TimesUp(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_times_up);
            FullscreenDialog(dialog);
            mImageButtonTimesUpOk = dialog.findViewById(R.id.mImageButtonTimesUpOk);
            mImageButtonTimesUpRestart = dialog.findViewById(R.id.mImageButtonTimesUpRestart);
        }
    }

    class Correct {
        Dialog dialog;
        ImageButton mImageButtonCorrectOk;
        TextView mTextViewCorrectMessage;

        Correct(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_correct);
            FullscreenDialog(dialog);
            mTextViewCorrectMessage = dialog.findViewById(R.id.mTextViewCorrectMessage);
            mImageButtonCorrectOk = dialog.findViewById(R.id.mImageButtonCorrectOk);
        }
    }

    class Wrong {
        Dialog dialog;
        ImageButton mImageButtonWrongOk;
        TextView mTextViewWrongMessage;

        Wrong(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_wrong);
            FullscreenDialog(dialog);
            mTextViewWrongMessage = dialog.findViewById(R.id.mTextViewWrongMessage);
            mImageButtonWrongOk = dialog.findViewById(R.id.mImageButtonWrongOk);
        }
    }

}
