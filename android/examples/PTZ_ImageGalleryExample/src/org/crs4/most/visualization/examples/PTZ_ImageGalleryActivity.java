package org.crs4.most.visualization.examples;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.crs4.most.streaming.IStream;
import org.crs4.most.streaming.StreamingEventBundle;
import org.crs4.most.streaming.StreamingLib;
import org.crs4.most.streaming.StreamingLibBackend;
import org.crs4.most.streaming.enums.PTZ_Direction;
import org.crs4.most.streaming.enums.PTZ_Zoom;
import org.crs4.most.streaming.enums.StreamProperty;
import org.crs4.most.streaming.enums.StreamState;
import org.crs4.most.streaming.enums.StreamingEvent;
import org.crs4.most.streaming.enums.StreamingEventType;
import org.crs4.most.streaming.ptz.PTZ_Manager;
import org.crs4.most.streaming.utils.ImageDownloader;
import org.crs4.most.streaming.utils.ImageDownloader.IBitmapReceiver;
import org.crs4.most.visualization.IStreamFragmentCommandListener;
import org.crs4.most.visualization.PTZ_ControllerFragment;
import org.crs4.most.visualization.StreamInspectorFragment;
import org.crs4.most.visualization.StreamViewerFragment;
import org.crs4.most.visualization.StreamInspectorFragment.IStreamProvider;
import org.crs4.most.visualization.image_gallery.ImageGalleryFragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.support.v7.app.ActionBarActivity;


