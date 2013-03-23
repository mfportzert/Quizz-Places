package com.quizz.places.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.quizz.core.application.BaseQuizzApplication;

public class PreferencesUtils {

	public static SharedPreferences sharedPreferences(Activity pActivity) {
		return ((Activity) pActivity).getPreferences(Activity.MODE_PRIVATE);
	}
	
	public static boolean isVibrationEnabled(Activity pActivity) {
		return sharedPreferences(pActivity).getBoolean(BaseQuizzApplication.PREF_VIBRATION_KEY, false);		
	}
	
	public static boolean isAudioEnabled(Activity pActivity) {
		return sharedPreferences(pActivity).getBoolean(BaseQuizzApplication.PREF_AUDIO_KEY, false);
	}
	
	public static boolean isDisplayExitPopupEnabled(Activity pActivity) {
		return sharedPreferences(pActivity).getBoolean(BaseQuizzApplication.PREF_EXIT_POPUP_KEY, false);
	}
	
	public static void setVibrationEnabled(Activity pActivity, boolean status) {		
		Editor editor = sharedPreferences(pActivity).edit();
		editor.putBoolean(BaseQuizzApplication.PREF_VIBRATION_KEY, status);		
		editor.commit();
	}
	
	public static void setAudioEnabled(Activity pActivity, boolean status) {
		Editor editor = sharedPreferences(pActivity).edit();
		editor.putBoolean(BaseQuizzApplication.PREF_AUDIO_KEY, status);
		editor.commit();
	}
	
	public static void setDisplayExitPopupEnabled(Activity pActivity, boolean status) {
		Editor editor = sharedPreferences(pActivity).edit();
		editor.putBoolean(BaseQuizzApplication.PREF_EXIT_POPUP_KEY, false);
		editor.commit();
	}
}
