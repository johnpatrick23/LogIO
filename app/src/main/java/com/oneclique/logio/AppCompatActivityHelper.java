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

import java.io.File;
import java.io.IOException;
import java.util.List;
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
        Snackbar.make(parentLayout, Message, Snackbar.LENGTH_LONG).show();
    }

    protected void FullscreenDialog(Dialog dialog){
        Objects.requireNonNull(dialog.getWindow())
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);

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

    String TAG = "LaroLexia";

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

        void show(){
            dialog.show();
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

        ImageButton mImageButtonCompletedHome;
        ImageButton mImageViewCompletedRestart;
        ImageButton mImageButtonCompletedPlay;

        LevelComplete(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_level_complete);
            FullscreenDialog(dialog);

            mImageButtonCompletedPlay = dialog.findViewById(R.id.mImageButtonCompletedPlay);
            mImageButtonCompletedHome = dialog.findViewById(R.id.mImageButtonCompletedHome);
            mImageViewCompletedRestart = dialog.findViewById(R.id.mImageViewCompletedRestart);
            mTextViewCompletedTime = dialog.findViewById(R.id.mTextViewCompletedTime);

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
        Button mButtonItemPlay;


        Items(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_items);
            FullscreenDialog(dialog);

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
            mButtonItemPlay = dialog.findViewById(R.id.mButtonItemPlay);

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


}
