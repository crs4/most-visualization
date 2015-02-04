package org.crs4.most.visualization;

import java.util.ArrayList;
 

 

import org.crs4.most.streaming.enums.PTZ_Direction;
import org.crs4.most.streaming.enums.PTZ_Zoom;


import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;


/**
 * This fragment provides you a set of visual panels and buttons to be used as a GUI frontend for handling remote PTZ webcams.
 * This fragment expects the attached activity implements the  {@link IPtzCommandReceiver} interface, because
 * it notifies to this interface all the GUI actions (e.g button clicks)
 */
public class PTZ_ControllerPopupWindowFactory  implements OnTouchListener{

	private static final String TAG="PTZ_ControllerPopupWindowFactory";
	private static final String PAN_TILT_PANEL_VISIBILITY = "PAN_TILT_PANEL_VISIBILITY";
	private static final String ZOOM_PANEL_VISIBILITY = "ZOOM_PANEL_VISIBILITY";
	private static final String SNAPSHOT_VISIBILITY = "SNAPSHOT_VISIBILITY";
	
	/**
	 * An activity must implement this interface to be able to receive notifications from the attached PTZ_ControllerFragment
	 *  
	 */
	public interface IPtzCommandReceiver {
		
		/**
		 * Called when the user presses one button of the pan-tilt panel
		 * @param dir the required moving direction 
		 */
		public void onPTZstartMove(PTZ_Direction dir);
		
		/**
		 * Called when the user releases one button of the pan-tilt panel
		 * @param the moving direction before this stop command
		 */
		public void onPTZstopMove(PTZ_Direction dir);
		
		/**
		 * Called when the user presses one button of the zoom panel
		 * @param dir the required zooming direction 
		 */
		public void onPTZstartZoom(PTZ_Zoom dir);
		
		/**
		 * Called when the user releases one button of the zoom panel
		 * @param the zooming direction before this stop command
		 */
		public void onPTZstopZoom(PTZ_Zoom dir);
		
		/**
		 * Called when the user clicks on the home button of the pan-tilt panel
		 */
		public void onGoHome();
		
		/**
		 * Called when the user clicks on the snapshot button
		 */
		public void onSnaphot();
		
	}
	
