package com.quizz.places.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.actionbarsherlock.internal.nineoldandroids.animation.AnimatorSet;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.dialogs.ConfirmQuitDialog;
import com.quizz.core.dialogs.ConfirmQuitDialog.Closeable;
import com.quizz.core.fragments.BaseMenuFragment;
import com.quizz.core.listeners.VisibilityAnimatorListener;
import com.quizz.core.models.Section;
import com.quizz.core.utils.AnimatorUtils;
import com.quizz.core.utils.NavigationUtils;
import com.quizz.places.R;
import com.quizz.places.db.GameDataLoading;
import com.quizz.places.db.GameDataLoading.GameDataLoadingListener;

public class MenuFragment extends BaseMenuFragment implements Closeable, GameDataLoadingListener {

	public static final String DEBUG_TAG = MenuFragment.class.getSimpleName();

	private Button mButtonPlay;
	private Button mButtonRateThisApp;
	private Button mButtonStats;
	private Button mButtonSettings;
	private LinearLayout mMenuButtonsContainer;

	private ImageView mTitleSign;
	private ImageView mFooter;
	//private ImageButton mButtonHomeExit;

	private AnimatorSet mHideUiAnimatorSet;
	private View mConfirmQuitDialogView;
	private ConfirmQuitDialog mConfirmQuitDialog;
	
	
	private View mView;
	private RelativeLayout mLoadingContainerLayout;
	private ProgressBar mProgressBar;

	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View mView = inflater.inflate(R.layout.fragment_menu, container, false);

		mButtonPlay = (Button) mView.findViewById(R.id.buttonPlay);
		mButtonRateThisApp = (Button) mView.findViewById(R.id.buttonRateThisApp);
		mButtonStats = (Button) mView.findViewById(R.id.buttonStats);
		mButtonSettings = (Button) mView.findViewById(R.id.buttonSettings);
		mTitleSign = (ImageView) mView.findViewById(R.id.titleSign);
		mFooter = (ImageView) mView.findViewById(R.id.footer);
		mMenuButtonsContainer = (LinearLayout) mView
				.findViewById(R.id.menuButtonsContainer);
		//mButtonHomeExit = (ImageButton) view.findViewById(R.id.buttonHomeExit);

		initAsyncGameLoading();
		
		/*
		mButtonHomeExit.setAlpha(220);
		mButtonHomeExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});*/

