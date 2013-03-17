package com.quizz.places.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class RotatingSunEffect extends View {

	private static final int RAYS_COLOR = 0x22FFB90F;

	public float rotationDegrees = 0f;
	private Point mCenterPoint = new Point();
	private Point mPointB = new Point();
	private Point mPointC = new Point();
	private Paint mRayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint mInnerGlowPaint = new Paint();

	private Bitmap mSavedRaysBitmap = null;
	private Canvas mSavedRaysCanvas = null;

	private Bitmap mSavedInnerGlowBitmap = null;
	private Canvas mSavedInnerGlowCanvas = null;

	private Path mRaysPath = new Path();
	private RadialGradient mInnerGlowGradient;

	private void init() {
		mRayPaint.setStrokeWidth(2);
		mRayPaint.setColor(RAYS_COLOR);
		mRayPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mRayPaint.setAntiAlias(true);

		mInnerGlowPaint = new Paint();
		mInnerGlowPaint.setColor(0xFF000000);
		mInnerGlowPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
	}

	public RotatingSunEffect(Context context) {
		super(context);
		init();
	}

	public RotatingSunEffect(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RotatingSunEffect(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	private void drawFoggyWindowWithTransparentCircle(Canvas canvas,
			Bitmap bitmap, float circleX, float circleY, float radius) {

		if (mSavedInnerGlowBitmap == null) {
			// Create a temporary bitmap
			mSavedInnerGlowBitmap = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Bitmap.Config.ARGB_8888);
			mSavedInnerGlowCanvas = new Canvas(mSavedInnerGlowBitmap);
		}

		// Clear the bitmap of previous drawing
		mSavedInnerGlowBitmap.eraseColor(Color.TRANSPARENT);

		// Copy bitmap into tempBitmap
		mSavedInnerGlowCanvas.drawBitmap(bitmap, 0, 0, null);

		// Apply the gradient
		mInnerGlowPaint.setShader(mInnerGlowGradient);

		// Draw transparent circle into tempBitmap
		mSavedInnerGlowCanvas.drawCircle(circleX, circleY, radius,
				mInnerGlowPaint);

		// Draw tempBitmap onto the screen (over what's already there)
		canvas.drawBitmap(mSavedInnerGlowBitmap, 0, 0, null);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		// Initialize only once
		if (mSavedRaysBitmap == null) {
			mSavedRaysBitmap = Bitmap.createBitmap((int) getWidth(),
					(int) getHeight(), Config.ARGB_4444);
			mSavedRaysCanvas = new Canvas(mSavedRaysBitmap);

			mCenterPoint.x = (int) (getWidth() * 0.4f);
			mCenterPoint.y = (int) (getHeight());

			// Create a radial gradient
			mInnerGlowGradient = new RadialGradient(mCenterPoint.x,
					mCenterPoint.y, getWidth() / 1.6f, 0xFF000000, 0x00000000,
					Shader.TileMode.CLAMP);
		} else {

			// Clear the bitmap of previous drawing
			mSavedRaysBitmap.eraseColor(Color.TRANSPARENT);

			// angle lumineux : 20
			// angle ecart : 40
			
			int distance = (int) Math.sqrt(Math.pow(getWidth(), 2)
					+ Math.pow(getHeight(), 2));
			int rotationStart = (int) rotationDegrees;
			for (int angle = rotationStart; angle < rotationStart + 360; angle += 60) {

				double thetaB = Math.toRadians(angle);
				mPointB.x = (int) (mCenterPoint.x + distance * Math.cos(thetaB));
				mPointB.y = (int) (mCenterPoint.y + distance * Math.sin(thetaB));

				double thetaC = Math.toRadians(angle + 20);
				mPointC.x = (int) (mCenterPoint.x + distance * Math.cos(thetaC));
				mPointC.y = (int) (mCenterPoint.y + distance * Math.sin(thetaC));

				mRaysPath.rewind();
				mRaysPath.setFillType(Path.FillType.EVEN_ODD);
				mRaysPath.moveTo(mCenterPoint.x, mCenterPoint.y);
				mRaysPath.lineTo(mPointB.x, mPointB.y);
				mRaysPath.lineTo(mPointC.x, mPointC.y);
				mRaysPath.close();

				thetaB = Math.toRadians(angle + 20);
				mPointB.x = (int) (mCenterPoint.x + distance * Math.cos(thetaB));
				mPointB.y = (int) (mCenterPoint.y + distance * Math.sin(thetaB));

				thetaC = Math.toRadians(angle + 60);
				mPointC.x = (int) (mCenterPoint.x + distance * Math.cos(thetaC));
				mPointC.y = (int) (mCenterPoint.y + distance * Math.sin(thetaC));

				mSavedRaysCanvas.drawPath(mRaysPath, mRayPaint);
			}
		}

		drawFoggyWindowWithTransparentCircle(canvas, mSavedRaysBitmap,
				mCenterPoint.x, mCenterPoint.y, getWidth() / 1.6f);
	}
}
