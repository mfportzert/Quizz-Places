package com.quizz.places.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.LinearInterpolator;

import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.quizz.places.R;
import com.quizz.places.widgets.RotatingSunEffect;

public class LevelSuccessDialog extends Activity {

	private ValueAnimator mBackgroundRotationAnimator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		// To remove the background of the 'Dialog'
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		setContentView(R.layout.dialog_level_success);
		
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
}
