package com.quizz.places.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.fragments.BaseStatsFragment;
import com.quizz.core.managers.DataManager;
import com.quizz.core.models.Badge;
import com.quizz.core.models.Stat;
import com.quizz.core.utils.ConvertUtils;
import com.quizz.core.utils.PreferencesUtils;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.core.widgets.SectionProgressView;
import com.quizz.places.R;
import com.quizz.places.adapters.StatsItemAdapter;

public class StatsFragment extends BaseStatsFragment {
	// TODO: Bouger dans Badge
	private static List<Badge> sBadges = new ArrayList<Badge>();
	
	// TODO: Bouger les valeurs dans arrays.xml
	static {
		sBadges.add(new Badge(R.string.badge_traveler, R.drawable.badge_traveler, 0));
		sBadges.add(new Badge(R.string.badge_tourist, R.drawable.badge_tourist, 10));
		sBadges.add(new Badge(R.string.badge_explorer, R.drawable.badge_explorer, 21));
		sBadges.add(new Badge(R.string.badge_discovery, R.drawable.badge_discovery, 39));
		sBadges.add(new Badge(R.string.badge_extreme_vacationer, R.drawable.badge_extreme, 56));
		sBadges.add(new Badge(R.string.badge_globe_trotter, R.drawable.badge_globe_trotter, 78));
		sBadges.add(new Badge(R.string.badge_emblem, R.drawable.badge_emblem, 100));
		sBadges.add(new Badge(R.string.badge_legend, R.drawable.badge_legend, 120));
	}
	
	public static Badge getCurrentBadge() {
		int totalCleared = DataManager.getClearedLevelTotalCount();
		Badge currentBadge = null;
		for (Badge badge : sBadges) {
			if (totalCleared >= badge.requiredLevelProgression) {
				currentBadge = badge;
			} else {
				break;
			}
		}
		return currentBadge;
	}
	
	public static int getRemainingLevelsForNextBadge() {
		Badge currentBadge = getCurrentBadge();
		int requiredLevel = -1;
		int sectionIndex = sBadges.indexOf(currentBadge);
		if ((sectionIndex + 1) < sBadges.size()) {
			requiredLevel = sBadges.get(sectionIndex + 1).requiredLevelProgression;
			requiredLevel -= DataManager.getClearedLevelTotalCount();
		}
		return requiredLevel;
	}
	
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
		
		Badge currentBadge = getCurrentBadge();
		
		TextView badgeLabel = (TextView) view.findViewById(R.id.BadgeLabel);
		ImageView badgeIcon = (ImageView) view.findViewById(R.id.BadgeIcon);
		SectionProgressView badgeProgress = (SectionProgressView) view.findViewById(R.id.BadgeProgress);
		TextView badgeProgressLabel = (TextView) view.findViewById(R.id.BadgeProgressLabel);
		
		badgeLabel.setText(currentBadge.label);
		badgeIcon.setImageResource(currentBadge.icon);
		
		int currentBadgeIndex = sBadges.indexOf(currentBadge);
		boolean isLastBadge = (currentBadgeIndex == sBadges.size() - 1);
		if (isLastBadge) {
			badgeProgress.setVisibility(View.GONE);
			badgeProgressLabel.setVisibility(View.GONE);
		} else {
			Badge nextBadge = sBadges.get(currentBadgeIndex + 1);
			int requiredLevelsBeforeNextBadge = nextBadge.requiredLevelProgression - 
					currentBadge.requiredLevelProgression;
			int clearedLevelsNb = DataManager.getClearedLevelTotalCount();
			int remainingLevelsBeforeNextBadge = nextBadge.requiredLevelProgression - clearedLevelsNb;
			int levelsDone = requiredLevelsBeforeNextBadge - remainingLevelsBeforeNextBadge;
			
			float percentDone = ((float) levelsDone / (float) requiredLevelsBeforeNextBadge) * 100;
			badgeProgress.setProgressValue(percentDone);
			badgeProgress.setPaddingProgress(2, 2, 2, 2);
			badgeProgress.setProgressDrawable(getResources().getDrawable(R.drawable.fg_section_progress_yellow));
			badgeProgressLabel.setText(levelsDone+" / "+requiredLevelsBeforeNextBadge);
		}				
/*
		PlacesDAO dao = new PlacesDAO(this.getActivity());
		List<Stat> stats = dao.getStats();
		List<Stat> achievements = new ArrayList<Stat>();
		
		if (stats != null && stats.size() > 0) {
			for (Stat s : stats) {
				if (s.isAchievement()) {
					achievements.add(s);
				} else {
					simples.add(s);
				}
			}
		}
		*/
		/*ViewGroup parentGroup = (ViewGroup)view.findViewById(R.id.StatsAchievementsContainer);
		this.fillAchivementLinearLayout(inflater, parentGroup, achievements);
		
		parentGroup = (ViewGroup)view.findViewById(R.id.SimpleStatsContainer);
		this.fillSimpleStatsLinearLayout(inflater, parentGroup, simples);		*/
		
//		mSimpleAdapter.setItems(simples);
//		mSimpleStatsListView = (ListView) view.findViewById(R.id.StatsSimpleListView);
//		mSimpleStatsListView.setAdapter(mSimpleAdapter);
//
//		ObjectAnimator listDisplay = ObjectAnimator.ofFloat(mSimpleStatsListView,
//				"alpha", 0f, 1f);
//		listDisplay.setDuration(300);
//		listDisplay.start();
		

