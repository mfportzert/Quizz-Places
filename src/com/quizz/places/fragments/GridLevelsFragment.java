package com.quizz.places.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.fragments.BaseGridLevelsFragment;
import com.quizz.core.models.Level;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.adapters.LevelsItemAdapter;
import com.quizz.places.adapters.LevelsItemAdapter.OnPictureClickListener;

public class GridLevelsFragment extends BaseGridLevelsFragment {

	private GridView mLevelsGridView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mAdapter = new LevelsItemAdapter(getActivity(),
				R.layout.item_grid_levels);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_grid_levels, container, false);
		
		mLevelsGridView = (GridView) view.findViewById(R.id.gridLevels);
		mLevelsGridView.setAdapter(mAdapter);
		
		((LevelsItemAdapter) mAdapter).setOnPictureClickListener(mLevelPictureItemClickListener);
		mAdapter.notifyDataSetChanged();

		int picturesClearedCount = 0;
		for (int i = 0; i < mAdapter.getCount(); i++) {
			picturesClearedCount += (((Level) mAdapter.getItem(i)).status == 
					Level.STATUS_LEVEL_CLEAR) ? 1 : 0;
		}
		setActionbarView(getActivity().getString(R.string.ab_levels_grid_title),
				String.valueOf(picturesClearedCount) + "/" + String.valueOf(mAdapter.getCount()));
		
		return view;
	}

	private void setActionbarView(String middleText, String rightText) {
		QuizzActionBar actionBar = ((BaseQuizzActivity) getActivity()).getQuizzActionBar();
		actionBar.setCustomView(R.layout.ab_view_sections);
		View customView = actionBar.getCustomViewContainer();
		((TextView) customView.findViewById(R.id.ab_section_middle_text))
				.setText(middleText);
		((TextView) customView.findViewById(R.id.ab_section_right_text)).setText(rightText);
	}
	
	// ===========================================================
	// Listeners
	// ===========================================================

	OnPictureClickListener mLevelPictureItemClickListener = new OnPictureClickListener() {

		@Override
		public void onClick(View itemView, ImageView pictureView, int position) {
			FragmentManager fragmentManager = getActivity()
					.getSupportFragmentManager();

			// Set the new level
			Fragment fragment = fragmentManager.findFragmentByTag(
					LevelFragment.class.getSimpleName());
			if (fragment != null) {
				LevelFragment levelFragment = (LevelFragment) fragment;
				levelFragment.overrideCurrentLevelArgument(mAdapter.getItem(position));
			}
			
			// Get back to the level
			getActivity().onBackPressed();
		}
	};
}
