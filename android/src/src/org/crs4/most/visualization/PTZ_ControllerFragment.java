package org.crs4.most.visualization;

import java.util.ArrayList;
 

import org.crs4.most.streaming.enums.PTZ_Direction;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Toast;


public class PTZ_ControllerFragment extends Fragment implements OnTouchListener{

	private static String TAG="PTZ_ControllerFragment";
	
	public interface IPtzCommandReceiver {
		
		public void onPTZstartMove(PTZ_Direction dir);
		public void onPTZstopMove(PTZ_Direction dir);
	}
	
	private IPtzCommandReceiver ptzCommandReceiver = null;
	
	/**
	 * Provides a new istance of this fragment
	 * @return the PTZ_ControllerFragment instance
	 */
	 public static  PTZ_ControllerFragment newInstance() {
		 PTZ_ControllerFragment ptz = new PTZ_ControllerFragment();
	     return ptz;
	    }

	 @Override
	  public void onAttach(Activity activity) {
		   super.onAttach(activity);
		   this.ptzCommandReceiver = (IPtzCommandReceiver) activity;
	   }
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
          {
	        View rootView = inflater.inflate(R.layout.ptz_panel, container, false);
	       // this.streamsView = (ListView) rootView.findViewById(R.id.listStreams);
	      
	        setupButtonListeners(rootView);
	        return rootView;
          }

	private void setupButtonListeners(View rootView) {
		
		 ArrayList<View> ptzButtons = new ArrayList<View>();
	     rootView.findViewsWithText(ptzButtons, "ptz_button" , View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
	    // Toast.makeText(getActivity(), "Found views:" + String.valueOf(ptzButtons.size()), Toast.LENGTH_LONG).show();
	     for (View v : ptzButtons)
		           v.setOnTouchListener(this);
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
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		String ctxDesc =  v.getContentDescription().toString();
		PTZ_Direction ptzDirection = getPTZDirectionByContentDescription(ctxDesc);
		if (event.getAction()==MotionEvent.ACTION_DOWN)
		{
			//Toast.makeText(getActivity(), "Action Down " + ctxDesc, Toast.LENGTH_LONG).show();
			Log.d(TAG, "Action Down:" + ctxDesc);
			if (ptzDirection!=null)
				this.ptzCommandReceiver.onPTZstartMove(ptzDirection);
		}
		
		else if (event.getAction()==MotionEvent.ACTION_UP)
		{
			//Toast.makeText(getActivity(), "Action Up" + ctxDesc, Toast.LENGTH_LONG).show();
			Log.d(TAG, "Action Up:" + ctxDesc);
			if (ptzDirection!=null)
				this.ptzCommandReceiver.onPTZstopMove(ptzDirection);
		}
		
		
		return false;
	}

}
