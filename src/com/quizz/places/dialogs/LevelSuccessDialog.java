package com.quizz.places.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;

import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.quizz.core.fragments.BaseLevelFragment;
import com.quizz.core.interfaces.FragmentContainer;
import com.quizz.core.utils.NavigationUtils;
import com.quizz.places.R;
import com.quizz.places.adapters.LevelsItemAdapter;
import com.quizz.places.fragments.LevelFragment;
import com.quizz.places.widgets.RotatingSunEffect;

public class LevelSuccessDialog extends Activity {

	private ValueAnimator mBackgroundRotationAnimator;
	private LevelSuccessDialogListener mLevelSuccessDialogListener;
	
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
	
	public void setLevelSuccessDialogListener(LevelSuccessDialogListener listener) {
		mLevelSuccessDialogListener = listener;
	}
	
	public void close() {
		finish();		
	}
	
	OnClickListener mOnCloseButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			close();
		}
	};

	OnClickListener mOnNextButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			close();
			if (mLevelSuccessDialogListener != null) {
				mLevelSuccessDialogListener.onNextLevel();
			}
		}
	};
	
	public interface LevelSuccessDialogListener {
		public void onNextLevel();
	}
}