		return mView;
	}

	@Override
	public void onDestroy() {
		if (mConfirmQuitDialog != null) {
			mConfirmQuitDialog.dismiss();
		}
		super.onDestroy();
	}
	
	@Override
	public void close() {
		getActivity().finish();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		showUi();
	}

	private void showUi() {
		float[] signMovementValues = new float[] { -200, 0 };
		ObjectAnimator signPopup = ObjectAnimator.ofFloat(mTitleSign,
				"translationY", signMovementValues);
		signPopup.setDuration(300);
		signPopup.setStartDelay(700);
		signPopup.setInterpolator(new AccelerateInterpolator());
		signPopup.addListener(new VisibilityAnimatorListener(mTitleSign));

		float[] footerMovementValues = new float[] { 500, 0 };
		ObjectAnimator footerPopup = ObjectAnimator.ofFloat(mFooter,
				"translationY", footerMovementValues);
		footerPopup.setDuration(700);
		footerPopup.setInterpolator(new AccelerateInterpolator());
		footerPopup.addListener(new VisibilityAnimatorListener(mFooter));

		AnimatorUtils.bounceAnimator(signPopup, signMovementValues, 5, 100);
		AnimatorUtils.bounceAnimator(footerPopup, footerMovementValues, 5, 100);

		ObjectAnimator buttonsDisplay = ObjectAnimator.ofFloat(
				mMenuButtonsContainer, "alpha", 0f, 1f);
		buttonsDisplay.setDuration(500);
		buttonsDisplay.setStartDelay(700);
		buttonsDisplay.addListener(new VisibilityAnimatorListener(
				mMenuButtonsContainer));
		buttonsDisplay.start();
		
		Log.d(DEBUG_TAG, "showUi");
/*
		ObjectAnimator homeExitDisplay = ObjectAnimator.ofFloat(
				mButtonHomeExit, "alpha", 0f, 1f);
		homeExitDisplay.setDuration(500);
		homeExitDisplay.setStartDelay(700);
		homeExitDisplay.addListener(new VisibilityAnimatorListener(
				mButtonHomeExit));
		homeExitDisplay.start();*/
	}

	private AnimatorSet createHideUiAnimation() {
		ObjectAnimator signHiding = ObjectAnimator.ofFloat(mTitleSign,
				"translationY", 0, -200);
		signHiding.setDuration(300);

		ObjectAnimator footerHiding = ObjectAnimator.ofFloat(mFooter,
				"translationY", 0, 500);
		footerHiding.setDuration(700);

		ObjectAnimator buttonsHiding = ObjectAnimator.ofFloat(
				mMenuButtonsContainer, "alpha", 1f, 0f);
		buttonsHiding.setDuration(500);
/*
		ObjectAnimator homeExitHiding = ObjectAnimator.ofFloat(mButtonHomeExit,
				"alpha", 1f, 0f);
		homeExitHiding.setDuration(500);
*/
		AnimatorSet uiHidingAnimation = new AnimatorSet();
		uiHidingAnimation.playTogether(signHiding, footerHiding, buttonsHiding/*,
				homeExitHiding*/);
		return uiHidingAnimation;
	}
	
	private void initAsyncGameLoading() {
		GameDataLoading gdl = new GameDataLoading(this.getActivity());
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
	public void onGameLoadingStart() {
//		RelativeLayout.LayoutParams tvLayoutParams = new RelativeLayout.LayoutParams(
//		RelativeLayout.LayoutParams.WRAP_CONTENT,
//		RelativeLayout.LayoutParams.WRAP_CONTENT);
//		tvLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		TextView tv = new TextView(this);
//		tv.setText("test");
//		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
//		tv.setLayoutParams(tvLayoutParams);
		
		mProgressBar = new ProgressBar(this.getActivity(), null, android.R.attr.progressBarStyleHorizontal);
		mProgressBar.setMax(100);
		mProgressBar.setProgress(0);
		mProgressBar.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT));
		
		mLoadingContainerLayout = new RelativeLayout(this.getActivity());
		RelativeLayout.LayoutParams containerLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		containerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mLoadingContainerLayout.setLayoutParams(containerLayoutParams);
		mLoadingContainerLayout.setPadding(20, 0, 20, 0);	    
//		containerLayout.addView(tv);
		mLoadingContainerLayout.addView(mProgressBar);
		
		((RelativeLayout) mView.findViewById(R.id.fragmentsContainer)).addView(mLoadingContainerLayout);
	}

	@Override
	public void onGameLoadingProgress(int progress) {
		Log.d(DEBUG_TAG, "progress : " + String.valueOf(progress) + "%");
		mProgressBar.setProgress(progress);
		mProgressBar.invalidate();
	}

	@Override
	public void onGameLoadingSuccess(List<Section> sections) {
		mProgressBar.setVisibility(View.GONE);
		mLoadingContainerLayout.setVisibility(View.GONE);
		
		mHideUiAnimatorSet = createHideUiAnimation();
		FragmentTransaction fadeTransaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		fadeTransaction.setCustomAnimations(R.anim.none, R.anim.none,
				R.anim.none, R.anim.fade_out);

		initMenuButton(mButtonPlay, ListSectionsFragment.class,
				fadeTransaction, mHideUiAnimatorSet);
		initMenuButton(mButtonStats, StatsFragment.class, fadeTransaction,
				mHideUiAnimatorSet);
		initMenuButton(mButtonSettings, SettingsFragment.class, fadeTransaction,
				mHideUiAnimatorSet);

//		FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
//		NavigationUtils.directNavigationTo(MenuFragment.class,
//				this.getActivity().getSupportFragmentManager(), this, false, transaction);
	}

	@Override
	public void onGameLoadingFailure(Exception e) {
		// TODO Auto-generated method stub
	}

}
