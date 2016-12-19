/*!
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package it.crs4.most.visualization;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.crs4.most.streaming.IStream;
import it.crs4.most.streaming.utils.Size;

/**
 * This fragment represents a visual container for an {@link IStream}. It can be attached to any Activity, provided that it implements the {@link IStreamFragmentCommandListener} interface.
 * This fragment contains a surface where to render the stream along with two image buttons that you can optionally use for sending play or pause stream requests to the attached activity
 */
public class StreamViewerFragment extends Fragment implements SurfaceHolder.Callback {

    public static final String FRAGMENT_STREAM_ID_KEY = "stream_fragment_stream_id_key";
    private static final String TAG = "StreamViewerFragment";

    private IStreamFragmentCommandListener cmdListener = null;
    private SurfaceView surfaceView = null;
    private View streamCover = null;
    private TextView txtHiddenSurface = null;
    private boolean playerButtonsVisible = true;
    private ImageButton butPlay;
    private ImageButton butPause;
    private LinearLayout controlButtonLayout;
    private Integer width;
    private Integer height;
    private Object lock = new Object();

    /**
     * Intances a new StreamViewerFragment
     *
     * @param streamId the id of the stream to render
     * @return a new StreamViewerFragment instance
     */
    public static StreamViewerFragment newInstance(String streamId) {
        StreamViewerFragment sf = new StreamViewerFragment();

        Bundle args = new Bundle();
        args.putString(FRAGMENT_STREAM_ID_KEY, streamId);
        sf.setArguments(args);

        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setPlayerButtonsVisible(this.playerButtonsVisible);
        StreamViewerFragment.this.cmdListener.onSurfaceViewCreated(getStreamId(), this.surfaceView);
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
    public void onDetach() {
        super.onDetach();
        this.cmdListener.onSurfaceViewDestroyed(getStreamId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stream_layout, container, false);

        this.surfaceView = (SurfaceView) rootView.findViewById(R.id.stream_surface);
        this.streamCover = rootView.findViewById(R.id.hide_container);
        this.txtHiddenSurface = (TextView) rootView.findViewById(R.id.txt_hidden_surface);

        surfaceView.getHolder().addCallback(this);

        this.butPlay = (ImageButton) rootView.findViewById(R.id.button_play);
        this.butPlay.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                StreamViewerFragment.this.cmdListener.onPlay(getStreamId());
            }
        });

        this.butPause = (ImageButton) rootView.findViewById(R.id.button_pause);
        this.butPause.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                StreamViewerFragment.this.cmdListener.onPause(getStreamId());
            }
        });

        this.controlButtonLayout = (LinearLayout) rootView.findViewById(R.id.control_button_layout);
        this.controlButtonLayout.setVisibility(playerButtonsVisible ? View.VISIBLE : View.INVISIBLE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.controlButtonLayout.getLayoutParams();
        if (playerButtonsVisible) {
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        else {
            layoutParams.height = 0;
        }
        this.controlButtonLayout.setLayoutParams(layoutParams);

        return rootView;
    }

    private String getStreamId() {
        return getArguments().getString(FRAGMENT_STREAM_ID_KEY);
    }

    /**
     * Set the stream visible
     */
    public void setStreamVisible() {
        this.streamCover.setVisibility(View.INVISIBLE);
    }

    /**
     * Set the stream invisible
     *
     * @param message an optional message to show instead of the stream
     */
    public void setStreamInvisible(String message) {
        this.streamCover.setVisibility(View.VISIBLE);
        this.txtHiddenSurface.setText(message);
    }

    /**
     * Set the player buttons visible or not
     *
     * @param value <code>true</code> set buttons visible; <code>false</code> invisible.
     */
    public void setPlayerButtonsVisible(boolean value) {
        this.playerButtonsVisible = value;
        if (getView() != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.controlButtonLayout.getLayoutParams();
            if (value) {
                this.controlButtonLayout.setVisibility(View.VISIBLE);
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            }
            else {
                this.controlButtonLayout.setVisibility(View.INVISIBLE);
                layoutParams.height = 0;
            }
        }
    }



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        synchronized (lock){
            this.height = height;
            this.width = width;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    public Integer getHeight() {
        synchronized (lock){
            return height;
        }
    }

    public Integer getWidth() {
        synchronized (lock){
            return width;
        }
    }
}
