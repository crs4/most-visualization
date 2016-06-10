package it.crs4.remotear;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.AndroidUtils;
import org.artoolkit.ar.base.NativeInterface;
import org.artoolkit.ar.base.camera.CameraEventListener;
import org.artoolkit.ar.base.camera.CameraPreferencesActivity;
import org.artoolkit.ar.base.camera.CaptureCameraPreview;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.gles20.ARRendererGLES20;

import it.crs4.zmqlib.pubsub.ZMQSubscriber;


public  class LocalARActivity extends Activity implements CameraEventListener {
    protected static final String TAG = "LocalARActivity";
    protected ARRenderer renderer;
    protected FrameLayout mainLayout;
    private CaptureCameraPreview preview;
    private GLSurfaceView glView;
//    private TouchGLSurfaceView glView;
    private boolean firstUpdate = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        PreferenceManager.setDefaultValues(this, org.artoolkit.ar.base.R.xml.preferences, false);
//        this.requestWindowFeature(1);
//        this.getWindow().addFlags(1024);
//        this.getWindow().setFormat(-3);
//        this.getWindow().addFlags(128);
//        this.setRequestedOrientation(0);
//        AndroidUtils.reportDisplayInformation(this);
        setContentView(R.layout.local_ar);
    }

    protected void onStart() {
        super.onStart();
        Log.i("ARActivity", "onStart(): Activity starting.");
        if(!ARToolKit.getInstance().initialiseNative(this.getCacheDir().getAbsolutePath())) {
            (new AlertDialog.Builder(this)).setMessage("The native library is not loaded. The application cannot continue.").setTitle("Error").setCancelable(true).setNeutralButton(17039360, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    LocalARActivity.this.finish();
                }
            }).show();
        } else {
            this.mainLayout = this.supplyFrameLayout();
            if(this.mainLayout == null) {
                Log.e("ARActivity", "onStart(): Error: supplyFrameLayout did not return a layout.");
            } else {
                this.renderer = this.supplyRenderer();
                if(this.renderer == null) {
                    Log.e("ARActivity", "onStart(): Error: supplyRenderer did not return a renderer.");
                    this.renderer = new ARRenderer();
                }

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.preview = new CaptureCameraPreview(this, this);
        Log.i("ARActivity", "onResume(): CaptureCameraPreview created");
        this.glView = new GLSurfaceView(this);
        ActivityManager activityManager = (ActivityManager)this.getSystemService("activity");
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 131072;
        if(supportsEs2) {
            Log.i("ARActivity", "onResume(): OpenGL ES 2.x is supported");
            if(this.renderer instanceof ARRendererGLES20) {
                this.glView.setEGLContextClientVersion(2);
            } else {
                Log.w("ARActivity", "onResume(): OpenGL ES 2.x is supported but only a OpenGL 1.x renderer is available. \n Use ARRendererGLES20 for ES 2.x support. \n Continuing with OpenGL 1.x.");
                this.glView.setEGLContextClientVersion(1);
            }
        } else {
            Log.i("ARActivity", "onResume(): Only OpenGL ES 1.x is supported");
            if(this.renderer instanceof ARRendererGLES20) {
                throw new RuntimeException("Only OpenGL 1.x available but a OpenGL 2.x renderer was provided.");
            }

            this.glView.setEGLContextClientVersion(1);
        }

//        this.glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.glView.getHolder().setFormat(-3);
        this.glView.setRenderer(this.renderer);
        this.glView.setRenderMode(0);
        this.glView.setZOrderMediaOverlay(true);
        Log.i("ARActivity", "onResume(): GLSurfaceView created");
        this.mainLayout.addView(this.preview, new ViewGroup.LayoutParams(-1, -1));
        this.mainLayout.addView(this.glView, new ViewGroup.LayoutParams(-1, -1));
        Log.i("ARActivity", "onResume(): Views added to main layout.");
        if(this.glView != null) {
            this.glView.onResume();
        }

    }

    protected void onPause() {
        super.onPause();
        if(this.glView != null) {
            this.glView.onPause();
        }

        this.mainLayout.removeView(this.glView);
        this.mainLayout.removeView(this.preview);
    }

    public void onStop() {
        Log.i("ARActivity", "onStop(): Activity stopping.");
        super.onStop();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == org.artoolkit.ar.base.R.id.settings) {
            this.startActivity(new Intent(this, CameraPreferencesActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public CaptureCameraPreview getCameraPreview() {
        return this.preview;
    }

    public GLSurfaceView getGLView() {
        return this.glView;
    }

    public void cameraPreviewStarted(int width, int height, int rate, int cameraIndex, boolean cameraIsFrontFacing) {
        if(ARToolKit.getInstance().initialiseAR(width, height, "Data/camera_para.dat", cameraIndex, cameraIsFrontFacing)) {
            Log.i("ARActivity", "getGLView(): Camera initialised");
        } else {
            Log.e("ARActivity", "getGLView(): Error initialising camera. Cannot continue.");
            this.finish();
        }

        Toast.makeText(this, "Camera settings: " + width + "x" + height + "@" + rate + "fps", 0).show();
        this.firstUpdate = true;
    }

    public void cameraPreviewFrame(byte[] frame) {
        if(this.firstUpdate) {
            if(this.renderer.configureARScene()) {
                Log.i("ARActivity", "cameraPreviewFrame(): Scene configured successfully");
            } else {
                Log.e("ARActivity", "cameraPreviewFrame(): Error configuring scene. Cannot continue.");
                this.finish();
            }

            this.firstUpdate = false;
        }

        if(ARToolKit.getInstance().convertAndDetect(frame)) {
            if(this.glView != null) {
                this.glView.requestRender();
            }

            this.onFrameProcessed();
        }

    }

    public void onFrameProcessed() {
    }

    public void cameraPreviewStopped() {
        ARToolKit.getInstance().cleanup();
    }

    protected void showInfo() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("ARToolKit Version: " + NativeInterface.arwGetARToolKitVersion());
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = dialogBuilder.create();
        alert.setTitle("ARToolKit");
        alert.show();
    }


    protected ARRenderer supplyRenderer() {
            String address = "156.148.33.66:5555";
            ZMQSubscriber subscriber = new ZMQSubscriber(address);
            Thread subThread = new Thread(subscriber);
            subThread.start();
            return new TouchARRenderer(this, subscriber);
    }

    protected FrameLayout supplyFrameLayout() {
        return (FrameLayout) this.findViewById(R.id.local_ar_frame);
    }

}
