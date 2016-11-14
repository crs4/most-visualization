/*!
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014-15, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */


package it.crs4.most.visualization;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import java.util.ArrayList;

import it.crs4.most.streaming.enums.PTZ_Direction;
import it.crs4.most.streaming.enums.PTZ_Zoom;


/**
 * This Factory class provides you an interactive visual panel containing a set of buttons to be used as a GUI frontend for handling remote PTZ webcams.
 * You need to pass a {@link IPtzCommandReceiver} interface to the factory method of this class, because
 * it notifies to this interface all the GUI actions (e.g button clicks)
 * Note that the created window implements the {@link android.view.View.OnTouchListener} interface, so you can move it to the desired position on the screen.
 */
public class PTZ_ControllerPopupWindowFactory implements OnTouchListener {

    private static final String TAG = "PTZ_ControllerPopupWindowFactory";
    private static final String PAN_TILT_PANEL_VISIBILITY = "PAN_TILT_PANEL_VISIBILITY";
    private static final String ZOOM_PANEL_VISIBILITY = "ZOOM_PANEL_VISIBILITY";
    private static final String SNAPSHOT_VISIBILITY = "SNAPSHOT_VISIBILITY";

    // variables for hand
    private float mDx;
    private float mDy;
    private int mCurrentX = 100; // initial xPos
    private int mCurrentY = 100; // initial yPos


    private IPtzCommandReceiver ptzCommandReceiver = null;
    private PopupWindow popupWindow;
    private Context context;


