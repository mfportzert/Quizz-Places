package com.quizz.places.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quizz.core.models.Section;
import com.quizz.core.utils.ConvertUtils;
import com.quizz.core.widgets.SectionProgressView;
import com.quizz.places.R;

public class SectionsItemAdapter extends ArrayAdapter<Section> {

    private int mLineLayout;
    private LayoutInflater mInflater;
    private int[] mProgressDrawables;

    public SectionsItemAdapter(Context context, int lineLayout) {
	super(context, lineLayout);

	mLineLayout = lineLayout;
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

    static class ViewHolder {
	TextView name;
	TextView points;
	TextView levels;
	ImageView buttonEnter;
	SectionProgressView progress;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	ViewHolder holder;
	if (convertView == null) {

	    convertView = this.mInflater.inflate(this.mLineLayout, null);

	    holder = new ViewHolder();
	    holder.name = (TextView) convertView.findViewById(R.id.sectionName);
	    holder.points = (TextView) convertView
		    .findViewById(R.id.sectionNbPoints);
	    holder.levels = (TextView) convertView
		    .findViewById(R.id.sectionNbLevels);
	    holder.buttonEnter = (ImageView) convertView
		    .findViewById(R.id.sectionEnterButton);
	    holder.progress = (SectionProgressView) convertView
		    .findViewById(R.id.sectionProgress);

	    holder.buttonEnter.setAlpha(225);

	    convertView.setTag(holder);

	} else {
	    holder = (ViewHolder) convertView.getTag();
	}

	Section section = getItem(position);
	holder.name.setText(section.name);
	holder.progress.setProgressRes(mProgressDrawables[position
		% mProgressDrawables.length]);
	holder.progress.setProgressValue(34);

	int verticalPadding = (int) ConvertUtils.convertDpToPixels(2.5f,
		getContext());
	int horizontalPadding = (int) ConvertUtils.convertDpToPixels(3f,
		getContext());
	holder.progress.setPaddingProgress(horizontalPadding, verticalPadding,
		horizontalPadding, verticalPadding);

	return convertView;
    }
}
