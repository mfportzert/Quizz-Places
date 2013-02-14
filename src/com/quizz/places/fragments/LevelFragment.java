package com.quizz.places.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.dialogs.HintsDialog;
import com.quizz.core.fragments.BaseLevelFragment;
import com.quizz.core.imageloader.ImageLoader;
import com.quizz.core.imageloader.ImageLoader.ImageType;
import com.quizz.core.models.Level;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.application.QuizzPlacesApplication;

public class LevelFragment extends BaseLevelFragment {

    private HintsDialog mHintsDialog;
    
    private TextView mLevelTitle;
    private Button mCheckButton;
    private EditText mInputText;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);

	View view = inflater.inflate(R.layout.fragment_level, container, false);
	ImageView pictureBig = (ImageView) view
		.findViewById(R.id.levelPictureBig);
	ImageButton hintsButton = (ImageButton) view.findViewById(R.id.levelHints);
	mLevelTitle = (TextView) view.findViewById(R.id.levelName);
	mCheckButton = (Button) view.findViewById(R.id.levelCheckButton);
	mInputText = (EditText) view.findViewById(R.id.levelInputResponse);
	
	/* Load + rotate picture */
	Level level = getArguments().getParcelable(ARG_LEVEL);
	ImageLoader imageLoader = new ImageLoader(getActivity());
	imageLoader.displayImage(QuizzPlacesApplication.IMAGES_DIR
		+ level.imageName, pictureBig, ImageType.LOCAL);

	float rotation = getArguments().getFloat(ARG_ROTATION);
	ObjectAnimator.ofFloat(pictureBig, "rotation", 0.0f, rotation / 2)
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
	if (level.difficulty.equals(Level.LEVEL_MEDIUM)) {
	    hardStar.setEnabled(false);
	} else if (!level.difficulty.equals(Level.LEVEL_HARD)) {
	    mediumStar.setEnabled(false);
	    hardStar.setEnabled(false);
	}

	/* Init layout */
	mLevelTitle.setText(level.partialResponse);
	mCheckButton.setOnClickListener(mCheckButtonClickListener);
	hintsButton.setOnClickListener(mHintsButtonClickListener);
	// TODO: Init input response hint (x words, x letters)
	return view;
    }

    // ===========================================================
    // Listeners
    // ===========================================================

    OnClickListener mCheckButtonClickListener = new OnClickListener() {

	@Override
	public void onClick(View v) {
	    mLevelTitle.setText(mInputText.getText());
	}
    };
    
    OnClickListener mHintsButtonClickListener = new OnClickListener() {

	@Override
	public void onClick(View v) {
	    if (mHintsDialog == null) {
		mHintsDialog = new HintsDialog(getActivity(), LayoutInflater.from(
			getActivity()).inflate(R.layout.dialog_hints, null));
	    }
	    
	    WindowManager.LayoutParams wmlp = mHintsDialog.getWindow().getAttributes();
	    wmlp.gravity = Gravity.CENTER;
	    // TODO: Make funny animations..
	    wmlp.windowAnimations = R.style.HintsDialogAnimation;
	    
	    mHintsDialog.show();
	}
    };
}
