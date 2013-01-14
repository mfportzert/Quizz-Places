package com.quizz.places.adapters;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.quizz.core.models.Level;
import com.quizz.places.R;

public class LevelsItemAdapter extends ArrayAdapter<Level> {

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
        	new ThumbnailTask(position, holder).execute();
        }
        
        return convertView;
    }
	
	private class ThumbnailTask extends AsyncTask<Void, Void, Drawable> {
	    private int mPosition;
	    private ViewHolder mHolder;

	    public ThumbnailTask(int position, ViewHolder holder) {
	        mPosition = position;
	        mHolder = holder;
	    }

	    @Override
	    protected Drawable doInBackground(Void... arg0) {
	    	try {
	    		return Drawable.createFromStream(getContext().getAssets().open("pictures/big_ben.jpg"), null);
		    } catch (IOException e) {
				e.printStackTrace();
			}
	    	return null;
	    }

	    @Override
	    protected void onPostExecute(Drawable drawable) {
	        if (mHolder.position == mPosition) {
	        	mPictures.append(mPosition, new WeakReference<Drawable>(drawable));
	            mHolder.picture.setImageDrawable(drawable);
	            /*
	            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
	    		mHolder.picture.startAnimation(animation);*/
	    		mHolder.picture.setVisibility(View.VISIBLE);
	    		mHolder.difficulty.setVisibility(View.VISIBLE);
	        }
	    }
	}
}
