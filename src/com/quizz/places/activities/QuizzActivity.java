package com.quizz.places.activities;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.models.Section;
import com.quizz.core.utils.NavigationUtils;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.db.GameDataLoading;
import com.quizz.places.db.GameDataLoading.GameDataLoadingListener;
import com.quizz.places.fragments.MenuFragment;

public class QuizzActivity extends BaseQuizzActivity implements GameDataLoadingListener {

	public static final String DEBUG_TAG = QuizzActivity.class.getSimpleName();
	
	private ObjectAnimator mBgAnimation;
	private RelativeLayout mLoadingContainerLayout;
	private ProgressBar mProgressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			// First launch of the activity, not a rotation change
			getQuizzActionBar().hide(QuizzActionBar.MOVE_DIRECT);
//			FragmentTransaction transaction = getSupportFragmentManager()
//					.beginTransaction();
//			NavigationUtils.directNavigationTo(GameStartLoadingFragment.class,
//					getSupportFragmentManager(), this, false, transaction);
		}

		/*
		 * View confirmQuitDialog =
		 * getLayoutInflater().inflate(R.layout.dialog_confirm_quit, null);
		 * setConfirmQuitDialogView(confirmQuitDialog);
		 */

		getQuizzActionBar().getBackButton().setImageResource(R.drawable.back_but_5);
		getQuizzActionBar().setCustomView(R.layout.ab_view_sections);
		getQuizzActionBar().setBackgroundResource(R.drawable.bg_actionbar);

		initAsyncGameLoading();
		initBackground();
	}

	private void initAsyncGameLoading() {
		GameDataLoading gdl = new GameDataLoading(this);
		if (gdl.isFirstLaunch()) {
			gdl.executeAsyncGameLoading(gdl.isFirstLaunch());
//			gdl.initPreferences();
		} else if (gdl.isDbUpgradeNeeded()) {
			Log.d(DEBUG_TAG, "upgrade is needed");
			// Handle upgrade stuff here
			gdl.upgradeVersionInPreferences();
		} else {
			Log.d(DEBUG_TAG, "upgrade is NOT needed");
			
		}
	}

	@Override
	protected void onDestroy() {
		if (mBgAnimation != null && mBgAnimation.isRunning()) {
			mBgAnimation.end();
		}
		super.onDestroy();
	}

	private void initBackground() {
		getBackgroundAnimatedImage().setBackgroundResource(R.drawable.clouds);

		mBgAnimation = ObjectAnimator.ofFloat(getBackgroundAnimatedImage(),
				"translationX", 500, -1700);
		mBgAnimation.setDuration(30000);
		mBgAnimation.setInterpolator(new LinearInterpolator());
		mBgAnimation.setRepeatCount(ObjectAnimator.INFINITE);
		mBgAnimation.start();
	}

	@Override
	public void onGameLoadingStart() {
//		RelativeLayout.LayoutParams tvLayoutParams = new RelativeLayout.LayoutParams(
//		RelativeLayout.LayoutParams.WRAP_CONTENT,
//		RelativeLayout.LayoutParams.WRAP_CONTENT);
//		tvLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		TextView tv = new TextView(this);
//		tv.setText("test");
//		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
//		tv.setLayoutParams(tvLayoutParams);
		
		mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
		mProgressBar.setMax(100);
		mProgressBar.setProgress(0);
		mProgressBar.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT));
		
		mLoadingContainerLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams containerLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		containerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mLoadingContainerLayout.setLayoutParams(containerLayoutParams);
		mLoadingContainerLayout.setPadding(20, 0, 20, 0);	    
//		containerLayout.addView(tv);
		mLoadingContainerLayout.addView(mProgressBar);
		
		((RelativeLayout) findViewById(R.id.fragmentsContainer)).addView(mLoadingContainerLayout);
	}

	@Override
	public void onGameLoadingProgress(int progress) {
		Log.d(DEBUG_TAG, "progress : " + String.valueOf(progress) + "%");
		mProgressBar.setProgress(progress);
		mProgressBar.invalidate();
	}

	@Override
	public void onGameLoadingSuccess(List<Section> sections) {
//		mProgressBar.setVisibility(View.GONE);
//		mLoadingContainerLayout.setVisibility(View.GONE);
//		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		NavigationUtils.directNavigationTo(MenuFragment.class,
				getSupportFragmentManager(), this, false, transaction);
	}

	@Override
	public void onGameLoadingFailure(Exception e) {
		// TODO Auto-generated method stub
	}

}