    /**
     * Creates a new floating popupWindow, containing a set of optional panels to be viewed
     *
     * @param context             the context where to render the popup Window
     * @param ptzReceiver         the remote object to use as the target of all user notifications
     * @param panTiltPanelVisible set the pan-tilt panel visible or not
     * @param zoomPanelVisible    set the zoom panel visible or not
     * @param snapShotVisible     set the snapshot button visible or not
     * @param xPos                the initial X position of the popupWindow
     * @param yPos                the initial y position of the popupWindow
     */
    public PTZ_ControllerPopupWindowFactory(Context context, IPtzCommandReceiver ptzReceiver,
                                            boolean panTiltPanelVisible,
                                            boolean zoomPanelVisible,
                                            boolean snapShotVisible,
                                            int xPos,
                                            int yPos) {

        this.context = context;
        this.ptzCommandReceiver = ptzReceiver;
        this.mCurrentX = xPos;
        this.mCurrentY = yPos;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rootView = inflater.inflate(R.layout.ptz_panel, null);

        setupButtonListeners(rootView);

        this.setPanTiltPanelVisible(rootView, panTiltPanelVisible);
        this.setSnaphotVisible(rootView, zoomPanelVisible);
        this.setZoomPanelVisible(rootView, snapShotVisible);


        this.popupWindow = new PopupWindow(rootView,
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

//        this.popupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        this.popupWindow.setTouchable(true);
        this.popupWindow.setFocusable(false);
        this.popupWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mDx = mCurrentX - event.getRawX();
                    mDy = mCurrentY - event.getRawY();
                }
                else if (action == MotionEvent.ACTION_MOVE) {
                    mCurrentX = (int) (event.getRawX() + mDx);
                    mCurrentY = (int) (event.getRawY() + mDy);
                    popupWindow.update(mCurrentX, mCurrentY, -1, -1);
                }
                return false;
            }
        });

    }

    /**
     * @return the created popup Window
     */
    public PopupWindow getPopupWindow() {
        //Toast.makeText(this.context, "Getting PopupWindow" , Toast.LENGTH_LONG).show();
        return this.popupWindow;
    }

    /**
     * Show the PopupWindow at the current location
     */
    public void show() {
        this.getPopupWindow().showAtLocation(this.popupWindow.getContentView(), Gravity.NO_GRAVITY,
            mCurrentX, mCurrentY);
    }

    /**
     * Dismiss the PopupWindow
     */
    public void dismiss() {
        this.getPopupWindow().dismiss();
    }

    /**
     *
     * @return
     */
    public boolean isShowing() {
        return this.getPopupWindow().isShowing();
    }

    private void setupButtonListeners(View rootView) {
        ArrayList<View> ptzButtons = new ArrayList<>();
        rootView.findViewsWithText(ptzButtons, "ptz_button", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        // Toast.makeText(getActivity(), "Found views:" + String.valueOf(ptzButtons.size()), Toast.LENGTH_LONG).show();
        for (View v : ptzButtons) {
            v.setOnTouchListener(this);
        }

        ImageButton butHome = (ImageButton) rootView.findViewById(R.id.but_move_home);
        butHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ptzCommandReceiver.onGoHome();
            }
        });


        ImageButton butSnapshot = (ImageButton) rootView.findViewById(R.id.but_snapshot);
        butSnapshot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ptzCommandReceiver.onSnapshot();
            }
        });
    }


    private void setPanTiltPanelVisible(View rootView, boolean visibile) {
        GridLayout ptPanel = (GridLayout) rootView.findViewById(R.id.pt_buttons_grid);
        if (visibile) {
            ptPanel.setVisibility(View.VISIBLE);
        }
        else {
            ptPanel.setVisibility(View.GONE);
        }
    }

    private void setZoomPanelVisible(View rootView, boolean visibile) {
        ImageButton butZoomIn = (ImageButton) rootView.findViewById(R.id.but_zoom_in);
        ImageButton butZoomOut = (ImageButton) rootView.findViewById(R.id.but_zoom_out);
        if (visibile) {
            butZoomIn.setVisibility(View.VISIBLE);
            butZoomOut.setVisibility(View.VISIBLE);
        }
        else {
            butZoomIn.setVisibility(View.GONE);
            butZoomOut.setVisibility(View.GONE);
        }
    }

    private void setSnaphotVisible(View rootView, boolean visibile) {
        ImageButton butSnap = (ImageButton) rootView.findViewById(R.id.but_snapshot);
        if (visibile) {
            butSnap.setVisibility(View.VISIBLE);
        }
        else {
            butSnap.setVisibility(View.GONE);
        }
    }

    private PTZ_Direction getPTZDirectionByContentDescription(String desc) {
        if (desc.endsWith("_nw")) {
            return PTZ_Direction.UP_LEFT;
        }
        if (desc.endsWith("_n")) {
            return PTZ_Direction.UP;
        }
        if (desc.endsWith("_ne")) {
            return PTZ_Direction.UP_RIGHT;
        }
        if (desc.endsWith("_w")) {
            return PTZ_Direction.LEFT;
        }
        if (desc.endsWith("_e")) {
            return PTZ_Direction.RIGHT;
        }
        if (desc.endsWith("_sw")) {
            return PTZ_Direction.DOWN_LEFT;
        }
        if (desc.endsWith("_s")) {
            return PTZ_Direction.DOWN;
        }
        if (desc.endsWith("_se")) {
            return PTZ_Direction.DOWN_RIGHT;
        }
        else {
            return null;
        }
    }

    private PTZ_Zoom getPTZZoomByContentDescription(String desc) {
        if (desc.endsWith("_plus")) {
            return PTZ_Zoom.IN;
        }
        else if (desc.endsWith("_minus")) {
            return PTZ_Zoom.OUT;
        }
        else {
            return null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == null || v.getContentDescription() == null) {
            return false;
        }

        String ctxDesc = v.getContentDescription().toString();
        PTZ_Direction ptzDirection = getPTZDirectionByContentDescription(ctxDesc);
        PTZ_Zoom ptzZoom = getPTZZoomByContentDescription(ctxDesc);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //Toast.makeText(this.context, "Action Down " + ctxDesc, Toast.LENGTH_LONG).show();
            Log.d(TAG, "Action Down:" + ctxDesc);
            if (ptzDirection != null) {
                this.ptzCommandReceiver.onPTZstartMove(ptzDirection);
            }
            else if (ptzZoom != null) {
                this.ptzCommandReceiver.onPTZstartZoom(ptzZoom);
            }
        }

        else if (event.getAction() == MotionEvent.ACTION_UP) {
            //Toast.makeText(getActivity(), "Action Up" + ctxDesc, Toast.LENGTH_LONG).show();
            Log.d(TAG, "Action Up:" + ctxDesc);
            if (ptzDirection != null) {
                this.ptzCommandReceiver.onPTZstopMove(ptzDirection);
            }
            else if (ptzZoom != null) {
                this.ptzCommandReceiver.onPTZstopZoom(ptzZoom);
            }
        }


        return false;
    }

}
