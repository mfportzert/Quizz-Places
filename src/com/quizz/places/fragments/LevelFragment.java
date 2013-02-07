package com.quizz.places.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.fragments.BaseLevelFragment;
import com.quizz.core.imageloader.ImageLoader;
import com.quizz.core.imageloader.ImageLoader.ImageType;
import com.quizz.core.models.Level;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.application.QuizzPlacesApplication;

public class LevelFragment extends BaseLevelFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);

	View view = inflater.inflate(R.layout.fragment_level, container, false);
	ImageView pictureBig = (ImageView) view.findViewById(R.id.levelPictureBig);
	TextView levelName = (TextView) view.findViewById(R.id.levelName);
	
	Level level = getArguments().getParcelable(ARG_LEVEL);
	ImageLoader imageLoader = new ImageLoader(getActivity());
	imageLoader.displayImage(QuizzPlacesApplication.IMAGES_DIR + level.imageName, pictureBig,
		ImageType.LOCAL);
	
	QuizzActionBar actionBar = ((BaseQuizzActivity) getActivity()).getQuizzActionBar();
	actionBar.setCustomView(R.layout.ab_view_level);

	View customView = actionBar.getCustomViewContainer();
	ImageView mediumStar = (ImageView) customView.findViewById(R.id.levelStarMedium);
	ImageView hardStar = (ImageView) customView.findViewById(R.id.levelStarHard);
	
	mediumStar.setEnabled(true);
	hardStar.setEnabled(true);
	if (level.difficulty.equals(Level.DIFFICULTY_MEDIUM)) {
	    hardStar.setEnabled(false);
	} else if (!level.difficulty.equals(Level.DIFFICULTY_HARD)) {
	    mediumStar.setEnabled(false);
	    hardStar.setEnabled(false);
	}
	
	levelName.setText("Le C _ _ _ _ _ _");
	
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

    // ===========================================================
    // Listeners
    // ===========================================================

    OnItemClickListener mPictureClickListener = new OnItemClickListener() {

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}
    };
}
