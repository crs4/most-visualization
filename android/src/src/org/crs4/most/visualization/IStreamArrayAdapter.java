/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */


package org.crs4.most.visualization;

import java.util.List;

import org.crs4.most.streaming.IStream;
import org.crs4.most.streaming.enums.StreamProperty;
import org.crs4.most.streaming.enums.StreamState;



import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class IStreamArrayAdapter extends ArrayAdapter<IStream> {
   
	private List<StreamProperty> streamProperties = null;
	
	/**
	 * This adapter provides a way of rendering informations about a list of {@link IStream} objects.
	 * @param context
	 * @param viewId the view id where to render the informations about each stream
	 * @param objects the list of {@link IStream} objects.
	 * @param streamProperties the properties to render for each stream (a null value renders all the available properties)
	 */
    public IStreamArrayAdapter(Context context, int viewId,
                 List<IStream> objects, List<StreamProperty> streamProperties) {
        super(context, viewId, objects);
        this.streamProperties = streamProperties;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewOptimize(position, convertView, parent);
    }

    private View getViewOptimize(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.istream_row, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.textName);
            viewHolder.uri = (TextView)convertView.findViewById(R.id.textUri);
            viewHolder.videoSize = (TextView) convertView.findViewById(R.id.textSize);
            viewHolder.latency = (TextView) convertView.findViewById(R.id.textLatency);
            viewHolder.status = (TextView)convertView.findViewById(R.id.textState);
            
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        IStream myStream = getItem(position);
        this.filterViewColumns(myStream, viewHolder);
        
        return convertView;
    }
    
    private void filterViewColumns(IStream myStream, ViewHolder viewHolder)
    {
    	if (this.streamProperties!=null)
    	{
    		if (!this.streamProperties.contains(StreamProperty.NAME))
    				{ viewHolder.name.setVisibility(View.GONE);}
    		else
    		{
    			viewHolder.name.setText(myStream.getName());
    		};
    		
    		
    		if (!this.streamProperties.contains(StreamProperty.URI))
					{ viewHolder.uri.setVisibility(View.GONE);}
    		else{
    			viewHolder.uri.setText(myStream.getProperty(StreamProperty.URI).toString());
    		};
    		
    		if (!this.streamProperties.contains(StreamProperty.VIDEO_SIZE))
			{ viewHolder.videoSize.setVisibility(View.GONE);}
    		else
    		{
    			if (myStream.getProperty(StreamProperty.VIDEO_SIZE)!=null)
    	        	viewHolder.videoSize.setText(myStream.getProperty(StreamProperty.VIDEO_SIZE).toString());
    	        else
    	        	viewHolder.videoSize.setText("n.a");
    		};
    		
    		if (!this.streamProperties.contains(StreamProperty.LATENCY))
			{ viewHolder.latency.setVisibility(View.GONE);}
    		else
    		{
    			viewHolder.latency.setText("" + myStream.getProperty(StreamProperty.LATENCY)+ " ms");
    		};
    		
    		if (!this.streamProperties.contains(StreamProperty.STATE))
			{ viewHolder.videoSize.setVisibility(View.GONE);}
    		else 
    		{
    			 viewHolder.status.setText(myStream.getState().toString());
    			 if (myStream.getState()==StreamState.ERROR)
    		        	viewHolder.status.setBackgroundColor(Color.RED);
    		        else if (myStream.getState()==StreamState.PLAYING_REQUEST)
    		        	viewHolder.status.setBackgroundColor(0xFFFFA500); // ORANGE COLOR
    		        else
    		        	viewHolder.status.setBackgroundColor(Color.GREEN);
    		};
    	}
    }
    
    private class ViewHolder {
        public TextView name;
        public TextView uri;
        public TextView videoSize;
        public TextView latency;
        public TextView status;
        
    }
}