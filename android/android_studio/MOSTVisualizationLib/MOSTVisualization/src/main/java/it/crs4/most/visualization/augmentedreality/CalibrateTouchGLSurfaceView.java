package it.crs4.most.visualization.augmentedreality;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import it.crs4.most.visualization.augmentedreality.renderer.PubSubARRenderer;

public class CalibrateTouchGLSurfaceView extends TouchGLSurfaceView {
    public CalibrateTouchGLSurfaceView (Context context) {
        super(context);
        initScaleDetector(context);
    }

    public CalibrateTouchGLSurfaceView (Context context, AttributeSet attrs) {
        super(context, attrs);
        initScaleDetector(context);
    }

    @Override
    protected void handleUp(){
        super.handleUp();
        float [] extraCalibration = new float[] {mesh.getX(), mesh.getY(), mesh.getZ()};
        ((PubSubARRenderer)renderer).setExtraCalibration(extraCalibration);
        mesh.setX(0, false);
        mesh.setY(0, false);
        mesh.setZ(0, false);
        requestRender();
    }

    @Override
    protected void handleDown(){
        super.handleDown();
        mesh.setX(0, false);
        mesh.setY(0, false);
        mesh.setZ(0, false);
        ((PubSubARRenderer)renderer).setExtraCalibration(new float[3]);

    }
}


