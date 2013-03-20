package com.quizz.places.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.quizz.places.R;
import com.quizz.places.widgets.RotatingSunEffect;

public class LevelSuccessDialog extends Activity {
	public static final int RESULT_CODE_CLOSE = 1;
	public static final int RESULT_CODE_NEXT = 2;
	
	private ValueAnimator mBackgroundRotationAnimator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		// To remove the background of the 'Dialog'
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		setContentView(R.layout.dialog_level_success);
		Button closeButton = (Button) findViewById(R.id.level_success_close_button);
		Button nextButton = (Button) findViewById(R.id.level_success_next_button);
		closeButton.setOnClickListener(mOnCloseButtonClickListener);
		nextButton.setOnClickListener(mOnNextButtonClickListener);
		
		final RotatingSunEffect background = (RotatingSunEffect) findViewById(R.id.level_success_background_effect);
		
		mBackgroundRotationAnimator = ValueAnimator.ofFloat(0, 360);
		mBackgroundRotationAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {

                background.rotationDegrees = (Float) arg0.getAnimatedValue();
                background.invalidate();
            }
        });
		mBackgroundRotationAnimator.setInterpolator(new LinearInterpolator());
		mBackgroundRotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
		mBackgroundRotationAnimator.setDuration(6500);
		mBackgroundRotationAnimator.start();
	}
	
	@Override
	protected void onDestroy() {
		if (mBackgroundRotationAnimator != null && mBackgroundRotationAnimator.isRunning()) {
			mBackgroundRotationAnimator.end();
		}
		super.onDestroy();
	}
	
	public void close(int result) {
		Intent returnIntent = new Intent();
		setResult(result, returnIntent);
		finish();
	}
	
	OnClickListener mOnCloseButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			close(RESULT_CODE_CLOSE);
		}
	};

	OnClickListener mOnNextButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			close(RESULT_CODE_NEXT);
		}
	};
}
