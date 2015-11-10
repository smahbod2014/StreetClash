package com.cse190sc.streetclash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String>
{
    public CustomAdapter(Context context, String[] skills) {
        super(context, R.layout.skills_row_vertical, skills);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.skills_row_vertical, parent, false);

        String skill = getItem(position);
        TextView item1 = (TextView) customView.findViewById(R.id.tv_skill);

        item1.setText(skill);

        return customView;
    }
}
