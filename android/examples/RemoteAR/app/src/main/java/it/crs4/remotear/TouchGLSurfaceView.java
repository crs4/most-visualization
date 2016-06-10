package it.crs4.remotear;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import it.crs4.remotear.TouchARRenderer;
import it.crs4.remotear.mesh.Plane;

/**
 * Created by mauro on 24/05/16.
 */
public class TouchGLSurfaceView extends GLSurfaceView {
    private String TAG = "it.crs4.remotear.TouchGLSurfaceView";
    protected final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    protected float mPreviousX;
    protected float mPreviousY;
    protected TouchARRenderer renderer;

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    private boolean editMode = false;

    public TouchARRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(TouchARRenderer renderer) {
        this.renderer = renderer;
        super.setRenderer((Renderer) renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public TouchGLSurfaceView(Context context) {
        super(context);
    }


    public TouchGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!editMode){
                    Log.d(TAG, "ACTION_MOVE");

                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;

                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                        dx = dx * -1 ;
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                        dy = dy * -1 ;
                    }

                    renderer.setAngle(
                            renderer.getAngle() +
                                    ((dx + dy) * TOUCH_SCALE_FACTOR));
                    requestRender();
                }

            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP: x " + x + " y " + y);

                Plane plane = new Plane(30, 30);
                renderer.addMesh(plane, x, y);
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }


}
