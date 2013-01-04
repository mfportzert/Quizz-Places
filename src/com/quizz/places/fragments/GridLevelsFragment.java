package com.quizz.places.fragments;


import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.fragments.BaseListLevelsFragment;
import com.quizz.core.models.Level;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.adapters.LevelsItemAdapter;

public class GridLevelsFragment extends BaseListLevelsFragment {
	
	private LevelsItemAdapter mAdapter;
	private GridView mLevelsListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mAdapter = new LevelsItemAdapter(getActivity(), R.layout.item_grid_levels);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
        View view = inflater.inflate(R.layout.fragment_grid_levels, container, false);
        
        mLevelsListView = (GridView) view.findViewById(R.id.gridLevels);
        mLevelsListView.setAdapter(mAdapter);
        
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
	
	OnItemClickListener mSectionItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
		}
	};
}
