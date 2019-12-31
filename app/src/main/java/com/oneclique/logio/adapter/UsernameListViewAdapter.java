package com.oneclique.logio.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oneclique.logio.AppCompatActivityHelper;
import com.oneclique.logio.R;

import java.util.List;

public class UsernameListViewAdapter extends BaseAdapter  {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<String> Usernames;

    public UsernameListViewAdapter(Activity activity, List<String> Usernames){
        this.context = activity.getApplicationContext();
        this.layoutInflater = LayoutInflater.from(this.context);
        this.Usernames = Usernames;
    }

    @Override
    public int getCount() {
        return Usernames.size();
    }

    @Override
    public Object getItem(int position) {
        return Usernames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_username, null, true);
        TextView mTextViewUsernameItem_ = convertView.findViewById(R.id.mTextViewUsernameItem);
        mTextViewUsernameItem_.setText(Usernames.get(position));

        return convertView;
    }
}
