package org.crs4.most.visualization.example;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.crs4.most.streaming.IStream;
import org.crs4.most.streaming.StreamProperties;
import org.crs4.most.streaming.StreamingEventBundle;
import org.crs4.most.streaming.StreamingLib;
import org.crs4.most.streaming.StreamingLibBackend;
import org.crs4.most.streaming.enums.StreamProperty;
import org.crs4.most.streaming.enums.StreamState;
import org.crs4.most.streaming.enums.StreamingEvent;
import org.crs4.most.streaming.enums.StreamingEventType;
import org.crs4.most.visualization.IStreamFragmentCommandListener;
import org.crs4.most.visualization.StreamInspectorFragment;
import org.crs4.most.visualization.StreamInspectorFragment.IStreamProvider;
import org.crs4.most.visualization.StreamViewerFragment;

import crs4.most.visualization.example.R;
import android.support.v7.app.ActionBarActivity;
import android.app.FragmentTransaction;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;


/**
 * This activity shows you:
 * <li> how to attach to an activity a StreamViewerFragment for playing a remote Stream </li>
 * <li> how to attach to the activity a StreamInspectorFragment for getting real time information about one or more remote streams and for changing its properties
 * <li> how to switch among three streaming modality: the H264 streaming mode, the motion jpeg mode and the still image mode </li>
 *
 */
public class StillImageExampleActivity extends ActionBarActivity implements Handler.Callback, IStreamFragmentCommandListener , IStreamProvider {
	
	private boolean exitFromAppRequest = false;
	//ID for the menu exit option
    private final int ID_MENU_EXIT = 1;
    private Handler handler;

	private static String TAG="StillImageExample";
	
	private IStream stream1 = null;
	StreamViewerFragment stream1Fragment = null;
	StreamInspectorFragment streamInspectorFragment = null;
	
	private String stillImageUri = null;
	private String streamingUri = null;
	private Timer motionTimer = null;
	
	private enum StreamMode {
		STREAMING,
		STILL_IMAGE,
		MOTION_JPEG
	}
	
	private StreamMode currentMode = StreamMode.STREAMING;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.handler = new Handler(this);
    	
    	
    	// Instance and initialize the Streaming Library
    	
    	StreamingLib streamingLib = new StreamingLibBackend();
    	
  
    	try {
    	  	// First of all, initialize the library 
			streamingLib.initLib(this.getApplicationContext());
			
			// Instance the first stream
	    	HashMap<String,String> stream1_params = new HashMap<String,String>();
	    	stream1_params.put("name", "Stream_1");
	    	
	    	Properties uriProps = getUriProperties("uri.properties.default");
       	 	this.stillImageUri = uriProps.getProperty("uri_still_image"); 
       	    this.streamingUri =  uriProps.getProperty("uri_stream");  
	    	stream1_params.put("uri", this.streamingUri);
	    	 
	    	this.stream1 = streamingLib.createStream(stream1_params, this.handler);
	    	Log.d(TAG,"STREAM 1 INSTANCE");
	    	// Instance the first StreamViewer fragment where to render the first stream by passing the stream name as its ID.
	    	this.stream1Fragment = StreamViewerFragment.newInstance(stream1.getName());
	    	
	    	this.streamInspectorFragment = StreamInspectorFragment.newInstance();
	    	
	    	// Default radio button at the start
	    	 ((RadioButton) findViewById(R.id.radio_stream)).setChecked(true);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	// add the first fragment to the first container
    	FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.add(R.id.container_stream_1,
				stream1Fragment);
		fragmentTransaction.add(R.id.container_stream_inspector, streamInspectorFragment);
		fragmentTransaction.commit();
		
		
        
    }
    
