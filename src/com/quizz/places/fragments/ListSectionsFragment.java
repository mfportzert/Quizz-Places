package com.quizz.places.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.fragments.BaseListSectionsFragment;
import com.quizz.core.interfaces.FragmentContainer;
import com.quizz.core.models.Section;
import com.quizz.core.utils.NavigationUtils;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.adapters.SectionsItemAdapter;

public class ListSectionsFragment extends BaseListSectionsFragment {

    private ListView mSectionsListView;
    private boolean mHideActionBarOnDestroyView = true;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);

	View view = inflater.inflate(R.layout.fragment_list_sections, container, false);

	mSectionsListView = (ListView) view.findViewById(R.id.sectionsListView);
	mSectionsListView.setAdapter(mAdapter);
	mSectionsListView.setOnItemClickListener(mSectionItemClickListener);

	mLoadingView = view.findViewById(R.id.loadingView);

	ObjectAnimator listDisplay = ObjectAnimator.ofFloat(mSectionsListView, "alpha", 0f, 1f);
	listDisplay.setDuration(300);
	listDisplay.start();

	return view;
    }
    
    @Override
    protected void initAdapter(ArrayAdapter<Section> adapter) {
	adapter = new SectionsItemAdapter(getActivity(), R.layout.item_list_sections);
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
	    ((BaseQuizzActivity) getActivity()).getQuizzActionBar().showIfNecessary(
		    QuizzActionBar.MOVE_NORMAL);
	}
    }

    // ===========================================================
    // Listeners
    // ===========================================================

    OnItemClickListener mSectionItemClickListener = new OnItemClickListener() {

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	    mHideActionBarOnDestroyView = false;
	    FragmentContainer container = (FragmentContainer) getActivity();
	    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

	    FragmentTransaction transaction = fragmentManager.beginTransaction();
	    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
		    R.anim.slide_in_left, R.anim.slide_out_right);

	    NavigationUtils.directNavigationTo(GridLevelsFragment.class, fragmentManager,
		    container, true, transaction);
	}
    };
}
