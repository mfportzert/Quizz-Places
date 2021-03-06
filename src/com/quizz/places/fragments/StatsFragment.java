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
	
	private boolean mHideActionBarOnDestroyView = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		badgeProgress.setDisplayInitialProgressIfEmpty(true);
		
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
			badgeProgressLabel.setText(((int) percentDone) + " %");
		}
		List<Stat> simples = new ArrayList<Stat>();
		
		simples.add(new Stat(R.drawable.levels, this.getString(R.string.places_found),
				DataManager.getClearedLevelTotalCount(), DataManager.getLevelTotalCount(), false));
		simples.add(new Stat(R.drawable.sections, this.getString(R.string.unlocked_lvl),
				DataManager.getUnlockedSectionsCount(), DataManager.getSections().size(), false));
		simples.add(new Stat(R.drawable._levels, this.getString(R.string.completed_sections),
				DataManager.getCompletedSectionsCount(), DataManager.getSections().size(), false));
		simples.add(new Stat(R.drawable.hint, this.getString(R.string.used_hints),
				PreferencesUtils.getUsedHintsCount(this.getActivity()), 0, false));
		
		ViewGroup parentGroup = (ViewGroup)view.findViewById(R.id.SimpleStatsContainer);
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
