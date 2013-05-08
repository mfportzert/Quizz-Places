package com.quizz.places.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

import com.quizz.core.models.Level;
import com.quizz.core.utils.StringUtils;
import com.quizz.places.R;

public class HintsDialog extends Activity {

	public static final String EXTRA_LEVEL = "HintsDialog.EXTRA_LEVEL";
	
	private Level mLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		// To remove the background of the 'Dialog'
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		setContentView(R.layout.dialog_info);
		
		mLevel = getIntent().getExtras().getParcelable(EXTRA_LEVEL);
		
		String title = getResources().getString(R.string.level_info_title);
		TextView titleTextView = (TextView) findViewById(R.id.infoTitle);
		TextView messageTextView = (TextView) findViewById(R.id.infoContent);
		titleTextView.setText(title);
		if (StringUtils.isEmpty(mLevel.indication)) {
			messageTextView.setText(R.string.level_no_info_found);
		} else {
			messageTextView.setText(Html.fromHtml(mLevel.indication));
		}
	}
}
