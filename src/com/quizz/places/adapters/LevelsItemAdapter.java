package com.quizz.places.adapters;

import java.io.IOException;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.quizz.core.models.Level;
import com.quizz.places.R;

public class LevelsItemAdapter extends ArrayAdapter<Level> {

	private int mLineLayout;
	private LayoutInflater mInflater;
	
	public LevelsItemAdapter(Context context, int lineLayout) {
        super(context, lineLayout);
        
        mLineLayout = lineLayout;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	static class ViewHolder {
        ImageView picture;
        ImageView difficulty;
        ImageView statusIcon;
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            
        	convertView = this.mInflater.inflate(this.mLineLayout, null);
        	
            holder = new ViewHolder();
            holder.picture = (ImageView) convertView.findViewById(R.id.levelPicture);
            holder.difficulty = (ImageView) convertView.findViewById(R.id.levelDifficulty);
            holder.statusIcon = (ImageView) convertView.findViewById(R.id.levelStatusIcon);
            
            convertView.setTag(holder);
            
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Level level = getItem(position);
        Drawable d;
		try {
			d = Drawable.createFromStream(getContext().getAssets().open("pictures/big_ben.jpg"), null);
			holder.picture.setImageDrawable(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return convertView;
    }
}
