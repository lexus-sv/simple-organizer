package com.example.densetsu.customSpinner;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.densetsu.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MarkerAdapter extends ArrayAdapter<MarkerItem> {
    Resources resources;
    public MarkerAdapter(Context context, ArrayList<MarkerItem> markerList) {
        super(context, 0, markerList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_spinner, parent, false
            );
        }

        CircleImageView circleImageView = convertView.findViewById(R.id.civ);
        TextView textView = convertView.findViewById(R.id.spin_tv);

        MarkerItem markerItem = getItem(position);

        if (markerItem != null) {
            circleImageView.setBackgroundColor(Color.DKGRAY);
            circleImageView.setImageResource(markerItem.getmImage());
            textView.setText(markerItem.getmCategory());
        }
        return convertView;
    }
}
