package com.quizz.places.fragments;


import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.quizz.core.fragments.BaseListSectionsFragment;
import com.quizz.core.models.Section;
import com.quizz.places.R;
import com.quizz.places.adapters.SectionsItemAdapter;

public class ListSectionsFragment extends BaseListSectionsFragment {
	
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
		
        View view = inflater.inflate(R.layout.fragment_list_sections, null);

        mSectionsListView = (ListView) view.findViewById(R.id.sectionsListView);
		mSectionsListView.setAdapter(mAdapter);
		
        return view;
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
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
			// TODO: Use list sections
			mAdapter.clear();
			for (int i = 0; i < 100; i++) {
				Section section = new Section();
				section.name = "Level "+i;
				mAdapter.add(section);
			}
			mAdapter.notifyDataSetChanged();
		}
	}
}
