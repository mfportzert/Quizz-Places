package com.quizz.places.fragments;


import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.fragments.BaseListSectionsFragment;
import com.quizz.core.models.Section;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.adapters.SectionsItemAdapter;

public class GridLevelsFragment extends BaseListSectionsFragment {
	
	private SectionsItemAdapter mAdapter;
	private ListView mSectionsListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mAdapter = new SectionsItemAdapter(getActivity(), R.layout.item_list_sections);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
        View view = inflater.inflate(R.layout.fragment_grid_levels, container, false);
        return view;
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
        if (getActivity() instanceof BaseQuizzActivity) {
        	((BaseQuizzActivity) getActivity()).getQuizzActionBar().hide(QuizzActionBar.MOVE_NORMAL);
        }
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getActivity() instanceof BaseQuizzActivity) {
			((BaseQuizzActivity) getActivity()).getQuizzActionBar().show(QuizzActionBar.MOVE_NORMAL);
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

	@Override
	protected void onSectionsLoaded(ArrayList<Section> listSections) {
		if (mAdapter != null) {
			mAdapter.clear();
			for (Section section : listSections) {
				section.name = "Level "+section.number;
				mAdapter.add(section);
			}
			mAdapter.notifyDataSetChanged();
		}
	}
	
	// ===========================================================
    // Listeners
    // ===========================================================
	
	OnItemClickListener mSectionItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
		}
	};
}
