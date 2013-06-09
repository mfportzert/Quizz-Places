package com.quizz.places.adapters;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.imageloader.ImageLoader;
import com.quizz.core.imageloader.ImageLoader.ImageLoaderListener;
import com.quizz.core.imageloader.ImageLoader.ImageType;
import com.quizz.core.models.Level;
import com.quizz.places.R;
import com.quizz.places.application.QuizzPlacesApplication;

public class LevelsItemAdapter extends ArrayAdapter<Level> {

	private static final float DEFAULT_RANDOM_ROTATION_RANGE = 10f;

	private int mLineLayout;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private Random mRandom = new Random();
	private OnPictureClickListener mPictureClickListener;
	
	public LevelsItemAdapter(Context context, int lineLayout) {
		super(context, lineLayout);

		mLineLayout = lineLayout;
		mImageLoader = new ImageLoader(context);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	class ViewHolder implements ImageLoaderListener {
		int position;
		RelativeLayout levelLayout;
		View pictureLayout;
		ImageView picture;
		ImageView statusIcon;
		ObjectAnimator alphaAnim;
		String currentPicturePath = "";

		@Override
		public void onImageLoaded(Bitmap bitmap, String url,
				ImageView imageView, ImageType imageType, boolean fromCache) {

			if (!fromCache) {
				alphaAnim = ObjectAnimator
						.ofFloat(levelLayout, "alpha", 0f, 1f).setDuration(200);
				alphaAnim.start();
			}

			Level level = LevelsItemAdapter.this.getItem(position);
			if (level.rotation == 0) {
				float rotationRatio = mRandom.nextFloat() * DEFAULT_RANDOM_ROTATION_RANGE;
				level.rotation = rotationRatio - (DEFAULT_RANDOM_ROTATION_RANGE / 2);
			}

			ObjectAnimator.ofFloat(
					imageView, "rotation", 0.0f, level.rotation).setDuration(0).start();

			if (getItem(position).status == Level.STATUS_LEVEL_CLEAR) {
				statusIcon.setVisibility(View.VISIBLE);
			}
			
			picture.setVisibility(View.VISIBLE);
			picture.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						float pivotX = picture.getLeft() + (picture.getWidth() / 2);
						float pivotY = picture.getTop() + (picture.getHeight() / 2);

						AnimatorSet animSet = new AnimatorSet();
						animSet.playTogether(
								ObjectAnimator.ofFloat(picture, "scaleX", 1f, 1.1f),
								ObjectAnimator.ofFloat(picture, "scaleY", 1f, 1.1f));
						animSet.setDuration(0);
						animSet.start();
					} else if (event.getAction() == MotionEvent.ACTION_UP
							|| event.getAction() == MotionEvent.ACTION_OUTSIDE
							|| event.getAction() == MotionEvent.ACTION_CANCEL) {
						
						if (event.getAction() == MotionEvent.ACTION_UP) {
							if (mPictureClickListener != null) {
								// Send click event
								mPictureClickListener.onClick(levelLayout, picture, position);
								return true;
							}
						}
						
						float pivotX = picture.getLeft() + (picture.getWidth() / 2);
						float pivotY = picture.getTop() + (picture.getHeight() / 2);

						AnimatorSet animSet = new AnimatorSet();
						animSet.playTogether(
								ObjectAnimator.ofFloat(picture, "scaleX", 1.1f, 1f),
								ObjectAnimator.ofFloat(picture, "scaleY", 1.1f, 1f));
						animSet.setDuration(0);
						animSet.start();
					}
					return true;
				}
			});
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = this.mInflater.inflate(this.mLineLayout, null);

			holder = new ViewHolder();
			holder.levelLayout = (RelativeLayout) convertView;
			holder.picture = (ImageView) convertView
					.findViewById(R.id.levelPicture);
			holder.pictureLayout = convertView
					.findViewById(R.id.levelPictureLayout);
			holder.statusIcon = (ImageView) convertView
					.findViewById(R.id.levelStatusIcon);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Level level = getItem(position);
		String imagePath = QuizzPlacesApplication.IMAGES_DIR + level.imageName;
		if (holder.currentPicturePath.compareTo(imagePath) != 0) {
			holder.position = position;
			holder.currentPicturePath = imagePath;
			
			holder.statusIcon.setVisibility(View.INVISIBLE);
			holder.picture.setVisibility(View.INVISIBLE);
			
			mImageLoader.displayImage(imagePath, holder.picture, ImageType.SMALL, holder);
		}

		return convertView;
	}
	
	public void setOnPictureClickListener(OnPictureClickListener listener) {
		mPictureClickListener = listener;
	}
	
	/**
	 * Triggered inside onTouch Action Up (needed more flexibility with the touch events)
	 * 
	 * @author M-F.P
	 *
	 */
	public interface OnPictureClickListener {
		public void onClick(View itemView, ImageView pictureView, int position);
	}
}
