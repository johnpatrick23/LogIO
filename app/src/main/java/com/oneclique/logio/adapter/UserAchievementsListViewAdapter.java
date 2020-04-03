package com.oneclique.logio.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oneclique.logio.LogIOSQLite.LogIOSQLiteModel.UserAchievementsModel;
import com.oneclique.logio.R;

import java.util.List;

public class UserAchievementsListViewAdapter extends BaseAdapter {

    private Context context;
    private List<UserAchievementsModel> achievementsModels;
    private LayoutInflater layoutInflater;

    public UserAchievementsListViewAdapter(Context context, List<UserAchievementsModel> achievementsModels){
        this.context = context;
        this.achievementsModels = achievementsModels;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return achievementsModels.size();
    }

    @Override
    public Object getItem(int position) {
        return achievementsModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_user_achievement, null, true);

        TextView mTextViewUserAchievementsLevel = convertView.findViewById(R.id.mTextViewUserAchievementsLevel);
        TextView mTextViewUserAchievementsNumberOfTriesText = convertView.findViewById(R.id.mTextViewUserAchievementsNumberOfTriesText);
        TextView mTextViewUserAchievementsNumberOfTries = convertView.findViewById(R.id.mTextViewUserAchievementsNumberOfTries);
        TextView mTextViewUserAchievementsAverageTime = convertView.findViewById(R.id.mTextViewUserAchievementsAverageTime);
        TextView mTextViewUserAchievementsPoints = convertView.findViewById(R.id.mTextViewUserAchievementsPoints);

        ImageView[] mImageViewAchievementsStars = {
                convertView.findViewById(R.id.mImageViewUserAchievementsStar1),
                convertView.findViewById(R.id.mImageViewUserAchievementsStar2),
                convertView.findViewById(R.id.mImageViewUserAchievementsStar3),
        };

        mTextViewUserAchievementsLevel.setText(achievementsModels.get(position).getA_level());

        int stars = Integer.parseInt(achievementsModels.get(position).getA_stars());

        mImageViewAchievementsStars[0].setImageResource(R.drawable.ic_starleftlocked);
        mImageViewAchievementsStars[1].setImageResource(R.drawable.ic_starmiddlelocked);
        mImageViewAchievementsStars[2].setImageResource(R.drawable.ic_starrightlocked);

        int[] unlockedStars = {
                R.drawable.ic_starleft,
                R.drawable.ic_starmiddle,
                R.drawable.ic_starright
        };

        for (int i = 0; i < stars; i++){
            mImageViewAchievementsStars[i].setImageResource(unlockedStars[i]);
        }

        mTextViewUserAchievementsNumberOfTriesText.setText(
                (Integer.parseInt(achievementsModels.get(position).getA_number_of_tries()) == 1) ? "Try:" : "Tries:");
        mTextViewUserAchievementsNumberOfTries.setText(achievementsModels.get(position).getA_number_of_tries());
        mTextViewUserAchievementsAverageTime.setText(achievementsModels.get(position).getA_time_finished());
        mTextViewUserAchievementsPoints.setText(achievementsModels.get(position).getA_description());
        return convertView;
    }
}
