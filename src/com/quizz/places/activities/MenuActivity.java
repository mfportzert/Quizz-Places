package com.quizz.places.activities;
	
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.Animator.AnimatorListener;
import com.actionbarsherlock.internal.nineoldandroids.animation.AnimatorSet;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseMenuActivity;
import com.quizz.places.R;

public class MenuActivity extends BaseMenuActivity {

	private Button mButtonPlay;
	private Button mButtonFreeHints;
	private Button mButtonStats;
	private Button mButtonSettings;
	private Button mButtonQuit;
	private ImageView mTitleSign;
	private ImageView mClouds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		mButtonPlay = (Button) findViewById(R.id.buttonPlay);
		mButtonFreeHints = (Button) findViewById(R.id.buttonFreeHints);
		mButtonStats = (Button) findViewById(R.id.buttonStats);
		mButtonSettings = (Button) findViewById(R.id.buttonSettings);
		mButtonQuit = (Button) findViewById(R.id.buttonQuit);
		mTitleSign = (ImageView) findViewById(R.id.titleSign);
		mClouds = (ImageView) findViewById(R.id.clouds);

		startAnimations();
	}
	
	private void startAnimations() {
		
		ObjectAnimator signPopup = ObjectAnimator.ofFloat(mTitleSign, "translationY", -200, 0);
		signPopup.setDuration(300);
		signPopup.addListener(mSignAnimatorListener);
		
		ObjectAnimator signBounceUp = ObjectAnimator.ofFloat(mTitleSign, "translationY", 0, -5);
		signBounceUp.setDuration(100);
		
		ObjectAnimator signBounceDown = ObjectAnimator.ofFloat(mTitleSign, "translationY", -5, 0);
		signBounceDown.setDuration(100);
		
		AnimatorSet signAnimatorSet = new AnimatorSet();
		signAnimatorSet.playSequentially(signPopup, signBounceUp, signBounceDown);
		signAnimatorSet.setStartDelay(500);
		signAnimatorSet.start();
		
		ObjectAnimator cloudMoving = ObjectAnimator.ofFloat(mClouds, "translationX", 100, -1000);
		cloudMoving.setDuration(20000);
		cloudMoving.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}
	
	// ===========================================================
    // Listeners
    // ===========================================================
	
	AnimatorListener mSignAnimatorListener = new AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
			mTitleSign.setVisibility(View.VISIBLE);
		}

		@Override
		public void onAnimationEnd(Animator animation) {}

		@Override
		public void onAnimationCancel(Animator animation) {}

		@Override
		public void onAnimationRepeat(Animator animation) {}
	};
}
