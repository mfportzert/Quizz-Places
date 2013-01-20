package com.quizz.places.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quizz.core.models.Stat;
import com.quizz.core.utils.ConvertUtils;
import com.quizz.core.widgets.SectionProgressView;
import com.quizz.places.R;

public class StatsItemAdapter extends ArrayAdapter<Stat> {

	private int mLineLayout;
	private LayoutInflater mInflater;
	private int[] mProgressDrawables;
	
	public StatsItemAdapter(Context context, int lineLayout) {
        super(context, lineLayout);
        
        mLineLayout = lineLayout;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mProgressDrawables = new int[] {
        		R.drawable.fg_section_progress_blue,
        		R.drawable.fg_section_progress_green,
        		R.drawable.fg_section_progress_orange,
        		R.drawable.fg_section_progress_pink,
        		R.drawable.fg_section_progress_purple,
        		R.drawable.fg_section_progress_yellow,
        		R.drawable.fg_section_progress_red
        };
    }

	static class ViewHolder {
        TextView label;
        TextView score;
        TextView average;
        ImageView icon;
        SectionProgressView progress;
    }
	
	@SuppressWarnings("deprecation")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            
        	convertView = this.mInflater.inflate(this.mLineLayout, null);
        	
            holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.StatLabel);
            holder.score = (TextView) convertView.findViewById(R.id.StatDoneOnTotal);
            holder.average = (TextView) convertView.findViewById(R.id.StatPercentScore);
            holder.icon = (ImageView) convertView.findViewById(R.id.StatIcon);
            holder.progress = (SectionProgressView) convertView.findViewById(R.id.StatProgress);
                        
            convertView.setTag(holder);
            
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Stat stat = getItem(position);
       	holder.label.setText(stat.getLabel());
       	holder.average.setText(String.valueOf(stat.getProgressInPercent()) + "%");
       	holder.score.setText(String.valueOf(stat.getDone()) + " / " + String.valueOf(stat.getTotal()));
       	holder.progress.setProgressRes(mProgressDrawables[position % mProgressDrawables.length]);
       	holder.progress.setProgressValue(stat.getProgressInPercent());

       	int verticalPadding = (int) ConvertUtils.convertDpToPixels(2.5f, getContext());
       	int horizontalPadding = (int) ConvertUtils.convertDpToPixels(3f, getContext());
       	holder.progress.setPaddingProgress(horizontalPadding, verticalPadding, 
       			horizontalPadding, verticalPadding);
       	
        return convertView;
    }
}
