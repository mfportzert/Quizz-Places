package com.quizz.places.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quizz.core.models.Stat;
import com.quizz.core.utils.ConvertUtils;
import com.quizz.core.widgets.SectionProgressView;
import com.quizz.places.R;

public class StatsItemAdapter extends BaseAdapter {

    private List<Stat> mItems;

    private Context mContext;

    private int mLineLayout;
    private LayoutInflater mInflater;
    private int[] mProgressDrawables;

    private static final int TYPE_SIMPLE_ITEM = 0;
    private static final int TYPE_ACHIEVEMENT_ITEM = 1;
    private static final int TYPE_COUNT = 2;

    static class ViewHolder {
	ImageView icon;
	TextView label;
	TextView score;
	// TextView average;
	ImageView trophy;
	SectionProgressView progress;
    }

    public StatsItemAdapter(Context context) {
	mContext = context;
	mInflater = (LayoutInflater) context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	mProgressDrawables = new int[] { R.drawable.fg_section_progress_blue,
		R.drawable.fg_section_progress_green,
		R.drawable.fg_section_progress_orange,
		R.drawable.fg_section_progress_pink,
		R.drawable.fg_section_progress_purple,
		R.drawable.fg_section_progress_yellow,
		R.drawable.fg_section_progress_red };
    }

    public void setItems(List<Stat> items) {
	this.mItems = items;
    }

    @Override
    public int getItemViewType(int position) {
	return getItem(position).isAchievement() ? TYPE_ACHIEVEMENT_ITEM
		: TYPE_SIMPLE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
	return TYPE_COUNT;
    }

    @Override
    public int getCount() {
	return mItems.size();
    }

    @Override
    public Stat getItem(int position) {
	return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
	return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	ViewHolder holder;
	Stat stat = getItem(position);
	int type = getItemViewType(position);

	System.out.println("getView " + position + " " + convertView
		+ " type = " + type);
	if (convertView == null) {
	    holder = new ViewHolder();
	    switch (type) {
	    case TYPE_SIMPLE_ITEM:
		convertView = mInflater
			.inflate(R.layout.item_simple_stat, null);
		holder.label = (TextView) convertView
			.findViewById(R.id.StatLabel);
		holder.score = (TextView) convertView
			.findViewById(R.id.StatDoneOnTotal);
		holder.icon = (ImageView) convertView
			.findViewById(R.id.StatIcon);
		break;
	    case TYPE_ACHIEVEMENT_ITEM:
		convertView = mInflater.inflate(R.layout.item_achievement_stat,
			null);
		holder.label = (TextView) convertView
			.findViewById(R.id.StatLabel);
		holder.score = (TextView) convertView
			.findViewById(R.id.StatDoneOnTotal);
		holder.icon = (ImageView) convertView
			.findViewById(R.id.StatIcon);
		holder.progress = (SectionProgressView) convertView
			.findViewById(R.id.StatProgress);
		holder.trophy = (ImageView) convertView
			.findViewById(R.id.StatCupIcon);
		break;
	    }
	    convertView.setTag(holder);
	} else {
	    holder = (ViewHolder) convertView.getTag();
	}

	if (stat.isAchievement() == true) {
	    holder.icon.setImageDrawable(this.mContext.getResources()
		    .getDrawable(stat.getIcon()));
	    holder.label.setText(String.valueOf(stat.getProgressInPercent())
		    + "% - " + stat.getLabel());
	    // holder.average.setText(String.valueOf(stat.getProgressInPercent())
	    // + "%");
	    holder.score.setText(String.valueOf(stat.getDone()) + " / "
		    + String.valueOf(stat.getTotal()));
	    holder.progress.setProgressRes(mProgressDrawables[position
		    % mProgressDrawables.length]);
	    holder.progress.setProgressValue(stat.getProgressInPercent());
	    int verticalPadding = (int) ConvertUtils.convertDpToPixels(2.5f,
		    this.mContext);
	    int horizontalPadding = (int) ConvertUtils.convertDpToPixels(3f,
		    this.mContext);
	    holder.progress.setPaddingProgress(horizontalPadding,
		    verticalPadding, horizontalPadding, verticalPadding);
	    if (stat.getDone() == stat.getTotal()) {
		holder.trophy.setImageDrawable(this.mContext.getResources()
			.getDrawable(R.drawable.gold_cup));
	    }
	} else {
	    holder.icon.setImageDrawable(this.mContext.getResources()
		    .getDrawable(stat.getIcon()));
	    holder.label.setText(stat.getLabel());
	    holder.score.setText(String.valueOf(stat.getDone()) + " / "
		    + String.valueOf(stat.getTotal()));
	}
	return convertView;
    }

}