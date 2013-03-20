package com.quizz.places.fragments;

import java.util.Locale;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.application.BaseQuizzApplication;
import com.quizz.core.fragments.BaseLevelFragment;
import com.quizz.core.imageloader.ImageLoader;
import com.quizz.core.imageloader.ImageLoader.ImageType;
import com.quizz.core.interfaces.FragmentContainer;
import com.quizz.core.managers.DataManager;
import com.quizz.core.models.Level;
import com.quizz.core.utils.NavigationUtils;
import com.quizz.core.utils.StringUtils;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.application.QuizzPlacesApplication;
import com.quizz.places.dialogs.HintsDialog;
import com.quizz.places.dialogs.LevelSuccessDialog;

public class LevelFragment extends BaseLevelFragment {
	public static final int LEVEL_SUCCESS_REQUEST_CODE = 1;
	private static final int GREEN_LETTER = 0xff34C924;
	
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

		ObjectAnimator.ofFloat(pictureBig, "rotation", 0.0f, mLevel.rotation / 4)
				.setDuration(0).start();

		// get number of hints the user can reveal
		SharedPreferences sharedPreferences = getActivity().getPreferences(Application.MODE_PRIVATE);
		int hintsAvailableToUnlock = sharedPreferences.getInt(BaseQuizzApplication.PREF_UNLOCKED_HINTS_COUNT_KEY, 0);
		
		/* Manage action bar + difficulty */
		setActionbarView(hintsAvailableToUnlock);

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
				"fonts/OpenSans-CondBold.ttf");
		mLevelTitle.setTypeface(face);

		return view;
	}

	private void setActionbarView(int hintsAvailableToUnlock) {
		QuizzActionBar actionBar = ((BaseQuizzActivity) getActivity()).getQuizzActionBar();
		actionBar.setCustomView(R.layout.ab_view_level);

		View customView = actionBar.getCustomViewContainer();
		ImageView mediumStar = (ImageView) customView.findViewById(R.id.levelStarMedium);
		ImageView hardStar = (ImageView) customView.findViewById(R.id.levelStarHard);

		mediumStar.setEnabled(true);
		hardStar.setEnabled(true);
		if (mLevel.difficulty.equals(Level.LEVEL_MEDIUM)) {
			hardStar.setEnabled(false);
		} else if (!mLevel.difficulty.equals(Level.LEVEL_HARD)) {
			mediumStar.setEnabled(false);
			hardStar.setEnabled(false);
		}
		
		((TextView) customView.findViewById(R.id.ab_level_hints_nb)).setText(
				String.valueOf(hintsAvailableToUnlock));
	}
	
	private boolean isCharacterValid(char userLetter, char responseLetter) {
		if (userLetter == responseLetter) {
			return true;
		}
		return false;
	}

	// TODO: Move to BaseLevelFragment
	private void checkResponse(String inputContent) {
		// Normalize and uppercase: Colisée => COLISEE
		inputContent = StringUtils.removeDiacritic(inputContent).toUpperCase(Locale.getDefault());
		String partialResponse = StringUtils.removeDiacritic(mPartialResponse).toUpperCase(Locale.getDefault());

		// Using StringBuilder for convenience
		StringBuilder userResponse = new StringBuilder(partialResponse);
		
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

		int errorsCount = 0;
		// We now apply the colors
		SpannableString coloredUserResponse = new SpannableString(userResponse);
		for (int i = 0; i < coloredUserResponse.length(); i++) {
			// We color only the '_' characters of the base partial response
			if (mPartialResponse.charAt(i) == '_') {
				// Remove accents
				char userLetter = coloredUserResponse.charAt(i);
				char responseLetter = Character.toUpperCase(
						StringUtils.removeDiacritic(mLevel.response.charAt(i)));
				
				// Check validity of the character
				boolean isValid = isCharacterValid(userLetter, responseLetter);
				int color = (isValid) ? GREEN_LETTER : Color.RED;
				coloredUserResponse.setSpan(new ForegroundColorSpan(color), i, i + 1, 0);
				
				// Increment errorsCount if not valid in order to know if we show success dialog
				errorsCount += (!isValid) ? 1 : 0;
			}
		}
		
		mLevelTitle.setText(coloredUserResponse);

		// if we didn't find any error, display success dialog
		if (errorsCount == 0) {
			onSuccess();
		} else {
			onError(errorsCount);
		}
	}

	public void onSuccess() {
		startActivity(new Intent(LevelFragment.this.getActivity(), LevelSuccessDialog.class));

		SharedPreferences sharedPreferences = getActivity().getPreferences(Application.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(BaseQuizzApplication.PREF_UNLOCKED_HINTS_COUNT_KEY, 
				sharedPreferences.getInt(BaseQuizzApplication.PREF_UNLOCKED_HINTS_COUNT_KEY, 0) +
				sharedPreferences.getInt(BaseQuizzApplication.PREF_DEFAULT_NB_HINTS_ONSUCCESS_KEY, 2)
				);
		editor.commit();
		
		mLevel.status = Level.STATUS_LEVEL_CLEAR;
		mLevel.update();
	}
	
	public void onError(int errorsCount) {
		
	}
	
	public void onNextLevel() {
		Level nextLevel = DataManager.getNextLevel(mLevel);
		if (nextLevel != null) {
			FragmentContainer container = (FragmentContainer) getActivity();
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
	
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in,
					R.anim.none, R.anim.slide_in_left,
					R.anim.slide_out_right);
	
			Bundle args = new Bundle();
			args.putParcelable(BaseLevelFragment.ARG_LEVEL, nextLevel);
			NavigationUtils.directNavigationTo(LevelFragment.class, fragmentManager, 
					container, false, transaction, args);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == LEVEL_SUCCESS_REQUEST_CODE) {
			if (resultCode == LevelSuccessDialog.RESULT_CODE_NEXT) {
				onNextLevel();
			}
		}
	}
	
	// ===========================================================
	// Listeners
	// ===========================================================

	OnClickListener mCheckButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			checkResponse(mInputText.getText().toString());
		}
	};

	OnClickListener mHintsButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			LevelFragment.this.startActivityForResult(new Intent(LevelFragment.this.getActivity(),
					HintsDialog.class), LEVEL_SUCCESS_REQUEST_CODE);
		}
	};
}
