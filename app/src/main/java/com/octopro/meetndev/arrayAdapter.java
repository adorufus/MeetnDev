package com.octopro.meetndev;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.cardview.widget.CardView;

public class arrayAdapter extends ArrayAdapter<Cards> {
	Context context;

	public arrayAdapter(Context context, int resourceId, List<Cards> items){
		super(context, resourceId, items);
	}

	public View getView(int position, View convertView, ViewGroup parent){
		Cards card_item = getItem(position);

		if(convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
		}

		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView skills = (TextView) convertView.findViewById(R.id.skills);
		ImageView image = (ImageView) convertView.findViewById(R.id.imageHolder);

		name.setText(card_item.getName());
		skills.setText(card_item.getUserSkills());
		Glide.with(getContext()).load(card_item.getUserImageUrl()).into(image);

		return convertView;
	}

}
