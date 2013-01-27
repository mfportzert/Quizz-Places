package com.quizz.places.adapters;

import java.lang.ref.SoftReference;
import java.util.Random;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.listeners.LoadAdapterPictureListener;
import com.quizz.core.models.Level;
import com.quizz.core.tasks.LoadAdapterPictureTask;
import com.quizz.places.R;
import com.quizz.places.application.QuizzPlacesApplication;

public class LevelsItemAdapter extends ArrayAdapter<Level> implements LoadAdapterPictureListener {

    @SuppressWarnings("unused")
    private static final String DIFFICULTY_EASY = "easy";
    private static final String DIFFICULTY_MEDIUM = "medium";
    private static final String DIFFICULTY_HARD = "hard";

    private static final float DEFAULT_RANDOM_ROTATION_RANGE = 12f;

    private int mLineLayout;
    private LayoutInflater mInflater;
    private SparseArray<SoftReference<Drawable>> mPictures = new SparseArray<SoftReference<Drawable>>();
    private SparseArray<Float> mRotations = new SparseArray<Float>();

    public LevelsItemAdapter(Context context, int lineLayout) {
	super(context, lineLayout);

	mLineLayout = lineLayout;
	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
	int position;
	boolean hidden;
	RelativeLayout levelLayout;
	ImageView picture;
	LinearLayout difficulty;
	ImageView statusIcon;
	ImageView easyStar;
	ImageView mediumStar;
	ImageView hardStar;
	ObjectAnimator alphaAnim;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	ViewHolder holder;
	if (convertView == null) {
	    convertView = this.mInflater.inflate(this.mLineLayout, null);

	    holder = new ViewHolder();
	    holder.levelLayout = (RelativeLayout) convertView;
	    holder.picture = (ImageView) convertView.findViewById(R.id.levelPicture);
	    holder.statusIcon = (ImageView) convertView.findViewById(R.id.levelStatusIcon);
	    holder.difficulty = (LinearLayout) convertView.findViewById(R.id.levelDifficulty);
	    holder.easyStar = (ImageView) convertView.findViewById(R.id.levelStarEasy);
	    holder.mediumStar = (ImageView) convertView.findViewById(R.id.levelStarMedium);
	    holder.hardStar = (ImageView) convertView.findViewById(R.id.levelStarHard);

	    convertView.setTag(holder);
	} else {
	    holder = (ViewHolder) convertView.getTag();
	}

	Level level = getItem(position);
	holder.position = position;

	Drawable picture = null;
	SoftReference<Drawable> pictureRef = mPictures.get(position);
	if (pictureRef != null) {
	    picture = pictureRef.get();
	}

	holder.mediumStar.setEnabled(true);
	holder.hardStar.setEnabled(true);

	if (level.difficulty.equals(DIFFICULTY_MEDIUM)) {
	    holder.hardStar.setEnabled(false);
	} else if (!level.difficulty.equals(DIFFICULTY_HARD)) {
	    holder.mediumStar.setEnabled(false);
	    holder.hardStar.setEnabled(false);
	}

	if (picture != null) {
	    holder.picture.setImageDrawable(picture);
	    if (mRotations.get(position) != null) {
		ObjectAnimator.ofFloat(holder.picture, "rotation", 0.0f, mRotations.get(position))
			.setDuration(0).start();
	    }
	    adjustIconStatusPosition(holder, picture);

	} else {
	    if (holder.alphaAnim != null && holder.alphaAnim.isRunning()) {
		holder.alphaAnim.cancel();
	    }

	    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(holder.levelLayout, "alpha", 0f);
	    alphaAnimator.setDuration(0);
	    alphaAnimator.start();

	    holder.hidden = true;

	    new LoadAdapterPictureTask(getContext(), QuizzPlacesApplication.IMAGES_DIR
		    + level.imageName, position, holder, this).execute();
	}

	return convertView;
    }

    private void adjustIconStatusPosition(ViewHolder viewHolder, Drawable drawable) {

	int[] pictureCoordinates = new int[2];
	viewHolder.picture.getLocationOnScreen(pictureCoordinates);

	int[] statusIconCoordinates = new int[2];
	viewHolder.statusIcon.getLocationOnScreen(statusIconCoordinates);

	ObjectAnimator
		.ofFloat(
			viewHolder.statusIcon,
			"x",
			0.0f,
			(pictureCoordinates[0] - statusIconCoordinates[0])
				+ (drawable.getIntrinsicWidth() / 4)).setDuration(0).start();

	ObjectAnimator
		.ofFloat(
			viewHolder.statusIcon,
			"y",
			0.0f,
			(pictureCoordinates[1] - statusIconCoordinates[1])
				- (drawable.getIntrinsicHeight() / 4)).setDuration(0).start();
    }

    @Override
    public void onPictureLoaded(Drawable drawable, int position, Object tag) {
	drawable.setFilterBitmap(true);
	((BitmapDrawable)drawable).setAntiAlias(true);
	
	ViewHolder viewHolder = (ViewHolder) tag;
	if (viewHolder.position == position) {
	    mPictures.append(position, new SoftReference<Drawable>(drawable));
	    viewHolder.picture.setImageDrawable(drawable);
	    
	    if (viewHolder.hidden) {
		viewHolder.alphaAnim = ObjectAnimator.ofFloat(viewHolder.levelLayout, "alpha", 0f,
			1f).setDuration(200);
		viewHolder.alphaAnim.start();

		Random random = new Random();
		float rotationRatio = random.nextFloat() * DEFAULT_RANDOM_ROTATION_RANGE;
		mRotations.append(position, rotationRatio - (DEFAULT_RANDOM_ROTATION_RANGE / 2));
		
		viewHolder.picture.getDrawable().setFilterBitmap(true);
		((BitmapDrawable)viewHolder.picture.getDrawable()).setAntiAlias(true);
		
		ObjectAnimator
			.ofFloat(viewHolder.picture, "rotation", 0.0f, mRotations.get(position))
			.setDuration(0).start();
		
		adjustIconStatusPosition(viewHolder, drawable);
		viewHolder.hidden = false;
	    }
	}
    }
}
