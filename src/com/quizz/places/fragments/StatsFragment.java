package com.quizz.places.fragments;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.fragments.BaseStatsFragment;
import com.quizz.core.models.Section;
import com.quizz.core.models.Stat;
import com.quizz.core.utils.ConvertUtils;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.core.widgets.SectionProgressView;
import com.quizz.places.R;
import com.quizz.places.adapters.StatsItemAdapter;
import com.quizz.places.db.PlacesDAO;

public class StatsFragment extends BaseStatsFragment {
	private StatsItemAdapter mAchievementAdapter;
	private ListView mAchievementStatsListView;
	private StatsItemAdapter mSimpleAdapter;
	private ListView mSimpleStatsListView;
	private boolean mHideActionBarOnDestroyView = true;
	
	private int[] mProgressDrawables;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initProgressDrawables();
		mAchievementAdapter = new StatsItemAdapter(getActivity());
		mSimpleAdapter = new StatsItemAdapter(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_stats, container, false);

		QuizzActionBar actionBar = ((BaseQuizzActivity) getActivity())
				.getQuizzActionBar();
		actionBar.setCustomView(R.layout.ab_view_stats);
		View customView = actionBar.getCustomViewContainer();
		((TextView) customView.findViewById(R.id.ab_stat_middle_text))
				.setText(R.string.ab_stats_title);

		PlacesDAO dao = new PlacesDAO(this.getActivity());
		List<Stat> stats = dao.getStats();
		List<Stat> achievements = new ArrayList<Stat>();
		List<Stat> simples = new ArrayList<Stat>();
		if (stats != null && stats.size() > 0) {
			for (Stat s : stats) {
				if (s.isAchievement()) {
					achievements.add(s);
				} else {
					simples.add(s);
				}
			}
		}
		
		ViewGroup parentGroup = (ViewGroup)view.findViewById(R.id.StatsAchievementsContainer);
		this.fillAchivementLinearLayout(inflater, parentGroup, achievements);
		
		parentGroup = (ViewGroup)view.findViewById(R.id.SimpleStatsContainer);
		this.fillSimpleStatsLinearLayout(inflater, parentGroup, simples);		
		
//		mSimpleAdapter.setItems(simples);
//		mSimpleStatsListView = (ListView) view.findViewById(R.id.StatsSimpleListView);
//		mSimpleStatsListView.setAdapter(mSimpleAdapter);
//
//		ObjectAnimator listDisplay = ObjectAnimator.ofFloat(mSimpleStatsListView,
//				"alpha", 0f, 1f);
//		listDisplay.setDuration(300);
//		listDisplay.start();

		return view;
	}

	private void fillSimpleStatsLinearLayout(LayoutInflater inflater, ViewGroup parentGroup,
			List<Stat> stats) {
		char position = 0; // guess we will never have more than 255 achievements
		for (Stat stat : stats) {
			View subview = inflater.inflate(R.layout.item_simple_stat, null, true);
			((ImageView) subview.findViewById(R.id.StatIcon))
			.setImageDrawable(this.getActivity().getResources()
			.getDrawable(stat.getIcon()));
			((TextView) subview.findViewById(R.id.StatLabel)).setText(stat.getLabel());
			((TextView) subview.findViewById(R.id.StatDoneOnTotal))
			.setText(String.valueOf(stat.getDone()));
			if ((position + 1) == stats.size()) {
				subview.findViewById(R.id.SimpleStatItemSeparator).setVisibility(View.GONE);
			}
			parentGroup.addView(subview);
			position++;
		}
	}
	
	@SuppressLint("NewApi")
	private void fillAchivementLinearLayout(LayoutInflater inflater, ViewGroup parentGroup,
			List<Stat> stats) {
		
		int verticalPadding = (int) ConvertUtils.convertDpToPixels(2.5f,
				this.getActivity());
		int horizontalPadding = (int) ConvertUtils.convertDpToPixels(3f,
				this.getActivity());

		char position = 0; // guess we will never have more than 255 achievements
		for (Stat stat : stats) {
			View subview = inflater.inflate(R.layout.item_achievement_stat, null, true);
			((TextView) subview.findViewById(R.id.StatLabel)).setText(stat.getLabel() + " : " + 
					String.valueOf(stat.getDone()) + " / "
					+ String.valueOf(stat.getTotal()));
//			((TextView) subview.findViewById(R.id.StatDoneOnTotal))
//						.setText(String.valueOf(stat.getDone()) + " / "
//						+ String.valueOf(stat.getTotal()));
			((ImageView) subview.findViewById(R.id.StatIcon))
						.setImageDrawable(this.getActivity().getResources()
						.getDrawable(stat.getIcon()));
			((SectionProgressView) subview.findViewById(R.id.StatProgress))
						.setPaddingProgress(horizontalPadding,verticalPadding,
						horizontalPadding, verticalPadding);
			((SectionProgressView) subview.findViewById(R.id.StatProgress))
						.setProgressRes(mProgressDrawables[position
			            % mProgressDrawables.length]/*R.drawable.fg_section_progress_blue_small*/);
			((SectionProgressView) subview.findViewById(R.id.StatProgress))
						.setProgressValue(stat.getProgressInPercent());
			if (stat.getDone() == stat.getTotal()) {
				((ImageView) subview.findViewById(R.id.StatCupIcon))
						.setImageDrawable(this.getActivity().getResources()
						.getDrawable(R.drawable.gold_cup));
				((ImageView) subview.findViewById(R.id.StatCupIcon)).setAlpha(1.0f);
			}
			if ((position + 1) == stats.size()) {
				subview.findViewById(R.id.AchievementItemSeparator).setVisibility(View.GONE);
			}
			parentGroup.addView(subview);
			position++;
		}
		
		parentGroup.invalidate();
	}
	
	private void initProgressDrawables() {
		mProgressDrawables = new int[] { R.drawable.fg_section_progress_yellow };
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
