package com.quizz.places.dialogs;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.quizz.core.dialogs.BaseHintsDialog;
import com.quizz.places.R;

public class HintsDialog extends BaseHintsDialog {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_hints);
		mHintsContainer = (ViewGroup) findViewById(R.id.hints_content);
		
		addTab(R.id.tab_idea, R.layout.include_hint_idea);
		addTab(R.id.tab_letters, R.layout.include_hint_letters);
	}

	@Override
	protected void onTabSelected(int tabId, View contentView) {
		
	}
}
