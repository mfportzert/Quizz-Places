package com.quizz.places.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AntiAliasPicture extends ImageView {

	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG|Paint.DITHER_FLAG);
	private Rect mPictureRect = new Rect();
	private Matrix mPictureMatrix = new Matrix(); 
	private boolean mImageReady = false;
	
	private int mPadLeft;
	private int mPadTop;
	private int mPadRight;
	private int mPadBottom;
	private int mBmpWidth;
	private int mBmpHeight;
	
	public AntiAliasPicture(Context context) {
		super(context);
	}
	
	public AntiAliasPicture(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		mImageReady = false;
		mPictureMatrix.reset();
		super.setImageBitmap(bm);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (!mImageReady) {
			Bitmap bitmap;
			if (getDrawable() != null && getDrawable() instanceof BitmapDrawable) {
				bitmap = ((BitmapDrawable) getDrawable()).getBitmap();

				mPadLeft = getPaddingLeft();
				mPadTop = getPaddingTop();
				mPadRight = getPaddingRight();
				mPadBottom = getPaddingBottom();

				mPictureRect.set(mPadLeft, mPadTop, 
						getWidth() - mPadRight, getHeight() - mPadBottom);
				
				mBmpWidth = getWidth() - mPadLeft - mPadRight;
				mBmpHeight = getHeight() - mPadTop - mPadBottom;
				if (mBmpWidth > 0 && mBmpHeight > 0) {
				    Bitmap newbitmap = Bitmap.createScaledBitmap(bitmap, mBmpWidth, mBmpHeight, false);
				    
				    BitmapShader bitmapShader = new BitmapShader(newbitmap,
			                TileMode.CLAMP, TileMode.CLAMP);
			        
				    mPictureMatrix.postTranslate(mPadLeft, mPadTop); 
			        bitmapShader.setLocalMatrix(mPictureMatrix); 
		
			        mPaint.setShader(bitmapShader);
			        mImageReady = true;
				}
			} else {
				return;
			}
		}
		
		canvas.drawRect(mPictureRect, mPaint);
	}	
}
