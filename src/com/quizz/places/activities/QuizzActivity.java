package com.quizz.places.activities;

import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.utils.NavigationUtils;
import com.quizz.places.R;
import com.quizz.places.fragments.MenuFragment;

public class QuizzActivity extends BaseQuizzActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		NavigationUtils.navigateTo(MenuFragment.class, getSupportFragmentManager(), this);
		
		View quizzLayout = getQuizzLayout();
		quizzLayout.setBackgroundResource(R.drawable.sky_clean);
		
		ImageView backgroundImage = getBackgroundAnimatedImage();
		backgroundImage.setBackgroundResource(R.drawable.clouds);
		
		ObjectAnimator bgAnimation = ObjectAnimator.ofFloat(backgroundImage, "translationX", 500, -1700);
		bgAnimation.setDuration(30000);
		bgAnimation.setInterpolator(new LinearInterpolator());
		bgAnimation.setRepeatCount(ObjectAnimator.INFINITE);
		bgAnimation.start();
	}
}
