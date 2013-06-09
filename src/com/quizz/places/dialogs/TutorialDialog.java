package com.quizz.places.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.quizz.places.R;

public class TutorialDialog extends Activity {
	
	private TutorialStep mCurrentTutorialStep = TutorialStep.NONE;
	private ViewGroup mTutorialBubbleLayout;
	private TextView mTutorialStepTitle;
	private TextView mTutorialStepExplanation;
	private Button mTutorialNextStepButton;
	
	private enum TutorialStep {
		NONE, PICTURE, DESCRIPTION, LETTERS_INPUT, HINTS, GOODBYE
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		// To remove the background of the 'Dialog'
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		setContentView(R.layout.dialog_tutorial);
		
		mTutorialStepTitle = (TextView) findViewById(R.id.levelTutorialTitle);
		mTutorialStepExplanation = (TextView) findViewById(R.id.levelTutorialExplanation);
		mTutorialNextStepButton = (Button) findViewById(R.id.levelTutorialNext);

		mTutorialNextStepButton.setOnClickListener(mTutorialNextButtonClickListener);
	}
	
	private void displayNextTutorialStep() {
		TutorialStep step = getNextTutorialStep();
		if (step != null) {
			mCurrentTutorialStep = step;
			
			mTutorialStepTitle.setText(getString(R.string.level_tutorial_title)+" "+
					mCurrentTutorialStep.ordinal()+"/"+(TutorialStep.values().length - 1));
			
			switch (mCurrentTutorialStep) {
			case PICTURE:
				mTutorialStepExplanation.setText(R.string.level_tutorial_picture);
				break;
			case DESCRIPTION:
				mTutorialStepExplanation.setText(R.string.level_tutorial_description);
				break;
			case LETTERS_INPUT:
				mTutorialStepExplanation.setText(R.string.level_tutorial_input);
				//your_scrollview.scrollTo(0, your_EditBox.getBottom());
				break;
			case HINTS:
				mTutorialStepExplanation.setText(R.string.level_tutorial_hints);
				break;
			case GOODBYE:
				mTutorialStepExplanation.setText(R.string.level_tutorial_goodbye);
				break;
			}
			
		} else {
			finish();
		}
	}
	
	private TutorialStep getNextTutorialStep() {
		if (mCurrentTutorialStep.ordinal() < TutorialStep.values().length - 1) {
			return TutorialStep.values()[mCurrentTutorialStep.ordinal() + 1];
		}
		return null;
	}
	
	OnClickListener mTutorialNextButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			displayNextTutorialStep();
		}
	};
}
