package com.quizz.places.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quizz.core.activities.BaseQuizzActivity;
import com.quizz.core.widgets.QuizzActionBar;
import com.quizz.places.R;

public class WikiFragment extends Fragment {
	
	public static final String ARG_WIKI_LINK = "WikiActivity.EXTRA_WIKI_LINK";
	public static final String ARG_LEVEL_NAME = "WikiActivity.ARG_LEVEL_NAME";
	
	private WebView mWebView;
	private ProgressBar mLoadingProgress;
 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_wiki, container, false);
 
		QuizzActionBar actionBar = ((BaseQuizzActivity) getActivity()).getQuizzActionBar();
		actionBar.setCustomView(R.layout.ab_view_wiki);
		
		mWebView = (WebView) view.findViewById(R.id.wikiWebView);
		mWebView.getSettings().setJavaScriptEnabled(true);
		String wikiLink = getArguments().getString(ARG_WIKI_LINK);
		if (wikiLink != null) {
			mWebView.loadUrl(wikiLink);
			mWebView.setWebViewClient(new WikiWebViewClient());
		}
		
		View customView = actionBar.getCustomViewContainer();
		TextView levelTitle = (TextView) customView.findViewById(R.id.ab_view_wiki_title_name);
		mLoadingProgress = (ProgressBar) customView.findViewById(R.id.ab_view_wiki_progress);
		
		String levelName = getArguments().getString(ARG_LEVEL_NAME);
		if (levelName != null) {
			levelTitle.setText(levelName);
		}
		return view;
	}
	
	class WikiWebViewClient extends WebViewClient {
	    /*
		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        return true;
	    }*/
	    
	    @Override
	    public void onPageStarted(WebView view, String url, Bitmap favicon) {
	    	mLoadingProgress.setVisibility(View.VISIBLE);	    	
	    	super.onPageStarted(view, url, favicon);
	    }
	    
	    @Override
	    public void onPageFinished(WebView view, String url) {
	    	mLoadingProgress.setVisibility(View.GONE);
	    	super.onPageFinished(view, url);
	    }
	}
}
