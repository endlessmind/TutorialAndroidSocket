package com.example.tutorialandroidsocket.Adapters;


import java.util.ArrayList;

import com.example.tutorialandroidsocket.R;
import com.example.tutorialandroidsocket.Utils;
import com.example.tutorialandroidsocket.Socket.Classes.FileData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter {

	private LayoutInflater inflator = null;
	private ArrayList<FileData> files;

	 class ViewHolder {
		 public TextView tvFileName;
		 public TextView tvFileSize;
	}
	 
	public FileAdapter(Context context, int resource, ArrayList<FileData> s) {
		this.files = s;
		inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            
	}
	 
	
	@Override
	public int getCount() {
		return files.size();
	}

	@Override
	public Object getItem(int position) {
		return files.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		
    	if(convertView == null){
    		int layout =  R.layout.file_item;
    		convertView = inflator.inflate(layout, null);

    		holder = new ViewHolder();
    		holder.tvFileName = (TextView) convertView.findViewById(R.id.tvFileName);
    		holder.tvFileSize = (TextView) convertView.findViewById(R.id.tvFileSize);

    		convertView.setTag(holder);
    		
    	} else {
    		holder = (ViewHolder) convertView.getTag();
    		
    	}
    	
    	FileData fd = files.get(position);
    	
    	holder.tvFileName.setText(fd.fileName);
    	if (fd.Type == 1)
    		holder.tvFileSize.setText(Utils.FormatFileSize(fd.Size));
    	else if (fd.Type == 2)
    		holder.tvFileSize.setText("Folder");
    	else if (fd.Type == -1)
    		holder.tvFileSize.setText("...");
		
		
		return convertView;
	}

}
