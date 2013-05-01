package com.quizz.places.fragments;

import java.io.IOException;
import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.Animator.AnimatorListener;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.application.BaseQuizzApplication;
import com.quizz.core.fragments.BaseGridLevelsFragment;
import com.quizz.core.fragments.BaseLevelFragment;
import com.quizz.core.imageloader.ImageLoader;
import com.quizz.core.imageloader.ImageLoader.ImageType;
import com.quizz.core.interfaces.FragmentContainer;
import com.quizz.core.managers.DataManager;
import com.quizz.core.models.Level;
import com.quizz.core.utils.NavigationUtils;
import com.quizz.core.utils.PreferencesUtils;
import com.quizz.core.utils.StringUtils;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.activities.PictureFullscreenActivity;
import com.quizz.places.application.QuizzPlacesApplication;
import com.quizz.places.dialogs.HintsDialog;
import com.quizz.places.dialogs.LevelSuccessDialog;

public class LevelFragment extends BaseLevelFragment {
	public static final int LEVEL_SUCCESS_REQUEST_CODE = 1;
	private static final int GREEN_LETTER = 0xff34C924;
	
	private static final String STATE_CURRENT_LEVEL = "LevelFragment.STATE_CURRENT_LEVEL";
	
	private ImageView mPictureBig;
	private ImageButton mInfoButton;
	private ImageButton mHintLettersButton;
	private TextView mLevelTitle;
	private Button mCheckButton;
	private EditText mInputText;

	private TextView mActionBarHints;
	private TextView mLevelCompletedLabel;
	
	private Level mCurrentLevel;
	private String mPartialResponse;
	private ImageLoader mImageLoader;
	
	private ImageButton mPreviousButton;
	private ImageButton mNextButton;
	private ImageButton mPictureGridButton;
	
	private TableLayout mLettersTableLayout;
	private boolean mLevelGivenFromPicturesGrid = false;
	
