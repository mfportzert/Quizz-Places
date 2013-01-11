package com.quizz.places.adapters;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.SparseArray;
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
	private SparseArray<Drawable> mPictures = new SparseArray<Drawable>();
	
	public LevelsItemAdapter(Context context, int lineLayout) {
        super(context, lineLayout);
        
        mLineLayout = lineLayout;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	static class ViewHolder {
		int position;
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
            holder.picture.setVisibility(View.GONE);
            
            convertView.setTag(holder);
            
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Level level = getItem(position);
        holder.position = position;
        if (mPictures.get(position) != null) {
        	holder.picture.setImageDrawable(mPictures.get(position));
        } else {
        	// FIXME: position system is probably not working
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
	        	mPictures.append(mPosition, drawable);
	            mHolder.picture.setImageDrawable(drawable);
	            mHolder.picture.setVisibility(View.VISIBLE);
	            
/*	            Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
	    		mHolder.picture.startAnimation(animation);*/
	        }
	    }
	}
}
