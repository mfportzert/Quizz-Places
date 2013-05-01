package com.quizz.places.widgets;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class PinchZoomImageView extends ImageView {

	static final int MAX_SCALE_FACTOR = 2;

	// These matrices will be used to scale points of the image
	private Matrix mViewMatrix = new Matrix();
	private Matrix mCurSavedMatrix = new Matrix();
	// These PointF objects are used to record the point(s) the user is touching
	private PointF start = new PointF();
	private PointF mCurMidPoint = new PointF();
	private float mOldDist = 1f;

	private Matrix mMinScaleMatrix;
	private float mMinScale;
	private float mMaxScale;
	float[] mTmpValues = new float[9];
	private boolean mWasScaleTypeSet;
	private Mode mCurrentMode = Mode.NONE;

	enum Mode {
		NONE, DRAG, ZOOM
	}

	public PinchZoomImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PinchZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// Getting initial Image matrix
		mViewMatrix = new Matrix(this.getImageMatrix());
		mMinScaleMatrix = new Matrix(mViewMatrix);
		float initialScale = getMatrixScale(mViewMatrix);

		if (initialScale < 1.0f) // Image is bigger than screen
			mMaxScale = MAX_SCALE_FACTOR;
		else
			mMaxScale = MAX_SCALE_FACTOR * initialScale;

		mMinScale = getMatrixScale(mMinScaleMatrix);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// We set scale only after onMeasure was called and automatically fit
		// image to screen
		if (!mWasScaleTypeSet) {
			setScaleType(ImageView.ScaleType.MATRIX);
			mWasScaleTypeSet = true;
		}

		float scale;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: // first finger down only
			mCurSavedMatrix.set(mViewMatrix);
			start.set(event.getX(), event.getY());
			mCurrentMode = Mode.DRAG;
			break;

		case MotionEvent.ACTION_UP: // first finger lifted
		case MotionEvent.ACTION_POINTER_UP: // second finger lifted
			mCurrentMode = Mode.NONE;

			float resScale = getMatrixScale(mViewMatrix);

			if (resScale > mMaxScale) {
				downscaleMatrix(resScale, mViewMatrix);
			} else if (resScale < mMinScale)
				mViewMatrix = new Matrix(mMinScaleMatrix);
			else if ((resScale - mMinScale) < 0.1f) // Don't allow user to drag
													// picture outside in case
													// of FIT TO WINDOW zoom
				mViewMatrix = new Matrix(mMinScaleMatrix);
			else
				break;

			break;

		case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down
			mOldDist = spacing(event);
			if (mOldDist > 5f) {
				mCurSavedMatrix.set(mViewMatrix);
				midPoint(mCurMidPoint, event);
				mCurrentMode = Mode.ZOOM;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (mCurrentMode == Mode.DRAG) {
				mViewMatrix.set(mCurSavedMatrix);
				mViewMatrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y); // create the transformation in the matrix
									// of points
			} else if (mCurrentMode == Mode.ZOOM) {
				// pinch zooming
				float newDist = spacing(event);
				if (newDist > 1.f) {
					mViewMatrix.set(mCurSavedMatrix);
					scale = newDist / mOldDist; // setting the scaling of the
												// matrix...if scale > 1 means
												// zoom in...if scale < 1 means
												// zoom out
					mViewMatrix.postScale(scale, scale, mCurMidPoint.x,
							mCurMidPoint.y);
				}
			}
			break;
		}

		setImageMatrix(mViewMatrix); // display the transformation on
										// screen

		return true; // indicate event was handled
	}

	/**
	 * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
	 * Description: calculates the midpoint between the two fingers
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/**
	 * Method: spacing Parameters: MotionEvent Returns: float Description:
	 * checks the spacing between the two fingers on touch
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * Returns scale factor of the Matrix
	 * 
	 * @param matrix
	 * @return
	 */
	private float getMatrixScale(Matrix matrix) {
		matrix.getValues(mTmpValues);
		return mTmpValues[Matrix.MSCALE_X];
	}

	/**
	 * Downscales matrix with the scale to maximum allowed scale factor, but the
	 * same translations
	 * 
	 * @param scale
	 * @param dist
	 */
	private void downscaleMatrix(float scale, Matrix dist) {
		float resScale = mMaxScale / scale;
		dist.postScale(resScale, resScale, mCurMidPoint.x, mCurMidPoint.y);
	}

}
