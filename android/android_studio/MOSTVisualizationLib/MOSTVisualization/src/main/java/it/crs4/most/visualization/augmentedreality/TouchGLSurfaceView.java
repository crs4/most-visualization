package it.crs4.most.visualization.augmentedreality;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import it.crs4.most.visualization.augmentedreality.mesh.Group;
import it.crs4.most.visualization.augmentedreality.mesh.Mesh;
import it.crs4.most.visualization.augmentedreality.mesh.MeshFactory;
import it.crs4.most.visualization.augmentedreality.mesh.MeshManager;
import it.crs4.most.visualization.augmentedreality.renderer.PubSubARRenderer;
import it.crs4.most.visualization.utils.zmq.BaseSubscriber;
import it.crs4.most.visualization.utils.zmq.IPublisher;

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
    private int touchSamplingCounter = 0;
    private BaseSubscriber subscriber;
    private IPublisher publisher;
    private Handler handler;


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
        setRenderMode(RENDERMODE_WHEN_DIRTY);
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
        requestRender();
        if (publisher != null) {
            JSONObject obj = new JSONObject();

            try {
                obj.put("msgType", "visibility");
                obj.put("value", enabled);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            publisher.send(obj.toString());
        }
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

                        requestRender();
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
                            touchSamplingCounter += 1;
                            Log.d(TAG, "Move");
                            float finalDx = mesh.getX() + dx;
                            float finalDy = mesh.getY() - dy;

//                            mesh.setX(finalDx < 1? (finalDx > -1? finalDx: -1): 1);
//                            mesh.setY(finalDy < 1? (finalDy > -1? finalDy: -1): 1);
                            mesh.setX(finalDx, false);
                            mesh.setY(finalDy, false);
                            requestRender();
                            if (touchSamplingCounter % 3 == 0) {
                                mesh.publishCoordinate();
                            }

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

                    touchSamplingCounter = 0;
                    mesh.publishCoordinate();
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

    public BaseSubscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(BaseSubscriber subscriber) {
        this.subscriber = subscriber;
        if (subscriber != null) {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    JSONObject json = (JSONObject) inputMessage.obj;
                    Mesh mesh = null;
                    try {
                        String msgType = (String) json.get("msgType");
                        switch (msgType){
                            case "visibility":
                                boolean enabled = json.getBoolean("value");
                                setEnabled(enabled);
                                if(renderer != null){
                                    ((PubSubARRenderer) renderer).setEnabled(enabled);
                                    requestRender();
                                }

                                break;

//                            case "newObj":
//                                mesh = MeshFactory.createMesh(json);
//                                addMesh(mesh);
//                                break;
                            case "coord":
                                String meshId = json.getString("id");
                                mesh = meshManager.getMeshByID(meshId);
                        }
                        if (mesh != null) {
                            mesh.setCoordinates(
                                    Float.valueOf(json.get("x").toString()),
                                    Float.valueOf(json.get("y").toString()),
                                    Float.valueOf(json.get("z").toString()),
                                    Float.valueOf(json.get("rx").toString()),
                                    Float.valueOf(json.get("ry").toString()),
                                    Float.valueOf(json.get("rz").toString())
                            );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            subscriber.handler = handler;
        }
    }

    public IPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(IPublisher publisher) {
        this.publisher = publisher;
    }
}