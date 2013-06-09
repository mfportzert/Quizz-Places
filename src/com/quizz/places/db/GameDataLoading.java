package com.quizz.places.db;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

import com.quizz.core.db.QuizzDAO;
import com.quizz.core.managers.DataManager;
import com.quizz.core.models.Section;
import com.quizz.core.utils.PreferencesUtils;

public class GameDataLoading {

	public static final String DEBUG_TAG = GameDataLoading.class.getSimpleName();

	private Context mContext;
	private GameDataLoadingListener mGameDataLoadingListener;
	
	public interface GameDataLoadingListener {
		public void onGameLoadingStart();
		public void onGameLoadingProgress(int progress);
		public void onGameLoadingSuccess(List<Section> sections);
		public void onGameLoadingFailure(Exception e);
    }
	
	public GameDataLoading(Context pContext) {
		if (pContext instanceof GameDataLoadingListener) {
			mContext = pContext;
			mGameDataLoadingListener = (GameDataLoadingListener) mContext;
		} else {
			throw new ClassCastException(pContext.toString() + " must implement GameDataLoadingListener");
		}
	}

	private boolean initVersionInPreferences() {
		SharedPreferences sharedPreferences = ((Activity) mContext).getPreferences(Application.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		if (!sharedPreferences.contains(PreferencesUtils.PREF_VERSION_KEY)) {
			editor.putInt(PreferencesUtils.PREF_VERSION_KEY, PreferencesUtils.PREF_VERSION_VALUE);
		}
		return editor.commit();
	}
	
	private boolean initNbHintsAvailableInPreferences() {
		SharedPreferences sharedPreferences = ((Activity) mContext).getPreferences(Application.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(PreferencesUtils.PREF_UNLOCKED_HINTS_COUNT_KEY, 
				PreferencesUtils.PREF_DEFAULT_UNLOCKED_HINTS_COUNT_VALUE);
		return editor.commit();
	}

	public boolean upgradeVersionInPreferences() {
		SharedPreferences sharedPreferences = ((Activity) mContext).getPreferences(Application.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(PreferencesUtils.PREF_VERSION_KEY, PreferencesUtils.PREF_VERSION_VALUE);
		return editor.commit();
	}

	public void initPreferences() {
		initVersionInPreferences();
		initNbHintsAvailableInPreferences();
	}
	
	public boolean isDbUpgradeNeeded() {
		SharedPreferences sharedPreferences = ((Activity) mContext).getPreferences(Application.MODE_PRIVATE);
		if (sharedPreferences.getInt(PreferencesUtils.PREF_VERSION_KEY, 0) 
				< PreferencesUtils.PREF_VERSION_VALUE) {
			return true;
		}
		return false;
	}
	
	public boolean isFirstLaunch() {
		SharedPreferences sharedPreferences = ((Activity) mContext).getPreferences(Application.MODE_PRIVATE);
		if (!sharedPreferences.contains(PreferencesUtils.PREF_VERSION_KEY)) {
			return true;
		}
		return false;
	}
	
	public void executeAsyncGameLoading(boolean loadFromJson) {
		
		 try {
				QuizzDAO.INSTANCE.getDbHelper().createDatabases();
//			 	List<Section> sections = DataManager.getSections();
//			 	mGameDataLoadingListener.onGameLoadingSuccess(sections);
				new AsyncDataLoader(loadFromJson).execute();

		 } catch (IOException e) {
			 throw new Error("Unable to create database");
		 }
		
	}

	// ===========================================================
	// Inner classes
	// ===========================================================

	/**
	 * First launch asyncTask<br />
	 * Initiates database and fill it with json file content
	 * 
	 */
	private class AsyncDataLoader extends AsyncTask<Void, Integer, List<Section>> {

		private boolean mLoadFromJson = false;
		private Exception mException = null;
		
		public AsyncDataLoader(boolean loadFromJson) {
			mLoadFromJson = loadFromJson;
		}
		
		@Override
		protected void onPreExecute() {
			mGameDataLoadingListener.onGameLoadingStart();
			initPreferences();
			Log.e("ASYNC", "onPreExecute: "+System.currentTimeMillis());
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			if (progress[0] <= 100) {
				mGameDataLoadingListener.onGameLoadingProgress(progress[0]);
			}
		}

		@Override
		protected void onPostExecute(List<Section> result) {
			Log.e("ASYNC", "onPostExecute: "+System.currentTimeMillis());
			if (mException != null) {
				mGameDataLoadingListener.onGameLoadingFailure(mException);
			} else {
				mGameDataLoadingListener.onGameLoadingSuccess(result);
			}
		}

		@Override
		protected List<Section> doInBackground(Void... arg0) {
			Log.e("ASYNC", "doInBackground: "+System.currentTimeMillis());
			List<Section> sections = DataManager.getSections();
			DataManager.dataLoaded = true;
			return sections;
		}
	}
	
}
