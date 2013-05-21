package com.quizz.places.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.quizz.core.models.Level;
import com.quizz.core.utils.PreferencesUtils;
import com.quizz.core.utils.StringUtils;
import com.quizz.places.R;

public class HintsDialog extends Activity {

	public static final String EXTRA_LEVEL = "HintsDialog.EXTRA_LEVEL";
	
	private Level mLevel;
	private Button mUnlockButton;
	private TextView mMessageTextView;
	private TextView mNbHintsTextView;
	private Toast mInfoToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		// To remove the background of the 'Dialog'
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		setContentView(R.layout.dialog_info);
		
		mLevel = getIntent().getExtras().getParcelable(EXTRA_LEVEL);
		
		mUnlockButton = (Button) findViewById(R.id.infoUnlock);
		mMessageTextView = (TextView) findViewById(R.id.infoContent);
		mNbHintsTextView = (TextView) findViewById(R.id.infoNbHints);

		TextView titleTextView = (TextView) findViewById(R.id.infoTitle);
		titleTextView.setText(R.string.level_info_title);
		
		mInfoToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mInfoToast.setText(R.string.level_not_enough_hints);
		
		// Get previously unlocked letters
		boolean isInformationUnlocked = PreferencesUtils.isInformationUnlocked(this, mLevel);
		if (isInformationUnlocked) {
			showInformation();
		} else {
			final int hints = PreferencesUtils.getHintsAvailable(HintsDialog.this);
			mNbHintsTextView.setText(getResources().getQuantityString(
					R.plurals.level_nb_hints_label, hints, hints));
			mMessageTextView.setVisibility(View.GONE);
			mUnlockButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (hints > 1) {
						PreferencesUtils.setInformationUnlocked(HintsDialog.this, mLevel, true);
						PreferencesUtils.setHintsAvailable(HintsDialog.this, hints - 2);
						showInformation();						
					} else {
						mInfoToast.show();
					}
				}
			});
		}
	}
	
	private void showInformation() {
		mUnlockButton.setVisibility(View.GONE);
		mNbHintsTextView.setVisibility(View.GONE);
		mMessageTextView.setVisibility(View.VISIBLE);
		if (StringUtils.isEmpty(mLevel.indication)) {
			mMessageTextView.setText(R.string.level_no_info_found);
		} else {
			mMessageTextView.setText(Html.fromHtml(mLevel.indication));
		}
	}
}