    public void onStillImageButtonClicked(View view) {
    	
    	this.loadStillImage();
    }
    
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_stream:
                if (checked)
                	setStreamingMode();
                break;
            case R.id.radio_still_image:
                if (checked)
                    setStillImageMode();
                break;
            case R.id.radio_motion_jpeg:
                if (checked)
                     setMotionJpegMode();
                break;
        }
    }

    private void setStreamingMode()
    {   
    	// Gui handling
    	Button butLoad = (Button) findViewById(R.id.but_load);
    	butLoad.setEnabled(false);
    	EditText txtFrameRate = (EditText) findViewById(R.id.txt_frame_rate);
    	txtFrameRate.setEnabled(false);
    	this.stream1Fragment.setPlayerButtonsVisible(true);
    	
    	// Stream handling
    	
    	StreamProperties sp = new StreamProperties();
    	sp.add(StreamProperty.URI, this.streamingUri);
    	this.stream1.commitProperties(sp);
    	this.currentMode = StreamMode.STREAMING;
    }
    
    private void setStillImageMode()
    {
    	// Gui Handling
    	Button butLoad = (Button) findViewById(R.id.but_load);
    	butLoad.setEnabled(true);
    	EditText txtFrameRate = (EditText) findViewById(R.id.txt_frame_rate);
    	txtFrameRate.setEnabled(false);
    	this.stream1Fragment.setPlayerButtonsVisible(false);
    	
    	// pause the stream
    	this.stream1.pause();
    	this.currentMode = StreamMode.STILL_IMAGE;
    }
   
    private void setMotionJpegMode()
    {
    	Button butLoad = (Button) findViewById(R.id.but_load);
    	butLoad.setEnabled(false);
    	EditText txtFrameRate = (EditText) findViewById(R.id.txt_frame_rate);
    	txtFrameRate.setEnabled(true);
    	this.stream1Fragment.setPlayerButtonsVisible(true);
    	
    	// pause the stream
    	this.stream1.pause();
    	this.currentMode = StreamMode.MOTION_JPEG;
    }
    
    private void loadStillImage()
    {  
    	Log.d(TAG, "Loading still image passing uri:" + this.stillImageUri);
    	this.stream1.loadStillImage(this.stillImageUri);
    }
    
    /**
     * Use a TimerTask for scheduling a remote image loading at fixed rate 
     * @param framePerMin
     */
    private void playMotionJpeg(int framePerMin)
    {
    	if (this.motionTimer!=null)
			this.motionTimer.cancel();
    	
    	if (framePerMin<=0)
    	{	return;}
    	
    	final long periodInMillis = (long) (60000 / framePerMin);
    	
         this.motionTimer = new Timer("MotionJpegTimer", true);
    	 this.motionTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				loadStillImage();
			}
		}, 0, periodInMillis);
    }
    
    private Properties getUriProperties(String FileName) {
		Properties properties = new Properties();
        try {
               /**
                * getAssets() Return an AssetManager instance for your
                * application's package. AssetManager Provides access to an
                * application's raw asset files;
                */
               AssetManager assetManager = this.getAssets();
               /**
                * Open an asset using ACCESS_STREAMING mode. This
                */
               InputStream inputStream = assetManager.open(FileName);
               /**
                * Loads properties from the specified InputStream,
                */
               properties.load(inputStream);

        } catch (IOException e) {
               // TODO Auto-generated catch block
               Log.e("AssetsPropertyReader",e.toString());
        }
        return properties;

 }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	 	//get the MenuItem reference
	 MenuItem item = 
	    	menu.add(Menu.NONE,ID_MENU_EXIT,Menu.NONE,R.string.mnu_exit);
	 return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	//check selected menu item
    	if(item.getItemId() == ID_MENU_EXIT)
    	{
    		exitFromApp();
    		return true;
    	}
    	return false;
    }
    
    private void exitFromApp() {
		this.exitFromAppRequest = true;
		if (this.stream1!=null)
		{
			this.stream1.destroy();
		}
		else
			this.finish();
	}

	@Override
	public boolean handleMessage(Message streamingMessage) {
		// The bundle containing all available informations and resources about the incoming event
				StreamingEventBundle myEvent = (StreamingEventBundle) streamingMessage.obj;
				
				String infoMsg ="Event Type:" +  myEvent.getEventType() + " ->" +  myEvent.getEvent() + ":" + myEvent.getInfo();
				Log.d(TAG, "handleMessage: Current Event:" + infoMsg);
				
				
				// for simplicity, in this example we only handle events of type STREAM_EVENT
				if (myEvent.getEventType()==StreamingEventType.STREAM_EVENT)
					if (myEvent.getEvent()==StreamingEvent.STREAM_STATE_CHANGED || myEvent.getEvent()== StreamingEvent.STREAM_ERROR)
					{
						if (this.stream1.getState()==StreamState.DEINITIALIZED && this.exitFromAppRequest)
						{
							Log.d(TAG,"Stream deinitialized. Exiting from app.");
							this.finish();
						}
						
					    // All events of type STREAM_EVENT provide a reference to the stream that triggered it.
					    // In this case we are handling two streams, so we need to check what stream triggered the event.
					    // Note that we are only interested to the new state of the stream
						IStream stream  =  (IStream) myEvent.getData();
					   
						// notify the stream inspector about the state chanced for refresh the informations
						this.streamInspectorFragment.updateStreamStateInfo(stream);
					    
					}
				return false;
	}


	@Override
	public void onPlay(String streamId) {
		Log.d(TAG,"Setting stream on play state");
		if (currentMode==StreamMode.STREAMING)
		{   
			this.playMotionJpeg(0);
			this.stream1.play();
		}
		else if (currentMode==StreamMode.MOTION_JPEG)
		{   
			this.stream1.pause();
			String framePerMinutes = ((EditText) findViewById(R.id.txt_frame_rate)).getText().toString();
			playMotionJpeg(Integer.parseInt(framePerMinutes));
		}
	}


	@Override
	public void onPause(String streamId) {
		Log.d(TAG,"Setting stream on pause state");
		this.stream1.pause();
		this.playMotionJpeg(0);
	}


	@Override
	public void onSurfaceViewCreated(String streamId, SurfaceView surfaceView) {
		this.stream1.prepare(surfaceView);
		
	}

	@Override
	public void onSurfaceViewDestroyed(String streamId) {
		this.stream1.destroy();
	}

	@Override
	public List<IStream> getStreams() {
		 List<IStream> streams = new ArrayList<IStream>();
		 streams.add(this.stream1);
		return streams;
	}

	@Override
	public List<StreamProperty> getStreamProperties() {
		ArrayList<StreamProperty> streamProps = new ArrayList<StreamProperty>();
		//streamProps.add(StreamProperty.LATENCY);
		streamProps.add(StreamProperty.NAME);
		streamProps.add(StreamProperty.URI);
		streamProps.add(StreamProperty.STATE);
		return streamProps;
	}
}