public class PTZ_ImageGalleryActivity extends ActionBarActivity implements Handler.Callback, 
																		PTZ_ControllerFragment.IPtzCommandReceiver , 
																		IStreamFragmentCommandListener,
																		IStreamProvider
																		
																		{

	private static String TAG = "PTZ_ImageGalleryActivity";
	
	private boolean exitFromAppRequest = false;
	
	private Handler handler;
	private IStream stream1 = null;
	private StreamViewerFragment stream1Fragment = null;
	private StreamInspectorFragment streamInspectorFragment = null;
	
	private PTZ_ControllerFragment ptzControllerFragment = null;
	private PTZ_Manager ptzManager =  null;
	private ImageGalleryFragment imageGalleryFragment = null;

	private String streamingUri;
	private Properties uriProps = null;
	
	private boolean streamingViewOn = true;
	
	
	private int land_gallery_container_id = 0x7f010001;
	private FrameLayout ptzFrameLayout = null;  
	private FrameLayout galleryFrameLayout = null;
	private FrameLayout inspectorFrameLayout = null; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.handler = new Handler(this);
        
        try {
         
        	// Instance and initialize the Streaming Library
        	StreamingLib streamingLib = new StreamingLibBackend();
            
            
            this.ptzControllerFragment = PTZ_ControllerFragment.newInstance(true,true,true);
            
    	  	// First of all, initialize the library 
			streamingLib.initLib(this.getApplicationContext());
			
			// Instance the first stream
	    	HashMap<String,String> stream1_params = new HashMap<String,String>();
	    	stream1_params.put("name", "Stream_1");
	    	
	    	this.uriProps = getUriProperties("uri.properties.default");
       	 	
            this.ptzManager = new PTZ_Manager(this, uriProps.getProperty("uri_ptz") , uriProps.getProperty("username_ptz"), uriProps.getProperty("password_ptz"));
            
            
       	    this.streamingUri =  uriProps.getProperty("uri_stream");  
	    	stream1_params.put("uri", this.streamingUri);
	    	 
	    	this.stream1 = streamingLib.createStream(stream1_params, this.handler);
	    	Log.d(TAG,"STREAM 1 INSTANCE");
	    	// Instance the first StreamViewer fragment where to render the first stream by passing the stream name as its ID.
	    	this.stream1Fragment = StreamViewerFragment.newInstance(stream1.getName());
	    	this.streamInspectorFragment = StreamInspectorFragment.newInstance();
	    	
	    	this.ptzFrameLayout = (FrameLayout) findViewById(R.id.container_ptz_controller);
	    	this.inspectorFrameLayout = (FrameLayout) findViewById(R.id.container_stream_inspector);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	// add the first fragment to the first container
    	FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.add(R.id.container_stream_1, stream1Fragment);
		fragmentTransaction.add(R.id.container_ptz_controller, this.ptzControllerFragment);
		fragmentTransaction.add(R.id.container_stream_inspector, this.streamInspectorFragment);
		fragmentTransaction.commit();
		
    }

    private boolean showGalleryInLandscapeOrientation()
    {
    	if (!(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE))
    		return false;
    	
    	if (galleryFrameLayout==null)
    		{galleryFrameLayout = new FrameLayout(this);
    		galleryFrameLayout.setId(land_gallery_container_id);
    		}
    	
    	     LinearLayout controlsLayout = (LinearLayout)   findViewById(R.id.land_ctl_frames_container);
    	     
    		Log.d(TAG, "ControlLayout is null? " + String.valueOf(controlsLayout==null));
    		controlsLayout.removeAllViews();
    	    controlsLayout.addView(galleryFrameLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1));
    	    
    	   
    	    FragmentTransaction ft = getFragmentManager()
    				.beginTransaction();
    	    ft.replace(land_gallery_container_id, imageGalleryFragment);
    	    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		    ft.addToBackStack(null);
    	    ft.commit();
    	   
    	    
    	    return true;
    }
    
    private boolean showCameraControlsInLandscapeOrientation()
    {
    	if (!(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE))
    		return false;
    	
    	LinearLayout controlsLayout = (LinearLayout)   findViewById(R.id.land_ctl_frames_container);
    	
    	controlsLayout.removeAllViews();
	    controlsLayout.addView(this.ptzFrameLayout, new  FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
    	controlsLayout.addView(this.inspectorFrameLayout, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
    	
    	return true;
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
	

	 MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
	    return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	//check selected menu item
    	if(item.getItemId() == R.id.mnu_exit)
    	{
    		exitFromApp();
    		return true;
    	}
    	else if(item.getItemId() == R.id.stream_mode)
    	{
    		 if (item.isChecked()) item.setChecked(false);
             else item.setChecked(true);
    		 setStreamView();

    	}
    	else if(item.getItemId() == R.id.gallery_mode)
    	{
    		 if (item.isChecked()) item.setChecked(false);
             else item.setChecked(true); 
    		 setGalleryView();

    	}
    	
    	return super.onOptionsItemSelected(item);

    }


	@Override
	public void onPTZstartMove(PTZ_Direction dir) {
		Log.d(TAG, "Called onPTZstartMove for direction:" + dir);
		//Toast.makeText(this, "Start Moving to ->" + dir, Toast.LENGTH_LONG).show();
		this.ptzManager.startMove(dir);
	}


	@Override
	public void onPTZstopMove(PTZ_Direction dir) {
		Log.d(TAG, "Called onPTZstoptMove for direction:" + dir);
		//Toast.makeText(this, "Stop Moving from ->" + dir, Toast.LENGTH_LONG).show();
		this.ptzManager.stopMove();
	}
	
	
	@Override
	public void onPTZstartZoom(PTZ_Zoom dir) {
		this.ptzManager.startZoom(dir);	
	}

	@Override
	public void onPTZstopZoom(PTZ_Zoom dir) {
		this.ptzManager.stopZoom();
	}

	@Override
	public void onGoHome() {
		String homePreset = this.uriProps.getProperty("home_preset_ptz");
		this.ptzManager.goTo(homePreset);
		
	}

	
	private void setStreamView() {
        
		// nothing to do in this case
        if (this.streamingViewOn) return;
        
	    // Instantiate a new fragment.
		//if (this.imageGalleryFragment == null)
	    //this.imageGalleryFragment = new ImageGalleryFragment();

	    // Add the fragment to the activity, pushing this transaction
	    // on to the back stack.
        
        if (!showCameraControlsInLandscapeOrientation())
        {
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    ft.replace(R.id.container_stream_1, this.stream1Fragment);
	    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	    ft.addToBackStack(null);
	    ft.commit();
        }
        
	    this.streamingViewOn = true;
	}
	
	private void setGalleryView() {
		 
        if (!this.streamingViewOn) return;
        
		// Instantiate a new fragment.
		if (this.imageGalleryFragment == null)
		 this.imageGalleryFragment = new ImageGalleryFragment();
      
		if (!showGalleryInLandscapeOrientation())
		{
			 // Add the fragment to the activity, pushing this transaction
		    // on to the back stack.
		    FragmentTransaction ft = getFragmentManager().beginTransaction();
		    ft.replace(R.id.container_stream_1, this.imageGalleryFragment);
		    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		    ft.addToBackStack(null);
		    ft.commit();
		}
	   
	    this.streamingViewOn = false;
	}
	
	@Override
	public void onSnaphot() {
		
		Log.d(TAG, "on snapshot called");
		
		IBitmapReceiver receiver = new IBitmapReceiver() {	
			@Override
			public void onBitmapSaved(ImageDownloader imageDownloader, String filename) {
				Log.d(TAG, "Saved Image:" + filename);
				Toast.makeText(PTZ_ImageGalleryActivity.this, "Image saved:" + filename , Toast.LENGTH_LONG).show();
				imageDownloader.logAppFileNames();
			}
			
			@Override
			public void onBitmapDownloaded(ImageDownloader imageDownloader,Bitmap image) {
				imageDownloader.saveImageToInternalStorage(image, "test_image__" + String.valueOf(System.currentTimeMillis()));
			}

			@Override
			public void onBitmapDownloadingError(
					ImageDownloader imageDownloader, Exception ex) {
					Toast.makeText(PTZ_ImageGalleryActivity.this, "Error downloading Image:" + ex.getMessage(), Toast.LENGTH_LONG).show();
				
			}

			@Override
			public void onBitmapSavingError(ImageDownloader imageDownloader,
					Exception ex) {
				Toast.makeText(PTZ_ImageGalleryActivity.this, "Error saving Image:" + ex.getMessage(), Toast.LENGTH_LONG).show();
				
			}
		};
		ImageDownloader imageDownloader = new ImageDownloader(receiver, this, uriProps.getProperty("username_ptz"), uriProps.getProperty("password_ptz"));
		imageDownloader.downloadImage(uriProps.getProperty("uri_still_image"));
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
	
	 private void exitFromApp() {
			this.exitFromAppRequest = true;
			if (this.stream1!=null  && this.stream1.getState()!= StreamState.DEINITIALIZED)
			{
				this.stream1.destroy();
			}
			else
				this.finish();
		}

	@Override
	public void onPlay(String streamId) {
		this.stream1.play();
	}

	@Override
	public void onPause(String streamId) {
		this.stream1.pause();
		
	}

	@Override
	public void onSurfaceViewCreated(String streamId, SurfaceView surfaceView) {
		Log.d(TAG, "Called onSurfaceViewCreated: preparing native stream...");
		this.stream1.prepare(surfaceView);
	}

	@Override
	public void onSurfaceViewDestroyed(String streamId) {
		Log.d(TAG, "Called onSurfaceViewDestroyed: destroying native stream...");
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
		streamProps.add(StreamProperty.NAME);
		streamProps.add(StreamProperty.STATE);
		return streamProps;
	}

	
}