	private MediaPlayer mSuccessPlayer;
	private Toast mInfoToast;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_level, container, false);
		mPictureBig = (ImageView) view.findViewById(R.id.levelPictureBig);
		mInfoButton = (ImageButton) view.findViewById(R.id.levelInfo);
		mHintLettersButton = (ImageButton) view.findViewById(R.id.levelHintLetters);
		mLevelTitle = (TextView) view.findViewById(R.id.levelName);
		mCheckButton = (Button) view.findViewById(R.id.levelCheckButton);
		mInputText = (EditText) view.findViewById(R.id.levelInputResponse);
		mLevelCompletedLabel = (TextView) view.findViewById(R.id.levelPictureFoundLabel);
		mLettersTableLayout = (TableLayout) view.findViewById(R.id.tableLetters);
		
		mPictureBig.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), PictureFullscreenActivity.class));
			}
		});
		
		// Init actionBar
		QuizzActionBar actionBar = ((BaseQuizzActivity) getActivity()).getQuizzActionBar();
		actionBar.setCustomView(R.layout.ab_view_level);

		View customView = actionBar.getCustomViewContainer();
		mPreviousButton = (ImageButton) customView.findViewById(R.id.previous_level_button);
		mNextButton = (ImageButton) customView.findViewById(R.id.next_level_button);
		mPictureGridButton = (ImageButton) customView.findViewById(R.id.grid_pictures_button);
		mActionBarHints = (TextView) customView.findViewById(R.id.ab_level_hints_nb);
		
		mPreviousButton.setOnClickListener(mOnPreviousButtonClickListener);
		mNextButton.setOnClickListener(mOnNextButtonClickListener);
		mPictureGridButton.setOnClickListener(mOnPictureGridButtonClickListener);
		
		mImageLoader = new ImageLoader(getActivity());
		mCheckButton.setOnClickListener(mCheckButtonClickListener);
		mInfoButton.setOnClickListener(mInfoButtonClickListener);
		mHintLettersButton.setOnClickListener(mHintLettersClickListener);
		
		// Load special font
		Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/OpenSans-CondBold.ttf");
		mLevelTitle.setTypeface(face);

		// get number of hints the user can reveal
		mActionBarHints.setText(String.valueOf(getHintsAvailable()));
		mInputText.setText("");
		
		Level level;
		if (mLevelGivenFromPicturesGrid) {
			level = mCurrentLevel;
			mLevelGivenFromPicturesGrid = false;
		} else {
			if (savedInstanceState != null && savedInstanceState.containsKey(STATE_CURRENT_LEVEL)) {
				level = savedInstanceState.getParcelable(STATE_CURRENT_LEVEL);
			} else {
				level = getArguments().getParcelable(ARG_LEVEL);
			}
		}
		
		initLayout(level);
		initSounds();
		
		mInfoToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
		
		return view;
	}
	
	private int getHintsAvailable() {
		SharedPreferences sharedPreferences = getActivity().getPreferences(Application.MODE_PRIVATE);
		return sharedPreferences.getInt(BaseQuizzApplication.PREF_UNLOCKED_HINTS_COUNT_KEY, 0);
	}
	
	private void setHintsAvailable(int hintsNumber) {
		SharedPreferences sharedPreferences = getActivity().getPreferences(Application.MODE_PRIVATE);
		Editor prefEditor = sharedPreferences.edit();
		prefEditor.putInt(BaseQuizzApplication.PREF_UNLOCKED_HINTS_COUNT_KEY, hintsNumber);
		prefEditor.commit();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(STATE_CURRENT_LEVEL, mCurrentLevel);
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * This method is used when fragment is already added in backstack
	 * but we need to change the level argument from the pictures grid
	 */
	public void overrideCurrentLevelArgument(Level level) {
		mCurrentLevel = level;
		mLevelGivenFromPicturesGrid = true;
	}
	
	public void initSounds() {
		AssetFileDescriptor afd;
		try {
			Log.d("LevelSuccessDialog", "passe dans playSuccessSound");
			afd = getActivity().getAssets().openFd("sounds/success.wav");
			mSuccessPlayer = new MediaPlayer();
			mSuccessPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			mSuccessPlayer.prepare();
		} catch (IOException e) {
			Log.d("IOException : ", e.getMessage());
		}
	}
	
	public void playSuccessSound() {
		if (mSuccessPlayer != null)
			mSuccessPlayer.start();
	}

	/**
	 * Load, rotate picture, fill data
	 * 
	 * @param level
	 */
	private void initLayout(Level level) {
		if (level == null) {
			return;
		}
		mCurrentLevel = level;
		
		mImageLoader.displayImage(QuizzPlacesApplication.IMAGES_DIR + mCurrentLevel.imageName, 
				mPictureBig, ImageType.LOCAL);

		ObjectAnimator.ofFloat(mPictureBig, "rotation", 0.0f, mCurrentLevel.rotation / 4)
				.setDuration(0)
				.start();		

		if (mCurrentLevel.status == Level.STATUS_LEVEL_CLEAR) {
			mLevelTitle.setText(mCurrentLevel.response);
			mInputText.setVisibility(View.GONE);
			mCheckButton.setVisibility(View.GONE);
			mLevelCompletedLabel.setVisibility(View.VISIBLE);
			mHintLettersButton.setVisibility(View.GONE);
		} else {
			// Init partial response
			mPartialResponse = "" + mCurrentLevel.response.charAt(0);
			for (int i = 1; i < mCurrentLevel.response.length(); i++) {
				mPartialResponse += (mCurrentLevel.response.charAt(i) == ' ') ? ' ' : '_';
			}
			
			mLevelTitle.setText(mPartialResponse);
			mInputText.setVisibility(View.VISIBLE);
			mCheckButton.setVisibility(View.VISIBLE);
			mLevelCompletedLabel.setVisibility(View.GONE);
			mHintLettersButton.setVisibility(View.VISIBLE);
			
			InputFilter[] FilterArray = new InputFilter[1];
			FilterArray[0] = new InputFilter.LengthFilter(mCurrentLevel.response.length());
			mInputText.setFilters(FilterArray);

			
			/* ADD INPUT CELLS */
			/*
			Display display = getActivity().getWindowManager().getDefaultDisplay();
		    DisplayMetrics outMetrics = new DisplayMetrics();
		    display.getMetrics(outMetrics);

		    float density  = getResources().getDisplayMetrics().density;
		    float screenDpWidth  = outMetrics.widthPixels / density;
			
		    // TODO: Get from dimens.xml
		    int cellWidth = 32;

		    int currentRowWidth = 0;
		    
			mLettersTableLayout.removeAllViews();
			TableRow currentRow = new TableRow(getActivity());
			LayoutParams params = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			currentRow.setLayoutParams(params);
			currentRow.setGravity(Gravity.CENTER_HORIZONTAL);
			mLettersTableLayout.addView(currentRow);
			for (Character letter : level.response.toCharArray()) {
				
				// TODO: TAKE WORD IN ACCOUNT
				if (currentRowWidth + cellWidth > screenDpWidth) {
					currentRow = new TableRow(getActivity());
					currentRow.setLayoutParams(params);
					currentRow.setGravity(Gravity.CENTER_HORIZONTAL);
					mLettersTableLayout.addView(currentRow);
					
					currentRowWidth = 0;
				}
				
				TextView letterTextView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.item_table_letters, null);
				letterTextView.setText(""+letter);
				currentRow.addView(letterTextView);
				
				currentRowWidth += cellWidth;
			}*/
		}
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
						StringUtils.removeDiacritic(mCurrentLevel.response.charAt(i)));
				
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
	
	private void onSuccess() {
		SharedPreferences sharedPreferences = getActivity().getPreferences(Application.MODE_PRIVATE);

		int newHintsAvailableNb = sharedPreferences.getInt(BaseQuizzApplication.PREF_UNLOCKED_HINTS_COUNT_KEY, 0) + 
				sharedPreferences.getInt(BaseQuizzApplication.PREF_DEFAULT_NB_HINTS_ONSUCCESS_KEY, 2);

		Editor editor = sharedPreferences.edit();
		editor.putInt(BaseQuizzApplication.PREF_UNLOCKED_HINTS_COUNT_KEY, newHintsAvailableNb);
		editor.commit();
		
		// update actionBar hints number
		mActionBarHints.setText(String.valueOf(newHintsAvailableNb));
		
		mCurrentLevel.status = Level.STATUS_LEVEL_CLEAR;
		mCurrentLevel.update();
		
		mLevelTitle.setText(mCurrentLevel.response);
		mInputText.setText("");
		mHintLettersButton.setVisibility(View.GONE);
		mInputText.setVisibility(View.GONE);
		mCheckButton.setVisibility(View.GONE);
		mLevelCompletedLabel.setVisibility(View.VISIBLE);

		// Unlock next section if necessary
		boolean unlocked = DataManager.unlockNextSectionIfNecessary(mCurrentLevel.sectionId);
		if (unlocked) {
			mInfoToast.setText("You unlocked the next level!");
			mInfoToast.show();
		}
		
		// Run success vibrations
		if (PreferencesUtils.isVibrationEnabled(this.getActivity()))
			this.runSuccessVibration();
		
		if (PreferencesUtils.isAudioEnabled(this.getActivity()))
			this.playSuccessSound();
		
		// Launching LevelSuccessDialog
		Intent intent = new Intent(getActivity(), LevelSuccessDialog.class);
		intent.putExtra(LevelSuccessDialog.EXTRA_LEVEL, mCurrentLevel);
		startActivityForResult(intent, LEVEL_SUCCESS_REQUEST_CODE);
	}
	
	public void runSuccessVibration() {
		Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(300);
	}
	
	private void onError(int errorsCount) {
		
	}
	
	private void onNextLevel() {
		// Fade out current level, load next level when animation end
		ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(getView(), "alpha", 1f, 0f);
		alphaAnimator.setDuration(200);
		alphaAnimator.addListener(mStartLoadNextLevelAnimatorListener);			
		alphaAnimator.start();
	}
	
	private void onPreviousLevel() {
		// Fade out current level, load next level when animation end
		ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(getView(), "alpha", 1f, 0f);
		alphaAnimator.setDuration(200);
		alphaAnimator.addListener(mStartLoadPreviousLevelAnimatorListener);			
		alphaAnimator.start();
	}
	
	private void displayNewLevel(Level newLevel) {
		if (newLevel != null) {
			initLayout(newLevel);
		}
		
		// Fade in new level
		if (getView() != null) {
			ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(getView(), "alpha", 0f, 1f);
			alphaAnimator.setDuration(200);
			alphaAnimator.start();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == LEVEL_SUCCESS_REQUEST_CODE) {
			switch (resultCode) {
			
				case LevelSuccessDialog.RESULT_CODE_NEXT:
					onNextLevel();
					break;
					
				case LevelSuccessDialog.RESULT_CODE_BACK:
					getActivity().onBackPressed();
					break;
			}
		}
	}
	
	private void addLetters() {
		
	}
	
	private void useHintLetters() {
		int hints = getHintsAvailable();
		if (hints > 0) {
			addLetters();
			hints--;
			// update action bar and shared preferences
			mActionBarHints.setText(String.valueOf(hints));
			setHintsAvailable(hints);
		} else {
			mInfoToast.setText(R.string.level_not_enough_hints);
			mInfoToast.show();
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

	OnClickListener mInfoButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), HintsDialog.class);
			intent.putExtra(HintsDialog.EXTRA_LEVEL, mCurrentLevel);
			startActivity(intent);
		}
	};
	
	OnClickListener mHintLettersClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			useHintLetters();
		}
	};
	
	AnimatorListener mStartLoadPreviousLevelAnimatorListener = new AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
		}
		
		@Override
		public void onAnimationRepeat(Animator animation) {
		}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			displayNewLevel(DataManager.getPreviousLevel(mCurrentLevel));
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
		}
	};
	
	AnimatorListener mStartLoadNextLevelAnimatorListener = new AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
		}
		
		@Override
		public void onAnimationRepeat(Animator animation) {
		}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			displayNewLevel(DataManager.getNextLevel(mCurrentLevel));
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
		}
	};
	
	OnClickListener mOnNextButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onNextLevel();
		}
	};
	

	OnClickListener mOnPreviousButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onPreviousLevel();
		}
	};
	
	OnClickListener mOnPictureGridButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			FragmentContainer container = (FragmentContainer) getActivity();
			FragmentManager fragmentManager = getActivity()
					.getSupportFragmentManager();

			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			transaction.setCustomAnimations(R.anim.slide_in_right,
					R.anim.slide_out_left, R.anim.slide_in_left,
					R.anim.slide_out_right);
			
			Bundle args = new Bundle();
			args.putParcelable(BaseGridLevelsFragment.ARG_SECTION,
					DataManager.getSection(mCurrentLevel.sectionId));
			NavigationUtils.directNavigationTo(GridLevelsFragment.class,
					fragmentManager, container, true, transaction, args);
		}
	};
}
