package com.quizz.places.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quizz.core.models.Section;
import com.quizz.places.R;

public class SectionsItemAdapter extends ArrayAdapter<Section> {

	private int mLineLayout;
	private LayoutInflater mInflater;
	
	public SectionsItemAdapter(Context context, int lineLayout) {
        super(context, lineLayout);
        
        this.mLineLayout = lineLayout;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	static class ViewHolder {
		
        TextView name;
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		
        ViewHolder holder;
        if (convertView == null) {
            
        	convertView = this.mInflater.inflate(this.mLineLayout, null);
        	
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.sectionName);
            
            convertView.setTag(holder);
            
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Section section = getItem(position);
       	holder.name.setText(section.name);
       	
        return convertView;
    }
}
