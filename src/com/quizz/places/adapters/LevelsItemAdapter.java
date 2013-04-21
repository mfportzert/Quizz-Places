package com.quizz.places.adapters;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.imageloader.ImageLoader;
import com.quizz.core.imageloader.ImageLoader.ImageLoaderListener;
import com.quizz.core.imageloader.ImageLoader.ImageType;
import com.quizz.core.models.Level;
import com.quizz.places.R;
import com.quizz.places.application.QuizzPlacesApplication;

public class LevelsItemAdapter extends ArrayAdapter<Level> {

	private static final float DEFAULT_RANDOM_ROTATION_RANGE = 12f;

	private int mLineLayout;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private Random mRandom = new Random();

	
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

		@Override
		public void onStartImageLoading(Bitmap bitmap, String url,
				ImageView imageView, ImageType imageType) {

			if (alphaAnim != null && alphaAnim.isRunning()) {
				alphaAnim.cancel();
			}

			ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(levelLayout,
					"alpha", 0f);
			alphaAnimator.setDuration(0);
			alphaAnimator.start();
		}

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
		holder.position = position;

		holder.statusIcon.setVisibility(View.INVISIBLE);
		holder.picture.setVisibility(View.INVISIBLE);

		mImageLoader.displayImage(QuizzPlacesApplication.IMAGES_DIR
				+ level.imageName, holder.picture, ImageType.LOCAL, holder);

		return convertView;
	}
}
