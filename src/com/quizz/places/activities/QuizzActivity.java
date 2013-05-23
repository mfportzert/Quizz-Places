package com.quizz.places.activities;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.google.analytics.tracking.android.EasyTracker;
import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.managers.DataManager;
import com.quizz.core.models.Section;
import com.quizz.core.utils.NavigationUtils;
import com.quizz.core.utils.PreferencesUtils;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.db.GameDataLoading;
import com.quizz.places.db.GameDataLoading.GameDataLoadingListener;
import com.quizz.places.fragments.MenuFragment;

public class QuizzActivity extends BaseQuizzActivity implements GameDataLoadingListener {

	public static final String DEBUG_TAG = QuizzActivity.class.getSimpleName();
	
	private ObjectAnimator mBgAnimation;
	private GameDataLoadingListener mGameDataLoadingListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* --- Campaigns tracking --- */
	    Intent intent = this.getIntent();
	    Uri uri = intent.getData();

	    EasyTracker.getInstance().setContext(this);

	    if (uri != null) {
	      EasyTracker.getTracker().setCampaign(uri.getPath());
          /*if(uri.getQueryParmeter("utm_source") != null) {    // Use campaign parameters if avaialble.
            EasyTracker.getTracker().setCampaign(uri.getPath()); 
          } else if (uri.getQueryParameter("referrer") != null) {    // Otherwise, try to find a referrer parameter.
            EasyTracker.getTracker().setReferrer(uri.getQueryParameter("referrer"));
          }*/
	    }
		/* ------ */
		
		if (savedInstanceState == null) {
			// First launch of the activity, not a rotation change
			getQuizzActionBar().hide(QuizzActionBar.MOVE_DIRECT);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			NavigationUtils.directNavigationTo(MenuFragment.class,
					getSupportFragmentManager(), this, false, transaction);
		}

		if (!DataManager.dataLoaded) {
			initAsyncGameLoading();
		}
		
		getQuizzActionBar().getBackButton().setImageResource(
				R.drawable.back_but_5);
		getQuizzActionBar().setCustomView(R.layout.ab_view_sections);
		getQuizzActionBar().setBackgroundResource(R.drawable.bg_actionbar);
		
		initBackground();
	}

	private void initAsyncGameLoading() {
		GameDataLoading gdl = new GameDataLoading(this);
		if (gdl.isFirstLaunch()) {
			 PreferencesUtils.setHintsAvailable(this, 
					 PreferencesUtils.PREF_DEFAULT_UNLOCKED_HINTS_COUNT_VALUE);
		}

		gdl.executeAsyncGameLoading(gdl.isFirstLaunch());
	}

	public void setGameDataLoadingListener(GameDataLoadingListener listener) {
		mGameDataLoadingListener = listener;
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
		if (mGameDataLoadingListener != null) {
			mGameDataLoadingListener.onGameLoadingStart();
		}
	}
	
	@Override
	public void onGameLoadingProgress(int progress) {
		if (mGameDataLoadingListener != null) {
			mGameDataLoadingListener.onGameLoadingProgress(progress);
		}
	}

	@Override
	public void onGameLoadingSuccess(List<Section> sections) {
		Log.e("ASYNC", "onGameLoadingSuccess: "+System.currentTimeMillis());
		if (mGameDataLoadingListener != null) {
			Log.e("ASYNC", "onGameLoadingSuccess mGameDataLoadingListener: "+System.currentTimeMillis());
			mGameDataLoadingListener.onGameLoadingSuccess(sections);
		}
	}

	@Override
	public void onGameLoadingFailure(Exception e) {
		if (mGameDataLoadingListener != null) {
			mGameDataLoadingListener.onGameLoadingFailure(e);
		}		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	    EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	    EasyTracker.getInstance().activityStop(this);
	}
}
