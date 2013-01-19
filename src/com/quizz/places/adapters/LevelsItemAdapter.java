package com.quizz.places.adapters;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.quizz.core.listeners.LoadAdapterPictureListener;
import com.quizz.core.models.Level;
import com.quizz.core.tasks.LoadAdapterPictureTask;
import com.quizz.places.R;

public class LevelsItemAdapter extends ArrayAdapter<Level> implements LoadAdapterPictureListener {

	private int mLineLayout;
	private LayoutInflater mInflater;
	private SparseArray<WeakReference<Drawable>> mPictures = new SparseArray<WeakReference<Drawable>>();
	
	public LevelsItemAdapter(Context context, int lineLayout) {
        super(context, lineLayout);
        
        mLineLayout = lineLayout;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	static class ViewHolder {
		int position;
        ImageView picture;
        LinearLayout difficulty;
        ImageView statusIcon;
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
        	convertView = this.mInflater.inflate(this.mLineLayout, null);
        	
            holder = new ViewHolder();
            holder.picture = (ImageView) convertView.findViewById(R.id.levelPicture);
            holder.difficulty = (LinearLayout) convertView.findViewById(R.id.levelDifficulty);
            holder.statusIcon = (ImageView) convertView.findViewById(R.id.levelStatusIcon);
            //holder.picture.setAlpha(0);
            holder.picture.setVisibility(View.GONE);
            holder.difficulty.setVisibility(View.GONE);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Level level = getItem(position);
        holder.position = position;
        
        Drawable picture = null;
        WeakReference<Drawable> pictureRef = mPictures.get(position);
        if (pictureRef != null) picture = pictureRef.get();
        
        if (picture != null) {
        	holder.picture.setImageDrawable(picture);
            holder.difficulty.setVisibility(View.VISIBLE);
        } else {
        	new LoadAdapterPictureTask(getContext(), position, holder, this).execute();
        }
        
        return convertView;
    }

	@Override
	public void onPictureLoaded(Drawable drawable, int position, Object tag) {
		ViewHolder viewHolder = (ViewHolder) tag;
		if (viewHolder.position == position) {
			mPictures.append(position, new WeakReference<Drawable>(drawable));
			viewHolder.picture.setImageDrawable(drawable);
			/*
			 * Animation animation = AnimationUtils.loadAnimation(getContext(),
			 * R.anim.fade_in); mHolder.picture.startAnimation(animation);
			 */
			viewHolder.picture.setVisibility(View.VISIBLE);
			viewHolder.difficulty.setVisibility(View.VISIBLE);
		}
	}
}
