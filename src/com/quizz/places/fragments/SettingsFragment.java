package com.quizz.places.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.fragments.BaseSettingsFragment;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;
import com.quizz.places.db.PlacesDAO;
import com.quizz.places.utils.PreferencesUtils;

public class SettingsFragment extends BaseSettingsFragment {
	private boolean mHideActionBarOnDestroyView = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(R.layout.fragment_settings, container, false);

		final TextView audioLabel = (TextView) view.findViewById(R.id.AudioLabel);
		final TextView vibrationLabel = (TextView) view.findViewById(R.id.VibrationLabel);
		final CheckBox audioCheckbox = (CheckBox) view.findViewById(R.id.AudioCheckbox);
		final CheckBox vibrationCheckbox = (CheckBox) view.findViewById(R.id.VibrationCheckbox);

		final Button resetButton = (Button) view.findViewById(R.id.ResetButton);
		
		if (!PreferencesUtils.isAudioEnabled(this.getActivity())) {
			PreferencesUtils.setAudioEnabled(this.getActivity(), false);
		} else {
			audioCheckbox.setChecked(PreferencesUtils.isAudioEnabled(this.getActivity()));
			audioLabel.setText(audioCheckbox.isChecked() ? R.string.audio_on : R.string.audio_off);
		}
		if (!PreferencesUtils.isVibrationEnabled(this.getActivity())) {
			PreferencesUtils.setVibrationEnabled(this.getActivity(), false);
		} else {
			vibrationCheckbox.setChecked(PreferencesUtils.isVibrationEnabled(this.getActivity()));
			vibrationLabel.setText(
					vibrationCheckbox.isChecked() ? R.string.vibration_on : R.string.vibration_off);			
		}
			
		audioCheckbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PreferencesUtils.setAudioEnabled(getActivity(), audioCheckbox.isChecked());
				audioLabel.setText(audioCheckbox.isChecked() ? R.string.audio_on : R.string.audio_off);
			}
		});
		
		vibrationCheckbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PreferencesUtils.setVibrationEnabled(getActivity(), vibrationCheckbox.isChecked());
				vibrationLabel.setText(
						vibrationCheckbox.isChecked() ? R.string.vibration_on : R.string.vibration_off);
			}
		});

		resetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(R.string.confirm_reset).setPositiveButton(R.string.yes, dialogClickListener)
				    .setNegativeButton(R.string.no, dialogClickListener).show();
				
			}
		});
		
		initActionbarView();
		
		return view;
	}

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	            //Yes button clicked
	        	new PlacesDAO(getActivity()).resetDB();
	            break;
	        case DialogInterface.BUTTON_NEGATIVE:
	            //No button clicked
	        	break;
	        }
	    }
	};

	
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
	
	private void initActionbarView() {
		QuizzActionBar actionBar = ((BaseQuizzActivity) getActivity()).getQuizzActionBar();
		actionBar.setCustomView(R.layout.ab_view_settings);
		View customView = actionBar.getCustomViewContainer();
		((TextView) customView.findViewById(R.id.ab_settings_middle_text))
				.setText(R.string.ab_settings_title);
	}
}
