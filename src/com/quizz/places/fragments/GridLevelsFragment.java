package com.quizz.places.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.fragments.BaseGridLevelsFragment;
import com.quizz.core.interfaces.FragmentContainer;
import com.quizz.core.utils.NavigationUtils;
import com.quizz.places.R;
import com.quizz.places.adapters.LevelsItemAdapter;

public class GridLevelsFragment extends BaseGridLevelsFragment {

    private GridView mLevelsGridView;
    private View mTransitionLevel;
    private ImageView mTransitionLevelImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	mAdapter = new LevelsItemAdapter(getActivity(), R.layout.item_grid_levels);
	super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);

	View view = inflater.inflate(R.layout.fragment_grid_levels, container, false);

	mTransitionLevel = (View) view.findViewById(R.id.transitionLevel);
	mTransitionLevelImage = (ImageView) mTransitionLevel.findViewById(R.id.levelPicture);
	View difficulty = mTransitionLevel.findViewById(R.id.levelDifficulty);
	View statusIcon = mTransitionLevel.findViewById(R.id.levelStatusIcon);
	difficulty.setVisibility(View.GONE);
	statusIcon.setVisibility(View.GONE);

	mLevelsGridView = (GridView) view.findViewById(R.id.gridLevels);
	mLevelsGridView.setAdapter(mAdapter);
	mLevelsGridView.setOnItemClickListener(mLevelItemClickListener);

	mAdapter.notifyDataSetChanged();

	return view;
    }

    // ===========================================================
    // Listeners
    // ===========================================================

    OnItemClickListener mLevelItemClickListener = new OnItemClickListener() {

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
	    new LevelClickTransition(view, position).start();
	}
    };

    // ===========================================================
    // Inner classes
    // ===========================================================

    /**
     * Transition Animation
     * 
     * Scale and translate a View corresponding to the level view clicked inside
     * the grid Scale to 2x and translate to 0 x - 0 y
     * 
     * @author M-F.P
     * 
     */
    public class LevelClickTransition {

	private final int mPosition;
	private View mView;

	public LevelClickTransition(View view, int position) {
	    mView = view;
	    mPosition = position;
	}

	public void start() {
	    /* Clone the clicked View inside the transition View */
	    ImageView picture = (ImageView) mView.findViewById(R.id.levelPicture);
	    if (picture != null) {
		mTransitionLevelImage.setImageDrawable(picture.getDrawable());
	    }

	    RelativeLayout.LayoutParams params = (LayoutParams) mTransitionLevel.getLayoutParams();
	    params.width = mView.getWidth();
	    params.height = (mView.getHeight() < picture.getHeight()) ? picture.getHeight() : mView
		    .getHeight();
	    params.leftMargin = mView.getLeft();
	    params.rightMargin = mView.getRight();
	    params.topMargin = mView.getTop();
	    params.bottomMargin = mView.getBottom();

	    mTransitionLevel.setLayoutParams(params);

	    /* Create scale & translate animations */
	    float pivotX = picture.getLeft() + (picture.getWidth() / 2);
	    float pivotY = picture.getTop() + (picture.getHeight() / 2);

	    AnimationSet animationSet = new AnimationSet(false);
	    animationSet.addAnimation(new ScaleAnimation(1f, 1.5f, 1f, 1.5f, pivotX, pivotY));
	    animationSet.addAnimation(new AlphaAnimation(1f, 0f));
	    animationSet.setInterpolator(new LinearInterpolator());
	    animationSet.setFillAfter(true);
	    animationSet.setDuration(200);

	    LevelsItemAdapter adapter = (LevelsItemAdapter) mAdapter;
	    ObjectAnimator
		    .ofFloat(mTransitionLevelImage, "rotation", 0.0f,
			    adapter.getPictureRotation(mPosition)).setDuration(0).start();

	    /* Fade out the grid */
	    Animation alphaAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
	    alphaAnimation.setFillAfter(true);
	    alphaAnimation.setAnimationListener(mTransitionListener);
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
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.none, R.anim.none,
			R.anim.fade_out);

		NavigationUtils.directNavigationTo(LevelFragment.class, fragmentManager, container,
			true, transaction);
	    }

	    @Override
	    public void onAnimationRepeat(Animation animation) {
	    }

	    @Override
	    public void onAnimationStart(Animation animation) {
	    }
	};
    };
}
