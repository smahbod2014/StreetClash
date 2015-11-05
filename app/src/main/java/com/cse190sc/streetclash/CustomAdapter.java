package com.cse190sc.streetclash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String[]> {

    public CustomAdapter(Context context, String[][] skills) {
        super(context, R.layout.skills_row, skills);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.skills_row, parent, false);

        String[] skillSet = getItem(position);
        TextView item1 = (TextView) customView.findViewById(R.id.tv_item1);
        TextView item2 = (TextView) customView.findViewById(R.id.tv_item2);
        TextView item3 = (TextView) customView.findViewById(R.id.tv_item3);

        item1.setText(skillSet[0]);
        item2.setText(skillSet[1]);
        item3.setText(skillSet[2]);

        return customView;
    }
}
