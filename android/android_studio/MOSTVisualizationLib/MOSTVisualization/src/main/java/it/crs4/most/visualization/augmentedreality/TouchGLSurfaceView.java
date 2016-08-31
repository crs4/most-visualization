package it.crs4.most.visualization.augmentedreality;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.util.HashMap;
import java.util.List;

import it.crs4.most.visualization.augmentedreality.mesh.Group;
import it.crs4.most.visualization.augmentedreality.mesh.Mesh;
import it.crs4.most.visualization.augmentedreality.mesh.MeshManager;

public class TouchGLSurfaceView extends GLSurfaceView {
    protected final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    protected float mPreviousX;
    protected float mPreviousY;
    protected Renderer renderer;
    private String TAG = "TouchGLSurfaceView";
    private Group meshGroup;
    private boolean mDrawing = false;
    private boolean mMoving = false;
    private boolean enabled = true;
    private Mode mode = Mode.Move;
    private ScaleGestureDetector mScaleDetector;
    private boolean mScaling = false;
    private MeshManager meshManager;
    private Mesh mesh;
    private float moveNormFactor = 1;

    public TouchGLSurfaceView(Context context) {
        super(context);
        initScaleDetector(context);
    }

    public TouchGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScaleDetector(context);
    }

    public MeshManager getMeshManager() {
        return meshManager;
    }

    public void setMeshManager(MeshManager meshManager) {
        this.meshManager = meshManager;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
        super.setRenderer(renderer);
    }

    public float getMoveNormFactor() {
        return moveNormFactor;
    }

    public void setMoveNormFactor(float moveNormFactor) {
        this.moveNormFactor = moveNormFactor;
    }


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private void initScaleDetector(Context context) {
        mScaleDetector = new ScaleGestureDetector(context,
            new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    if (!isEnabled()) {
                        return false;
                    }
                    TouchGLSurfaceView.this.mScaling = true;
                    float currentScaleFactor = detector.getScaleFactor();
                    Log.d(TAG, "currentScaleFactor " + currentScaleFactor);

                    if (mesh != null){
                        if (currentScaleFactor < 1) {
                            mesh.setZ(mesh.getZ() - 5f);
                        }
                        else {
                            mesh.setZ(mesh.getZ() + 5f);
                        }

                        return true;
                    }
                    return false;
                }

            });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!isEnabled()) {
            return false;
        }
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();
        mesh = meshManager.getSelectedMesh(x, y);
        if (mesh == null){
            return false;
        }

        if (mode == Mode.Move && e.getPointerCount() > 1) {
            mScaleDetector.onTouchEvent(e);
            mMoving = false;
//            mScaling = true;
        }
        else {
            if (mScaling) {
                mScaling = false;
                mPreviousX = x;
                mPreviousY = y;
                return true;

            }
            float dx = (x - mPreviousX)/moveNormFactor;
            float dy = (y - mPreviousY)/moveNormFactor;
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
                            float finalDx = mesh.getX() + dx;
                            float finalDy = mesh.getY() - dy;

//                            mesh.setX(finalDx < 1? (finalDx > -1? finalDx: -1): 1);
//                            mesh.setY(finalDy < 1? (finalDy > -1? finalDy: -1): 1);
                            mesh.setX(finalDx);
                            mesh.setY(finalDy);
                            break;

//                        case Edit:
//                            draw(x, y);
//                            break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "ACTION_UP");
                    if (mode == Mode.Edit) {
                        mDrawing = false;
                    }
                    else
                        mMoving = false;
                    break;

                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "ACTION_DOWN");
                    if (mode == Mode.Edit) {
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


    public enum Mode {Rotate, Edit, Move}

//    private void draw(float x, float y){
//        Plane plane = new Plane(30, 30);
//        plane.publisher = renderer.publisher;
//        renderer.addMesh(plane, x, y);
//        plane.publish();
//
//    }
}