	private IPtzCommandReceiver ptzCommandReceiver = null;
	private PopupWindow popupWindow;
	private Context context;
	
	
	/**
	 * Provides a new instance of this fragment, with a selection of desired panels
	 * @param panTiltPanelVisible set the pan-tilt panel visible or not
	 * @param zoomPanelVisible set the zoom panel visible or not
	 * @param snapShotVisible set the snapshot button visible or not
	 * @return
	 */
	public  PTZ_ControllerPopupWindowFactory(Context context, IPtzCommandReceiver ptzReceiver, boolean panTiltPanelVisible, boolean zoomPanelVisible, boolean snapShotVisible) {
		
		this.context = context;
		this.ptzCommandReceiver = ptzReceiver;
		 
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		 View rootView = inflater.inflate(R.layout.ptz_panel, null);
	      
	        setupButtonListeners(rootView);
	        
	          this.setPanTiltPanelVisible(rootView ,panTiltPanelVisible);
	          this.setSnaphotVisible(rootView ,zoomPanelVisible);
	          this.setZoomPanelVisible(rootView , snapShotVisible);
	          
		
		 
		 this.popupWindow = new PopupWindow(rootView,
	                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		 
		 this.popupWindow.setBackgroundDrawable (new BitmapDrawable());
		
	    
	    }
	 
	 
    public PopupWindow getPopupWindow()
    {
    	Toast.makeText(this.context, "Getting PopupWindow" , Toast.LENGTH_LONG).show();
    	return this.popupWindow;
    }
	 

	private void setupButtonListeners(View rootView) {
		
		 ArrayList<View> ptzButtons = new ArrayList<View>();
	     rootView.findViewsWithText(ptzButtons, "ptz_button" , View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
	    // Toast.makeText(getActivity(), "Found views:" + String.valueOf(ptzButtons.size()), Toast.LENGTH_LONG).show();
	     for (View v : ptzButtons)
		           v.setOnTouchListener(this);
	     
	     ImageButton butHome = (ImageButton) rootView.findViewById(R.id.but_move_home);
	     butHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ptzCommandReceiver.onGoHome();
			}});
	     
	     
	     ImageButton butSnapshot = (ImageButton) rootView.findViewById(R.id.but_snapshot);
	     butSnapshot.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ptzCommandReceiver.onSnaphot();
				}});
	     }
	
	
	private void setPanTiltPanelVisible(View rootView,  boolean visibile)
	{
		GridLayout ptPanel = (GridLayout) rootView.findViewById(R.id.pt_buttons_grid);
		if (visibile)
			ptPanel.setVisibility(View.VISIBLE);
		else
			ptPanel.setVisibility(View.GONE);
	}
	
	private void setZoomPanelVisible(View rootView, boolean visibile)
	{
		ImageButton  butZoomIn = (ImageButton) rootView.findViewById(R.id.but_zoom_in);
		ImageButton  butZoomOut = (ImageButton) rootView.findViewById(R.id.but_zoom_out);
		if (visibile)
		{
			butZoomIn.setVisibility(View.VISIBLE);
			butZoomOut.setVisibility(View.VISIBLE);
		}
		else
		{
			butZoomIn.setVisibility(View.GONE);
			butZoomOut.setVisibility(View.GONE);
		}
	}
	
	private void setSnaphotVisible(View rootView,  boolean visibile)
	{
		ImageButton  butSnap = (ImageButton) rootView.findViewById(R.id.but_snapshot);
		if (visibile)
			butSnap.setVisibility(View.VISIBLE);
		else
			butSnap.setVisibility(View.GONE);
	}
	
	private PTZ_Direction getPTZDirectionByContentDescription(String desc)
	{
		if (desc.endsWith("_nw")) return PTZ_Direction.UP_LEFT;
		if (desc.endsWith("_n")) return PTZ_Direction.UP;
		if (desc.endsWith("_ne")) return PTZ_Direction.UP_RIGHT;
		
		if (desc.endsWith("_w")) return PTZ_Direction.LEFT;
		if (desc.endsWith("_e")) return PTZ_Direction.RIGHT;
		
		if (desc.endsWith("_sw")) return PTZ_Direction.DOWN_LEFT;
		if (desc.endsWith("_s")) return PTZ_Direction.DOWN;
		if (desc.endsWith("_se")) return PTZ_Direction.DOWN_RIGHT;
		
		else return null;
	}
	
	private PTZ_Zoom getPTZZoomByContentDescription(String desc)
	{
		if (desc.endsWith("_plus")) return PTZ_Zoom.IN;
		else if  (desc.endsWith("_minus")) return PTZ_Zoom.OUT;
		else return null;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v==null || v.getContentDescription()==null)
			return false;
		
		String ctxDesc =  v.getContentDescription().toString();
		PTZ_Direction ptzDirection = getPTZDirectionByContentDescription(ctxDesc);
		PTZ_Zoom ptzZoom = getPTZZoomByContentDescription(ctxDesc);
		
		if (event.getAction()==MotionEvent.ACTION_DOWN)
		{
			Toast.makeText(this.context, "Action Down " + ctxDesc, Toast.LENGTH_LONG).show();
			Log.d(TAG, "Action Down:" + ctxDesc);
			if (ptzDirection!=null)
				this.ptzCommandReceiver.onPTZstartMove(ptzDirection);
			else if (ptzZoom!=null)
				this.ptzCommandReceiver.onPTZstartZoom(ptzZoom);
		}
		
		else if (event.getAction()==MotionEvent.ACTION_UP)
		{
			//Toast.makeText(getActivity(), "Action Up" + ctxDesc, Toast.LENGTH_LONG).show();
			Log.d(TAG, "Action Up:" + ctxDesc);
			if (ptzDirection!=null)
				this.ptzCommandReceiver.onPTZstopMove(ptzDirection);
			else if (ptzZoom!=null)
				this.ptzCommandReceiver.onPTZstopZoom(ptzZoom);
		}
		
		
		return false;
	}

}