		List<Stat> simples = new ArrayList<Stat>();
		
		simples.add(new Stat(R.drawable.levels, this.getString(R.string.completed_sections),
				DataManager.getCompletedSectionsCount(), DataManager.getSections().size(), false));
		simples.add(new Stat(R.drawable.levels, this.getString(R.string.places_found),
				DataManager.getClearedLevelTotalCount(), DataManager.getLevelTotalCount(), false));
		simples.add(new Stat(R.drawable.sections, this.getString(R.string.unlocked_lvl),
				DataManager.getUnlockedSectionsCount(), DataManager.getSections().size(), false));
		simples.add(new Stat(R.drawable.hint, this.getString(R.string.used_hints),
				PreferencesUtils.getUsedHintsCount(this.getActivity()), 0, false));
		
		ViewGroup parentGroup = (ViewGroup)view.findViewById(R.id.StatsAchievementsContainer);
		this.fillBadgeView(inflater, parentGroup, getCurrentBadge());
		
		parentGroup = (ViewGroup)view.findViewById(R.id.SimpleStatsContainer);
		this.fillSimpleStatsLinearLayout(inflater, parentGroup, simples);
		
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
			.setText(String.valueOf(stat.getDone()) + ((stat.getTotal() > 0) ? "/" + String.valueOf(stat.getTotal()) : ""));
			
			if ((position + 1) == stats.size()) {
				subview.findViewById(R.id.SimpleStatItemSeparator).setVisibility(View.GONE);
			}
			parentGroup.addView(subview);
			position++;
		}
	}
	
	private void fillBadgeView(LayoutInflater inflater, ViewGroup parentGroup, Badge badge)
	{
		int verticalPadding = (int) ConvertUtils.convertDpToPixels(2.5f,
				this.getActivity());
		int horizontalPadding = (int) ConvertUtils.convertDpToPixels(3f,
				this.getActivity());
		View subview = inflater.inflate(R.layout.item_achievement_stat, null, true);
		
		((ImageView) subview.findViewById(R.id.BadgeImage))
				.setImageDrawable(this.getActivity().getResources().getDrawable(badge.icon));
		
		((TextView) subview.findViewById(R.id.BadgeText)).setText(badge.label);
		
		((TextView) subview.findViewById(R.id.ImagesLeftForNextPromotion)).setText(
			this.getActivity()
			.getString(R.string.levels_left_before_next_badge, getRemainingLevelsForNextBadge()));
	
		((SectionProgressView) subview.findViewById(R.id.StatProgress))
			.setPaddingProgress(horizontalPadding,verticalPadding,
			horizontalPadding, verticalPadding);
		
		((SectionProgressView) subview.findViewById(R.id.StatProgress))
			.setProgressRes(R.drawable.fg_section_progress_blue);
		
		((SectionProgressView) subview.findViewById(R.id.StatProgress))
			.setProgressValue(
					(getRemainingLevelsForNextBadge() * 100.0f) / DataManager.getLevelTotalCount());
		
		parentGroup.addView(subview);
		parentGroup.invalidate();
	}
	
//	@SuppressLint("NewApi")
//	private void fillAchivementLinearLayout(LayoutInflater inflater, ViewGroup parentGroup,
//			List<Stat> stats) {
//		
//		int verticalPadding = (int) ConvertUtils.convertDpToPixels(2.5f,
//				this.getActivity());
//		int horizontalPadding = (int) ConvertUtils.convertDpToPixels(3f,
//				this.getActivity());
//
//		char position = 0; // guess we will never have more than 255 achievements
//		for (Stat stat : stats) {
//			View subview = inflater.inflate(R.layout.item_achievement_stat, null, true);
//			((TextView) subview.findViewById(R.id.StatLabel)).setText(stat.getLabel() + " : " + 
//					String.valueOf(stat.getDone()) + " / "
//					+ String.valueOf(stat.getTotal()));
////			((TextView) subview.findViewById(R.id.StatDoneOnTotal))
////						.setText(String.valueOf(stat.getDone()) + " / "
////						+ String.valueOf(stat.getTotal()));
//			((ImageView) subview.findViewById(R.id.StatIcon))
//						.setImageDrawable(this.getActivity().getResources()
//						.getDrawable(stat.getIcon()));
//			((SectionProgressView) subview.findViewById(R.id.StatProgress))
//						.setPaddingProgress(horizontalPadding,verticalPadding,
//						horizontalPadding, verticalPadding);
//			((SectionProgressView) subview.findViewById(R.id.StatProgress))
//						.setProgressRes(mProgressDrawables[position
//			            % mProgressDrawables.length]/*R.drawable.fg_section_progress_blue_small*/);
//			((SectionProgressView) subview.findViewById(R.id.StatProgress))
//						.setProgressValue(stat.getProgressInPercent());
//			if (stat.getDone() == stat.getTotal()) {
//				((ImageView) subview.findViewById(R.id.StatCupIcon))
//						.setImageDrawable(this.getActivity().getResources()
//						.getDrawable(R.drawable.gold_cup));
//				((ImageView) subview.findViewById(R.id.StatCupIcon)).setAlpha(1.0f);
//			}
//			if ((position + 1) == stats.size()) {
//				subview.findViewById(R.id.AchievementItemSeparator).setVisibility(View.GONE);
//			}
//			parentGroup.addView(subview);
//			position++;
//		}
//		
//		parentGroup.invalidate();
//	}
	
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
