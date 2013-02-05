package com.quizz.places.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.db.QuizzDAO;
import com.quizz.core.fragments.BaseStatsFragment;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.adapters.StatsItemAdapter;
import com.quizz.places.db.PlacesDAO;

public class StatsFragment extends BaseStatsFragment {
	private StatsItemAdapter mAdapter;
	private ListView mStatsListView;
	private boolean mHideActionBarOnDestroyView = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new StatsItemAdapter(getActivity());
		((BaseQuizzActivity) getActivity()).getQuizzActionBar().getMiddleText()
				.setText("Statistics");
		((BaseQuizzActivity) getActivity()).getQuizzActionBar().getRightText()
				.setText("");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_stats, container, false);

		PlacesDAO dao = new PlacesDAO(this.getActivity());

		mAdapter.setItems(dao.getStats());
		mStatsListView = (ListView) view.findViewById(R.id.StatsListView);
		mStatsListView.setAdapter(mAdapter);

		ObjectAnimator listDisplay = ObjectAnimator.ofFloat(mStatsListView,
				"alpha", 0f, 1f);
		listDisplay.setDuration(300);
		listDisplay.start();

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mHideActionBarOnDestroyView) {
			if (getActivity() instanceof BaseQuizzActivity) {
				((BaseQuizzActivity) getActivity()).getQuizzActionBar().hide(
						QuizzActionBar.MOVE_NORMAL);
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mHideActionBarOnDestroyView = true;
		if (getActivity() instanceof BaseQuizzActivity) {
			((BaseQuizzActivity) getActivity()).getQuizzActionBar()
					.showIfNecessary(QuizzActionBar.MOVE_NORMAL);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
