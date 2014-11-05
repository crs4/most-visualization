package org.crs4.most.visualization;

import java.util.List;

import org.crs4.most.streaming.IStream;
import org.crs4.most.streaming.StreamProperties;
import org.crs4.most.streaming.enums.StreamProperty;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;


public class StreamInspectorFragment extends Fragment {
	
	public interface IStreamProvider {
		
		/**
		 * Provide the list of the streams to show in the inspector
		 * @return
		 */
		public List<IStream> getStreams();

		/**
		 * Provide the list of properties to show for each stream (provide null for showing all fields)
		 * @return
		 */
		public List<StreamProperty> getStreamProperties();
	}
	
	 
	 
	private static final String TAG = "StreamInspectorFragment";
	
	private IStreamProvider streamProvider = null;
	private List<IStream> streamsArray = null;
	private ArrayAdapter<IStream> streamsArrayAdapter = null;
	private ListView streamsView = null;
	
	 public static  StreamInspectorFragment newInstance() {
		 StreamInspectorFragment sf = new StreamInspectorFragment();
	     return sf;
	    }
	 
	 @Override
	   public void onAttach(Activity activity) {
		   super.onAttach(activity);
		   this.streamProvider = (IStreamProvider) activity;
	   }
	 
	 	@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState)
	          {
		        View rootView = inflater.inflate(R.layout.istream_listview, container, false);
		        this.streamsView = (ListView) rootView.findViewById(R.id.listStreams);
		        return rootView;
	          }

	 	
	 	@Override 
		public void onActivityCreated(Bundle bundle){
	 		super.onActivityCreated(bundle);
	 		this.setupStreamsListView();
	 	}
	 	
	 public void updateStreamStateInfo(IStream stream)
	    { Log.d(TAG, "Called updateStreamStateInfo on stream");
	    	if (stream==null)
	    	{
	    		Log.e(TAG, "Called updateBuddyStateInfo on NULL stream");
	    		return;
	    	}
	    	
	    	Log.d(TAG, "Called updateStreamStateInfo on stram:" + stream.getName());
	    	
	    	int streamPosition = this.streamsArrayAdapter.getPosition(stream);
	    	if (streamPosition<0)
	    	{
	    		Log.d(TAG, "Adding stream to listView!");
	    		this.streamsArray.add(stream);
	    		
	    	}
	    	else 
	    	{
	    		Log.d(TAG, "Replacing stream into the listView!");
	    		this.streamsArray.set(streamPosition, stream);
	    	}
	    	this.streamsArrayAdapter.notifyDataSetChanged();
	    }
	 
	 private void filterHeaderView(List<StreamProperty> streamProperties, ViewGroup header)
	 {
		 if (streamProperties!=null)
	    	{
	    		if (!streamProperties.contains(StreamProperty.NAME))
	    				{header.getChildAt(0).setVisibility(View.GONE);}
	    		if (!streamProperties.contains(StreamProperty.URI))
						{header.getChildAt(1).setVisibility(View.GONE);}
	    		if (!streamProperties.contains(StreamProperty.VIDEO_SIZE))
					{header.getChildAt(2).setVisibility(View.GONE);}
	    		if (!streamProperties.contains(StreamProperty.LATENCY))
					{header.getChildAt(3).setVisibility(View.GONE);}
	    		if (!streamProperties.contains(StreamProperty.STATE))
					{header.getChildAt(4).setVisibility(View.GONE);}
	    
	    	}
	 }
	 private void setupStreamsListView()
	    {
	    		this.streamsArray = this.streamProvider.getStreams();
	    		List<StreamProperty> streamProperties = this.streamProvider.getStreamProperties();
	    		
	            this.streamsArrayAdapter = new IStreamArrayAdapter(getActivity(), R.layout.istream_row, this.streamsArray, streamProperties);
	           
	            LayoutInflater inflater = getActivity().getLayoutInflater();
	            
	            ViewGroup header = (ViewGroup)inflater.inflate(R.layout.istream_header, streamsView, false);
	            
	            this.filterHeaderView(streamProperties,header);
	            
	            streamsView.addHeaderView(header, null, false);
	            
	            streamsView.setAdapter(this.streamsArrayAdapter);
	            
	            

	            streamsView.setOnItemClickListener(new OnItemClickListener() {

	            	
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.d(TAG, "SELECTED ITEM:" + String.valueOf(position));
						
						    // Create and show the dialog.
						    final IStream selectedStream = streamsArray.get(position-1);
						   
						// custom dialog
						final Dialog dialog = new Dialog(getActivity());
						dialog.setContentView(R.layout.istream_popup_editor);
						
						
						
						dialog.setTitle(selectedStream.getName() + " [" + selectedStream.getState()+"]");
						
						
						final EditText txtUri = (EditText) dialog.findViewById(R.id.editUri);
						final String currentUri =  selectedStream.getProperty(StreamProperty.URI).toString();
						txtUri.setText(currentUri);
						final EditText txtLatency = (EditText) dialog.findViewById(R.id.editLatency);
						final String currentLatency = selectedStream.getProperty(StreamProperty.LATENCY).toString();
						txtLatency.setText(currentLatency);
						
						Button butOk = (Button) dialog.findViewById(R.id.button_ok);
						// if button is clicked, close the custom dialog
						butOk.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								StreamProperties props = new StreamProperties();
								props.add(StreamProperty.URI, txtUri.getText().toString());
								props.add(StreamProperty.LATENCY, txtLatency.getText().toString());
								selectedStream.commitProperties(props);
								
								dialog.dismiss();
							}
						});
						
						Button butCancel = (Button) dialog.findViewById(R.id.button_cancel);
						// if button is clicked, close the custom dialog
						butCancel.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Log.d(TAG, "Dialog operation cancelled");
								dialog.dismiss();
							}
						});
						
						
						dialog.show();
					 
						}// end of onItemClick
	            	} 
	            	 
	            	);
	           
	    }
}
