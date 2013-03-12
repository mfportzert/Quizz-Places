package com.quizz.places.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.fragments.BaseLevelFragment;
import com.quizz.core.imageloader.ImageLoader;
import com.quizz.core.imageloader.ImageLoader.ImageType;
import com.quizz.core.models.Level;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.application.QuizzPlacesApplication;
import com.quizz.places.dialogs.HintsDialog;

public class LevelFragment extends BaseLevelFragment {

	private TextView mLevelTitle;
	private Button mCheckButton;
	private EditText mInputText;

	private Level mLevel;
	private String mPartialResponse;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_level, container, false);
		ImageView pictureBig = (ImageView) view
				.findViewById(R.id.levelPictureBig);
		ImageButton hintsButton = (ImageButton) view
				.findViewById(R.id.levelHints);
		mLevelTitle = (TextView) view.findViewById(R.id.levelName);
		mCheckButton = (Button) view.findViewById(R.id.levelCheckButton);
		mInputText = (EditText) view.findViewById(R.id.levelInputResponse);

		/* Load + rotate picture */
		mLevel = getArguments().getParcelable(ARG_LEVEL);
		ImageLoader imageLoader = new ImageLoader(getActivity());
		imageLoader.displayImage(QuizzPlacesApplication.IMAGES_DIR
				+ mLevel.imageName, pictureBig, ImageType.LOCAL);

		float rotation = getArguments().getFloat(ARG_ROTATION);
		ObjectAnimator.ofFloat(pictureBig, "rotation", 0.0f, rotation / 4)
				.setDuration(0).start();

		/* Manage action bar + difficulty */
		QuizzActionBar actionBar = ((BaseQuizzActivity) getActivity())
				.getQuizzActionBar();
		actionBar.setCustomView(R.layout.ab_view_level);

		View customView = actionBar.getCustomViewContainer();
		ImageView mediumStar = (ImageView) customView
				.findViewById(R.id.levelStarMedium);
		ImageView hardStar = (ImageView) customView
				.findViewById(R.id.levelStarHard);

		mediumStar.setEnabled(true);
		hardStar.setEnabled(true);
		if (mLevel.difficulty.equals(Level.LEVEL_MEDIUM)) {
			hardStar.setEnabled(false);
		} else if (!mLevel.difficulty.equals(Level.LEVEL_HARD)) {
			mediumStar.setEnabled(false);
			hardStar.setEnabled(false);
		}

		// Init partial response
		mPartialResponse = "" + mLevel.response.charAt(0);
		for (int i = 1; i < mLevel.response.length(); i++) {
			mPartialResponse += (mLevel.response.charAt(i) == ' ') ? ' ' : '_';
		}

		/* Init layout */
		mLevelTitle.setText(mPartialResponse);
		mCheckButton.setOnClickListener(mCheckButtonClickListener);
		hintsButton.setOnClickListener(mHintsButtonClickListener);

		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(mLevel.response.length());
		mInputText.setFilters(FilterArray);
		// TODO: Init input response hint (x words, x letters)

		Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
				"OpenSans-CondBold.ttf");
		mLevelTitle.setTypeface(face);

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private boolean isCharacterValid(char userLetter, char responseLetter) {
		// TODO: Make validation more flexible
		if (userLetter == responseLetter) {
			return true;
		}
		return false;
	}

	private void checkResponse(String inputContent) {
		// For convenience
		StringBuilder userResponse = new StringBuilder(mPartialResponse);

		// We will replace all '_' from partial response to the corresponding
		// input response characters
		for (int i = 0; i < inputContent.length(); i++) {
			// Assure the response entered isn't (for any reason) longer than
			// the current partial response
			if (i > userResponse.length()) {
				break;
			}

			// We replace the first '_' found, leaving alone spaces and
			// already discovered characters
			if (userResponse.charAt(i) == '_') {
				userResponse.setCharAt(i, inputContent.charAt(i));
			}
		}

		// We now apply the colors
		SpannableString coloredUserResponse = new SpannableString(userResponse);
		for (int i = 0; i < coloredUserResponse.length(); i++) {
			// We color only the '_' characters of the base partial response
			if (mPartialResponse.charAt(i) == '_') {
				// Check validity of the character
				boolean isValid = isCharacterValid(
						coloredUserResponse.charAt(i),
						mLevel.response.charAt(i));
				int color = (isValid) ? Color.GREEN : Color.RED;
				coloredUserResponse.setSpan(new ForegroundColorSpan(color), i,
						i + 1, 0);
			}
		}

		mLevelTitle.setText(coloredUserResponse);
	}

	// ===========================================================
	// Listeners
	// ===========================================================

	OnClickListener mCheckButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			checkResponse(mInputText.getText().toString());
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mInputText.getWindowToken(), 0);
		}
	};

	OnClickListener mHintsButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			startActivity(new Intent(LevelFragment.this.getActivity(),
					HintsDialog.class));
		}
	};
}
