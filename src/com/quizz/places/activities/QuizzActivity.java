package com.quizz.places.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.utils.NavigationUtils;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.fragments.MenuFragment;

public class QuizzActivity extends BaseQuizzActivity {

    private static final String JSON_FILE = "places.json";

    private ObjectAnimator mBgAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	if (savedInstanceState == null) {
	    // First launch of the activity, not a rotation change
	    getQuizzActionBar().hide(QuizzActionBar.MOVE_DIRECT);
	    FragmentTransaction transaction = getSupportFragmentManager()
		    .beginTransaction();
	    NavigationUtils.directNavigationTo(MenuFragment.class,
		    getSupportFragmentManager(), this, false, transaction);
	}

	/*
	 * View confirmQuitDialog =
	 * getLayoutInflater().inflate(R.layout.dialog_confirm_quit, null);
	 * setConfirmQuitDialogView(confirmQuitDialog);
	 */

	getQuizzActionBar().getBackButton().setImageResource(
		R.drawable.back_but_5);
	getQuizzActionBar().setCustomView(R.layout.ab_view_sections);
	getQuizzActionBar().setBackgroundResource(R.drawable.bg_actionbar);

	View customView = getQuizzActionBar().getCustomViewContainer();
	TextView middleText = (TextView) customView
		.findViewById(R.id.ab_section_middle_text);
	TextView rightText = (TextView) customView
		.findViewById(R.id.ab_section_right_text);
	middleText.setText("22 / 30");
	rightText.setText("345 pts");

	initBackground();
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	mBgAnimation.end();
    }

    @Override
    protected String getJsonFilePath() {
	return JSON_FILE;
    }

    private void initBackground() {
	getQuizzLayout().setBackgroundResource(R.drawable.sky_clean);
	getBackgroundAnimatedImage().setBackgroundResource(R.drawable.clouds);

	mBgAnimation = ObjectAnimator.ofFloat(getBackgroundAnimatedImage(),
		"translationX", 500, -1700);
	mBgAnimation.setDuration(30000);
	mBgAnimation.setInterpolator(new LinearInterpolator());
	mBgAnimation.setRepeatCount(ObjectAnimator.INFINITE);
	mBgAnimation.start();
    }
}
