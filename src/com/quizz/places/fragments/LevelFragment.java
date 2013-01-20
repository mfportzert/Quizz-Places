package com.quizz.places.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.quizz.core.fragments.BaseLevelFragment;
import com.quizz.core.listeners.LoadPictureListener;
import com.quizz.core.tasks.LoadPictureTask;
import com.quizz.places.R;
import com.quizz.places.application.QuizzPlacesApplication;

public class LevelFragment extends BaseLevelFragment implements LoadPictureListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);

	View view = inflater.inflate(R.layout.fragment_level, container, false);

	ImageView pictureBig = (ImageView) view.findViewById(R.id.levelPictureBig);
	new LoadPictureTask(getActivity(), QuizzPlacesApplication.IMAGES_DIR + "colisee.jpg",
		pictureBig, this).execute();

	return view;
    }

    @Override
    public void onPictureLoaded(Drawable drawable, ImageView imageView) {
	imageView.setImageDrawable(drawable);
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
