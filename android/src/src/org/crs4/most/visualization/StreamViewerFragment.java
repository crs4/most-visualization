/*!
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package org.crs4.most.visualization;



 

import org.crs4.most.streaming.IStream;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * This fragment represents a visual container for an {@link IStream}. It can be attached to any Activity, provided that it implements the {@link IStreamFragmentCommandListener} interface.
 * This fragment contains a surface where to render the stream along with two image buttons that you can optionally use for sending play or pause stream requests to the attached activity 
 *
 */
public class StreamViewerFragment extends Fragment {
	 
	 public static final String FRAGMENT_STREAM_ID_KEY = "stream_fragment_stream_id_key";
	 
	 private IStreamFragmentCommandListener cmdListener = null;
	 private SurfaceView surfaceView = null;
	 
	 /**
	  * Intances a new StreamViewerFragment
	  * @param streamId the id of the stream to render
	  * @return a new StreamViewerFragment instance
	  */
	 public static  StreamViewerFragment newInstance(String streamId) {
		 StreamViewerFragment sf = new StreamViewerFragment();

	        Bundle args = new Bundle();
	        args.putString(FRAGMENT_STREAM_ID_KEY, streamId);
	        sf.setArguments(args);

	        return sf;
	    }
	 
	 private String getStreamId()
	 {
		 return getArguments().getString(FRAGMENT_STREAM_ID_KEY);
	 }
	
	 @Override
	 public void onCreate(Bundle savedInstanceState)
	 {
		 super.onCreate(savedInstanceState);
	 }
	 
	@Override 
	public void onActivityCreated(Bundle bundle){
		super.onActivityCreated(bundle);
		StreamViewerFragment.this.cmdListener.onSurfaceViewCreated(getStreamId(),this.surfaceView);
	}
	 
	   
	   @Override
	   /**
	    * @param activity: the activity attached to this fragment: it must implement the  {@link IStreamFragmentCommandListener} interface
	    */
	   public void onAttach(Activity activity) {
		   super.onAttach(activity);
		   this.cmdListener = (IStreamFragmentCommandListener) activity;
	   }
	   
	   
	   
	   @Override
	   public void onDetach()
	   {
		  super.onDetach();
		  this.cmdListener.onSurfaceViewDestroyed(getStreamId());
		  
	   }
	   
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState)
	          {
		        View rootView = inflater.inflate(R.layout.stream_layout, container, false);
		        
		        this.surfaceView = (SurfaceView) rootView.findViewById(R.id.streamSurface);
		        
		        ImageButton butPlay = (ImageButton)  rootView.findViewById(R.id.button_play);
		        butPlay.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		            	StreamViewerFragment.this.cmdListener.onPlay(getStreamId());
	            }
	        });
	        
	        
	        ImageButton butPause = (ImageButton)  rootView.findViewById(R.id.button_pause);
	        butPause.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	StreamViewerFragment.this.cmdListener.onPause(getStreamId());
	            }
	        });
	        
	        return rootView;
	          }
	    
	 
	    
	 /**
	  * Set the player buttons visible or not
	  * @param value <code>true</code> set buttons visible; <code>false</code> invisible.
	  */
	 public void setPlayerButtonsVisible(boolean value)
	 {
		 ImageButton butPlay = (ImageButton)  getView().findViewById(R.id.button_play);
		 ImageButton butPause = (ImageButton)  getView().findViewById(R.id.button_pause);
		 
		 if (value==true)
		 {
			 butPlay.setVisibility(View.VISIBLE);
			 butPause.setVisibility(View.VISIBLE);
			 
		 }
		 else
		 {
			 butPlay.setVisibility(View.INVISIBLE);
			 butPause.setVisibility(View.INVISIBLE);
		 }
		 
	 }
}
