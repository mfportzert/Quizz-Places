package com.quizz.places.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.quizz.core.imageloader.ImageLoader;
import com.quizz.core.imageloader.ImageLoader.ImageType;
import com.quizz.core.models.Level;
import com.quizz.core.utils.StringUtils;
import com.quizz.places.R;
import com.quizz.places.application.QuizzPlacesApplication;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class PictureFullscreenActivity extends Activity implements
		OnTouchListener {
	private static final String TAG = "Touch";
	
	public static final String EXTRA_LEVEL = "PictureFullscreenActivity.EXTRA_LEVEL"; 
	@SuppressWarnings("unused")
	private static final float MIN_ZOOM = 1f, MAX_ZOOM = 7.5f;

	// These matrices will be used to scale points of the image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// The 3 states (events) which the user is trying to perform
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// these PointF objects are used to record the point(s) the user is touching
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	
	// used to detect if the user has moved his finger before lifting it
	boolean mHasMoved = false;
	
	long mTouchDuration = 0;
	boolean mAutoRescaled = false;
	float mCurrentScale = 1f;
	float mTmpScale = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAutoRescaled = false;
		mCurrentScale = 1f;

		setContentView(R.layout.activity_picture_fullscreen);
		
		final ImageView contentView = (ImageView) findViewById(R.id.fullscreen_picture);
		TextView copyright = (TextView) findViewById(R.id.picture_copyright);
		contentView.setOnTouchListener(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(EXTRA_LEVEL)) {
			Level level = extras.getParcelable(EXTRA_LEVEL);
			if (!StringUtils.isEmpty(level.copyright)) {
				copyright.setText("© "+level.copyright);
				copyright.setVisibility(View.VISIBLE);
			}
			
			ImageLoader imageLoader = new ImageLoader(this);
			imageLoader.displayImage(QuizzPlacesApplication.IMAGES_DIR + level.imageName, 
					contentView, ImageType.NORMAL);
		}
	}
	
	private void autoRescale(ImageView view) {

		// drawable must be a bitmap
		Drawable drawable = view.getDrawable();
		if (drawable != null && drawable instanceof BitmapDrawable) {
			Bitmap image = ((BitmapDrawable) drawable).getBitmap();
			int imageWidth = image.getWidth();
			int imageHeight = image.getHeight();
			// Check that bitmap is already set and bigger than 0
			if (imageWidth > 0 && imageHeight > 0) {
				int screenHeight = view.getHeight();
				int screenWidth = view.getWidth();
				// calculate ratio screen/image
				float ratioHeight = (float) screenHeight / (float) imageHeight;
				float ratioWidth = (float) screenWidth / (float) imageWidth;
				if (ratioHeight <= 1 || ratioWidth <= 1) {
					// find the right scale (should be the smallest between width and height)
					float usedRatio = Math.min(ratioWidth, ratioHeight);
					matrix.postScale(usedRatio, usedRatio);
					
					// center image on screen
					int adjustedWidth = (int) (imageWidth * usedRatio);
					int adjustedHeight = (int) (imageHeight * usedRatio);
					int distX = (adjustedWidth < screenWidth) ? (screenWidth - adjustedWidth) / 2: 0;
					int distY = (adjustedHeight < screenHeight) ? (screenHeight - adjustedHeight) / 2: 0;
					matrix.postTranslate(distX, distY);
				}
				
				mAutoRescaled = true;
			}
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
		if (!mAutoRescaled) {
			// The matrix should start at scale of 1f if the bitmap is bigger than the screen
			// and has been scaled during display..
			autoRescale(view);
		}
		view.setScaleType(ImageView.ScaleType.MATRIX);

		dumpEvent(event);
		// Handle touch events here...

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: // first finger down only
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG"); // write to LogCat
			mode = DRAG;
			mHasMoved = false;
			mTouchDuration = System.currentTimeMillis();
			break;

		case MotionEvent.ACTION_UP: // first finger lifted
			mTouchDuration = System.currentTimeMillis() - mTouchDuration;
			if (!mHasMoved && mTouchDuration < 200) {
				// closes fullscreen for simple tap
				finish();
			}
			break;

		case MotionEvent.ACTION_POINTER_UP: // second finger lifted

			mode = NONE;
			Log.d(TAG, "mode=NONE");
			mCurrentScale *= mTmpScale;
			break;

		case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down
			
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 5f) {
				savedMatrix.set(matrix);
				mTmpScale = 1f;
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;

		case MotionEvent.ACTION_MOVE:

			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y); // create the transformation in the matrix
									// of points
			} else if (mode == ZOOM) {
				// pinch zooming
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 5f) {
					matrix.set(savedMatrix);
					mTmpScale = newDist / oldDist; // setting the scaling of the
												// matrix...if scale > 1 means
												// zoom in...if scale < 1 means
												// zoom out
					/*
					float realScale = mInitialScale * tmpScale;
					Log.e("dsfsdfsd", "INITIAL SCALE: "+ mInitialScale + ", TMP SCALE: "+tmpScale+", REAL SCALE: "+realScale);
					*/
					float realScale = mCurrentScale * mTmpScale;
					if (realScale < MIN_ZOOM) {
						mTmpScale *= (MIN_ZOOM / realScale);
					}
					
					if (realScale > MAX_ZOOM) {
						mTmpScale *= (MAX_ZOOM / realScale);
					}

					matrix.postScale(mTmpScale, mTmpScale, mid.x, mid.y);
				}
			}
			mHasMoved = true;
			break;
		}

		view.setImageMatrix(matrix); // display the transformation on screen

		return true; // indicate event was handled
	}

	/*
	 * --------------------------------------------------------------------------
	 * Method: spacing Parameters: MotionEvent Returns: float Description:
	 * checks the spacing between the two fingers on touch
	 * ----------------------------------------------------
	 */

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/*
	 * --------------------------------------------------------------------------
	 * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
	 * Description: calculates the midpoint between the two fingers
	 * ------------------------------------------------------------
	 */

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);

		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}

		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}

		sb.append("]");
		Log.d("Touch Events ---------", sb.toString());
	}
}
