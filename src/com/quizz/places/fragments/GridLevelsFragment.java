package com.quizz.places.fragments;


import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.quizz.core.fragments.BaseListLevelsFragment;
import com.quizz.core.interfaces.FragmentContainer;
import com.quizz.core.models.Level;
import com.quizz.core.utils.NavigationUtils;
import com.quizz.places.R;
import com.quizz.places.adapters.LevelsItemAdapter;

public class GridLevelsFragment extends BaseListLevelsFragment {
	
	private LevelsItemAdapter mAdapter;
	private GridView mLevelsGridView;
	private View mTransitionLevel;
	private ImageView mTransitionLevelImage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mAdapter = new LevelsItemAdapter(getActivity(), R.layout.item_grid_levels);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
        View view = inflater.inflate(R.layout.fragment_grid_levels, container, false);
        
        mTransitionLevel = (View) view.findViewById(R.id.transitionLevel);
        mTransitionLevelImage = (ImageView) mTransitionLevel.findViewById(R.id.levelPicture);
        mLevelsGridView = (GridView) view.findViewById(R.id.gridLevels);
        mLevelsGridView.setAdapter(mAdapter);
        mLevelsGridView.setOnItemClickListener(mLevelItemClickListener);
        
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

	@Override
	protected void onLevelsLoaded(ArrayList<Level> listLevels) {
		if (mAdapter != null) {
			mAdapter.clear();
			for (Level level : listLevels) {
				mAdapter.add(level);
			}
			mAdapter.notifyDataSetChanged();
		}
	}
	
	// ===========================================================
    // Listeners
    // ===========================================================
	
	OnItemClickListener mLevelItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			new LevelClickTransition(position).start();
		}
	};
		
	/**
	 * Transition Animation
	 * 
	 * Scale and translate a View corresponding to the level view clicked inside the grid
	 * Scale to 2x and translate to 0 x - 0 y
	 * 
	 * @author M-F.P
	 *
	 */
	public class LevelClickTransition {
		
		private final int mPosition;
		
		public LevelClickTransition(int position) {
			mPosition = position;
		}
		
		public void start() {
			/* Clone the clicked View inside the transition View */
			View view = mLevelsGridView.getChildAt(mPosition);
			ImageView picture = (ImageView) view.findViewById(R.id.levelPicture);
			if (picture != null) {
				mTransitionLevelImage.setImageDrawable(picture.getDrawable());
			}
			
			RelativeLayout.LayoutParams params = (LayoutParams) mTransitionLevel.getLayoutParams();
			params.width = view.getWidth();
			params.height = view.getHeight();
			params.setMargins(view.getTop(), view.getLeft(), 0, 0);
			mTransitionLevel.setLayoutParams(params);
			
			/* Create scale & translate animations */
			float scaleX = 2f;
			float scaleY = 2f;
			float pivotX = view.getLeft() + (view.getWidth() / 2);
			float pivotY = view.getTop() + (view.getHeight() / 2);
			float offsetX = view.getWidth() / scaleX;
			float offsetY = view.getHeight() / scaleY;
			
			AnimationSet animationSet = new AnimationSet(false);
			animationSet.addAnimation(new ScaleAnimation(1f, 2f, 1f, 2f, pivotX, pivotY));
			animationSet.addAnimation(new TranslateAnimation(view.getLeft(), offsetX, 
					view.getTop(), offsetY));
			animationSet.setAnimationListener(mTransitionListener);
			animationSet.setDuration(1000);
			
			/* Fade out the grid */
			Animation alphaAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
			alphaAnimation.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {}
				
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					mLevelsGridView.setVisibility(View.GONE);
				}
			});
			mLevelsGridView.startAnimation(alphaAnimation);
			
			/* Scale & translate the transition View */
			mTransitionLevel.setVisibility(View.VISIBLE);
			mTransitionLevel.startAnimation(animationSet);
		}
		
		AnimationListener mTransitionListener = new AnimationListener() {
		
			@Override
			public void onAnimationEnd(Animation animation) {
				FragmentContainer container = (FragmentContainer) getActivity();
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				
				FragmentTransaction transaction = fragmentManager.beginTransaction();
		    	transaction.setCustomAnimations(R.anim.fade_in_delay, R.anim.none,
		    			R.anim.none, R.anim.fade_out);
		    	
				NavigationUtils.directNavigationTo(LevelFragment.class, fragmentManager, container, 
						true, transaction);
			}
	
			@Override
			public void onAnimationRepeat(Animation animation) {}
	
			@Override
			public void onAnimationStart(Animation animation) {}
		};
	};
}
