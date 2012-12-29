package com.quizz.places.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseMenuActivity;
import com.quizz.core.listeners.VisibilityAnimatorListener;
import com.quizz.core.utils.AnimatorUtils;
import com.quizz.places.R;
import com.quizz.places.widgets.MenuBackground;

public class MenuActivity extends BaseMenuActivity {

	private Button mButtonPlay;
	private Button mButtonFreeHints;
	private Button mButtonStats;
	private Button mButtonSettings;
	private Button mButtonQuit;
	private LinearLayout mMenuButtonsContainer;
	
	private ImageView mTitleSign;
	private ImageView mClouds;
	private ImageView mFooter;
	private MenuBackground mHaloBackground;
	
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
		mFooter = (ImageView) findViewById(R.id.footer);
		mMenuButtonsContainer = (LinearLayout) findViewById(R.id.menuButtonsContainer);
		//mHaloBackground = (MenuBackground) findViewById(R.id.haloBackground);
		
		mButtonPlay.setOnClickListener(mPlayOnClickListener);
		
		// FIXME: May not be displayed correctly on bigger screen when looping
		// (bad transition)
		// TODO: Make an image with beginning left similar to right end
		// TODO: Scroll the horizontalScrollView instead of translating the
		// imageView
		HorizontalScrollView cloudsScrollView = (HorizontalScrollView) findViewById(R.id.cloudsContainer);
		cloudsScrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		showUi();
	}
	
	private void showUi() {
		
		float[] signMovementValues = new float[] { -200, 0 };
		ObjectAnimator signPopup = ObjectAnimator.ofFloat(mTitleSign, "translationY", signMovementValues);
		signPopup.setDuration(300);
		signPopup.setStartDelay(1200);
		signPopup.setInterpolator(new AccelerateInterpolator());
		signPopup.addListener(new VisibilityAnimatorListener(mTitleSign));
		
		ObjectAnimator cloudMoving = ObjectAnimator.ofFloat(mClouds,
				"translationX", 500, -1700);
		cloudMoving.setDuration(30000);
		cloudMoving.setInterpolator(new LinearInterpolator());
		cloudMoving.setRepeatCount(ObjectAnimator.INFINITE);
		cloudMoving.start();

		float[] footerMovementValues = new float[] { 500, 0 };
		ObjectAnimator footerPopup = ObjectAnimator.ofFloat(mFooter, "translationY", footerMovementValues);
		footerPopup.setDuration(700);
		footerPopup.setStartDelay(500);
		footerPopup.setInterpolator(new AccelerateInterpolator());
		footerPopup.addListener(new VisibilityAnimatorListener(mFooter));
		
		AnimatorUtils.bounceAnimator(signPopup, signMovementValues, 5, 100);
		AnimatorUtils.bounceAnimator(footerPopup, footerMovementValues, 5, 100);
		
		ObjectAnimator buttonsDisplay = ObjectAnimator.ofFloat(mMenuButtonsContainer, "alpha", 0f, 1f);
		buttonsDisplay.setDuration(500);
		buttonsDisplay.setStartDelay(1700);
		buttonsDisplay.addListener(new VisibilityAnimatorListener(mMenuButtonsContainer));
		buttonsDisplay.start();
		
		/*
		ValueAnimator animator = ValueAnimator.ofFloat(0, 360);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {

				mHaloBackground.rotationDegrees = (Float) arg0.getAnimatedValue();
				mHaloBackground.invalidate();
			}
		});
		animator.setInterpolator(new LinearInterpolator());
		animator.setRepeatCount(ValueAnimator.INFINITE);
		animator.setDuration(8000);
		animator.start();*/
	}
	
	private void hideUi() {
		
		ObjectAnimator signHiding = ObjectAnimator.ofFloat(mTitleSign, "translationY", 0, -200);
		signHiding.setDuration(300);
		signHiding.start();
		
		ObjectAnimator footerHiding = ObjectAnimator.ofFloat(mFooter, "translationY", 0, 500);
		footerHiding.setDuration(700);
		footerHiding.start();
		
		ObjectAnimator buttonsHiding = ObjectAnimator.ofFloat(mMenuButtonsContainer, "alpha", 1f, 0f);
		buttonsHiding.setDuration(500);
		buttonsHiding.start();
		
		/*
		ValueAnimator animator = ValueAnimator.ofFloat(0, 360);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {

				mHaloBackground.rotationDegrees = (Float) arg0.getAnimatedValue();
				mHaloBackground.invalidate();
			}
		});
		animator.setInterpolator(new LinearInterpolator());
		animator.setRepeatCount(ValueAnimator.INFINITE);
		animator.setDuration(8000);
		animator.start();*/
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

	// ===========================================================
	// Listeners
	// ===========================================================
	
	OnClickListener mPlayOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			hideUi();
		}
	};
}
