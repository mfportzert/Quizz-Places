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

				int left = getPaddingLeft();
				int top = getPaddingTop();
				int right = getPaddingRight();
				int bottom = getPaddingBottom();

				mPictureRect.set(getPaddingLeft(), getPaddingTop(), 
						getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
				
			    Bitmap newbitmap = Bitmap.createScaledBitmap(bitmap, getWidth() - left - right, 
			    		getHeight() - top - bottom, false);
			    
			    BitmapShader bitmapShader = new BitmapShader(newbitmap,
		                TileMode.CLAMP, TileMode.CLAMP);
		        
			    mPictureMatrix.postTranslate(left, top); 
		        bitmapShader.setLocalMatrix(mPictureMatrix); 

		        mPaint.setShader(bitmapShader);
		        mImageReady = true;
			} else {
				return;
			}
		}
		
		canvas.drawRect(mPictureRect, mPaint);
	}	
}
