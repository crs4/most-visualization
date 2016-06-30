package it.crs4.remotear;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import it.crs4.remotear.renderer.PubSubARRenderer;
import it.crs4.remotear.mesh.Group;
import it.crs4.remotear.mesh.Mesh;
import it.crs4.remotear.mesh.Plane;

/**
 * Created by mauro on 24/05/16.
 */
public class TouchGLSurfaceView extends GLSurfaceView {
    private String TAG = "TouchGLSurfaceView";
    protected final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    protected float mPreviousX;
    protected float mPreviousY;
    protected PubSubARRenderer renderer;
    private Group meshGroup;
    public enum Mode {Rotate, Edit, Move};
    private boolean mDrawing = false;
    private boolean mMoving = false;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    private Mode mode = Mode.Move;
    private ScaleGestureDetector mScaleDetector;
    private boolean mScaling = false;

    public PubSubARRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(PubSubARRenderer renderer) {
        Log.d(TAG, "inside setRenderer ");
        this.renderer = renderer;
        super.setRenderer((Renderer) renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public TouchGLSurfaceView(Context context) {
        super(context);
        initScaleDetector(context);
    }


    public TouchGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScaleDetector(context);
    }

    private void initScaleDetector(Context context){
        mScaleDetector = new ScaleGestureDetector(context,
            new ScaleGestureDetector.SimpleOnScaleGestureListener(){
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    TouchGLSurfaceView.this.mScaling = true;
                    float currentScaleFactor = detector.getScaleFactor();
                    Log.d(TAG, "currentScaleFactor " + currentScaleFactor);
                    Mesh mesh = renderer.getMesh("arrow");
                    if (currentScaleFactor < 1){
                        mesh.setZ(mesh.getZ() - 5f);
                    }
                    else{
                        mesh.setZ(mesh.getZ() + 5f);
                    }

                    return true;
                }

            });

    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        if (mode == Mode.Move && e.getPointerCount() > 1) {
            mScaleDetector.onTouchEvent(e);
            mMoving = false;
//            mScaling = true;
        }
        else{

            float x = e.getX();
            float y = e.getY();

            if (mScaling){
                mScaling = false;
                mPreviousX = x;
                mPreviousY = y;
                return true;

            }

            float dx = x - mPreviousX;
            float dy = y - mPreviousY;
            Mesh mesh = renderer.getMesh("arrow");
            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "ACTION_MOVE");
                    switch (mode) {
                        case Rotate:
                            Log.d(TAG, "Rotate");
                            // reverse direction of rotation above the mid-line
                            if (y > getHeight() / 2) {
                                dx = dx * -1;
                            }

                            // reverse direction of rotation to left of the mid-line
                            if (x < getWidth() / 2) {
                                dy = dy * -1;
                            }

                            mesh.setRy(mesh.getRy() + (dx + dy) * TOUCH_SCALE_FACTOR);
                            break;
                        case Move:
                            Log.d(TAG, "Move");
                            mesh.setX(mesh.getX() + dx);
                            mesh.setY(mesh.getY() - dy);
                            break;

                        case Edit:
                            draw(x, y);
                            break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "ACTION_UP");
                    if(mode == Mode.Edit){
                        mDrawing = false;
                    }
                    else
                        mMoving = false;
                    break;

                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "ACTION_DOWN");
                    if(mode == Mode.Edit){
                        mDrawing = true;
                    }
                    else
                        mMoving = true;
                    break;


            }
            requestRender();
            mPreviousX = x;
            mPreviousY = y;


        }
        return true;
    }

    private void draw(float x, float y){
        Plane plane = new Plane(30, 30);
        plane.publisher = renderer.publisher;
        renderer.addMesh(plane, x, y);
        plane.publish();

    }
}
