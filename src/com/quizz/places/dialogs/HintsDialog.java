package com.quizz.places.dialogs;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quizz.core.dialogs.BaseHintsDialog;
import com.quizz.core.models.Hint;
import com.quizz.core.models.Level;
import com.quizz.places.R;

public class HintsDialog extends BaseHintsDialog {

	public static final String EXTRA_LEVEL = "HintsDialog.EXTRA_LEVEL";
	
	private Level mLevel;
	private Hint mCulturalHint;
	private Hint mMapHint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_hints);
		mHintsContainer = (ViewGroup) findViewById(R.id.hints_content);
		
		mLevel = getIntent().getExtras().getParcelable(EXTRA_LEVEL);
		mCulturalHint = mLevel.getHints().get(0);
		mMapHint = mLevel.getHints().get(1);
		
		addTab(R.id.tab_hat, R.layout.include_hint_text);
		addTab(R.id.tab_map, R.layout.include_hint_map);
		selectTab(R.id.tab_hat);
	}
	
	@Override
	protected void onInitTab(int tabId, View contentView) {
		
		switch (tabId) {
		
		case R.id.tab_hat:
			String title = "Cultural tip";
			TextView titleTextView = (TextView) contentView.findViewById(R.id.hintTitle);
			TextView messageTextView = (TextView) contentView.findViewById(R.id.hintMessage);
			titleTextView.setText(title);
			messageTextView.setText(mCulturalHint.hint);
			break;

		case R.id.tab_map:
			ImageView map = (ImageView) contentView.findViewById(R.id.mapLocation);
			break;
		}
	}

	@Override
	protected void onSelectTab(int tabId, View contentView) {
		
	}
}
