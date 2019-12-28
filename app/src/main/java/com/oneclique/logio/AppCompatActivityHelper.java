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

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class AppCompatActivityHelper extends AppCompatActivity {

    protected static final int SPLASH_DISPLAY_LENGTH = 2000;

    protected String SelectedGameMode;
    protected String SelectedLevel;
    protected String SelectedLetter;

    private String mStringBasePath;
    private String mStringVideoPath;
    private String mStringImagePath;

    protected Typeface verdana_bold (){
        return Typeface.createFromAsset(getAssets(), "verdanab.ttf");
    }

    protected Typeface verdana (){
        return Typeface.createFromAsset(getAssets(), "verdana.ttf");
    }

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

    class AllLevels {
        Dialog dialog;

        AllLevels(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_all_levels);
            FullscreenDialog(dialog);
        }

        void show(){
            dialog.show();
        }
    }

    class User {
        Dialog dialog;

        User(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_user);
            FullscreenDialog(dialog);
        }

        void show(){
            dialog.show();
        }
    }

    class LevelComplete {
        Dialog dialog;

        LevelComplete(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_level_complete);
            FullscreenDialog(dialog);
        }

        void show(){
            dialog.show();
        }
    }

    class Options {
        Dialog dialog;

        Options(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_options);
            FullscreenDialog(dialog);
        }

        void show(){
            dialog.show();
        }
    }

    class Paused {
        Dialog dialog;

        Paused(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_paused);
            FullscreenDialog(dialog);
        }

        void show(){
            dialog.show();
        }
    }

    class Items {
        Dialog dialog;

        Items(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_items);
            FullscreenDialog(dialog);
        }

        void show(){
            dialog.show();
        }
    }

    class HowToPlay {
        Dialog dialog;

        HowToPlay(Context context){
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_how_to_play);
            FullscreenDialog(dialog);
        }

        void show(){
            dialog.show();
        }
    }


}
