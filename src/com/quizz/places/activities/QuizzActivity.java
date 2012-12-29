package com.quizz.places.activities;

import android.os.Bundle;

import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.places.fragments.MenuFragment;

public class QuizzActivity extends BaseQuizzActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		navigateTo(MenuFragment.class);
	}
}
