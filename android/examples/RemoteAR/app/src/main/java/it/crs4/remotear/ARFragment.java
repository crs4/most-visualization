package it.crs4.remotear;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

//import org.artoolkit.ar.base.ARToolKit;
//import org.artoolkit.ar.base.rendering.Cube;

import it.crs4.most.visualization.IStreamFragmentCommandListener;
import it.crs4.most.visualization.StreamViewerFragment;


public class ARFragment extends StreamViewerFragment {

    public static final String FRAGMENT_STREAM_ID_KEY = "stream_fragment_stream_id_key";
    private static final String TAG = "StreamViewerFragment";

    private IStreamFragmentCommandListener cmdListener = null;
    private SurfaceView surfaceView = null;
    private View streamCover = null;
    private TextView txtHiddenSurface = null;

    public TouchGLSurfaceView getGlView() {
        return glView;
    }

    private TouchGLSurfaceView glView;

    private boolean playerButtonsVisible = true;

    public static interface OnCompleteListener {
        public abstract void onFragmentCreate();
        public abstract void onFragmentResume();

    }

    private OnCompleteListener mListener;

    public static  ARFragment newInstance(String streamId) {
        ARFragment sf = new ARFragment();

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
        Log.d(TAG,"ON CREATE STREAM VIEWER");
    }

//    @Override
//    public void onActivityCreated(Bundle bundle){
//        super.onActivityCreated(bundle);
//        Log.d(TAG,"ON ACTIVITY_CREATED STREAM VIEWER");
//        setPlayerButtonsVisible(this.playerButtonsVisible);
//        this.cmdListener.onSurfaceViewCreated(getStreamId(),this.surfaceView);
//    }


    @Override
    /**
     * @param activity: the activity attached to this fragment: it must implement the  {@link IStreamFragmentCommandListener} interface
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG,"ON ATTACH STREAM VIEWER");
        this.cmdListener = (IStreamFragmentCommandListener) activity;
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }


    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        Log.d(TAG,"ON DETACH STREAM VIEWER");
        this.cmdListener.onSurfaceViewDestroyed(getStreamId());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG,"ON CREATE_VIEW STREAM VIEWER");
        View rootView = inflater.inflate(R.layout.fragment_ar, container, false);

        surfaceView = (SurfaceView) rootView.findViewById(R.id.streamSurface);
        surfaceView.getHolder().setFixedSize(704, 576); //FIXME should be dynamically set
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
//                ARFragment.this.cmdListener.onSurfaceViewCreated(getStreamId(), surfaceView);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
                Log.d(TAG, String.format("***SurfaceView surfaceChanged format %s, width %s, height %s", format, width, height));
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });




        glView = (TouchGLSurfaceView) rootView.findViewById(R.id.ARSurface);
        glView.getHolder().setFixedSize(704, 576); //FIXME should be dynamically set
        glView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
                Log.d(TAG, String.format("***GLSurfaceView surfaceChanged format %s, width %s, height %s", format, width, height));

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        glView.setEGLContextClientVersion(1);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(-3);

        Log.i(TAG, "onResume(): GLSurfaceView created");
//        this.mainLayout.addView(this.preview, new ViewGroup.LayoutParams(-1, -1));

//
        streamCover =  rootView.findViewById(R.id.hidecontainer);
        txtHiddenSurface = (TextView) rootView.findViewById(R.id.txtHiddenSurface);



        ImageButton butPlay = (ImageButton)  rootView.findViewById(R.id.button_play);
        butPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ARFragment.this.cmdListener.onPlay(getStreamId());
            }
        });


        ImageButton butPause = (ImageButton)  rootView.findViewById(R.id.button_pause);
        butPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ARFragment.this.cmdListener.onPause(getStreamId());
            }
        });

        mListener.onFragmentCreate();
//        setCmdListenerCallback();
        return rootView;
    }


    /**
     * Set the stream visible
     */
    public void setStreamVisible()
    {
        this.streamCover.setVisibility(View.INVISIBLE);
    }


    /**
     * Set the stream invisible
     * @param message an optional message to show instead of the stream
     */
    public void setStreamInvisible(String message)
    {
        this.streamCover.setVisibility(View.VISIBLE);
        this.txtHiddenSurface.setText(message);
    }

    /**
     * Set the player buttons visible or not
     * @param value <code>true</code> set buttons visible; <code>false</code> invisible.
     */
    public void setPlayerButtonsVisible(boolean value)
    {
        this.playerButtonsVisible = value;

        if (getView()==null) return;

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

    @Override
    public void onResume(){
        super.onResume();
        //setCmdListenerCallback();
                surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                ARFragment.this.cmdListener.onSurfaceViewCreated(getStreamId(), surfaceView);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

        mListener.onFragmentResume();

    }




}
