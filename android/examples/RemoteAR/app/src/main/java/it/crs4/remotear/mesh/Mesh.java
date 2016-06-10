package it.crs4.remotear.mesh;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import it.crs4.most.visualization.StreamViewerFragment;
import it.crs4.zmqlib.pubsub.IPublisher;
import it.crs4.zmqlib.pubsub.BaseSubscriber;


import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZMQ;

public class Mesh{
	// Our vertex buffer.
	private FloatBuffer verticesBuffer = null;

	// Our index buffer.
	private ShortBuffer indicesBuffer = null;

	// The number of indices.
	private int numOfIndices = -1;

	// Flat Color
	private float[] rgba = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	// Smooth Colors
	private FloatBuffer colorBuffer = null;

    public IPublisher publisher;
    private int id;
    private String TAG = "MESH";

    public Mesh(){}

    public Mesh(int id){
        this.id = id;
    }

    public float getX() {
        return x;
    }

    protected void publishCoordinate(){
        if (publisher != null){
            JSONObject msg = new JSONObject();
            try {
                msg.put("id", id);
                msg.put("x", x);
                msg.put("y", y);
                msg.put("z", z);
                msg.put("rx", rx);
                msg.put("ry", ry);
                msg.put("rz", rz);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            publisher.send(msg.toString());
        }
    }


    public void setX(float x) {
        setX(x, true);
    }

    public void setX(float x, boolean publish) {
        this.x = x;
        if(publish)
            publishCoordinate();
    }

    public float getY() {
        return y;
    }


    public void setY(float y) {
        setY(y, true);

    }
    public void setY(float y, boolean publish) {
        this.y = y;
        if (publish)
            publishCoordinate();
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        setZ(z, true);
    }

    public void setZ(float z, boolean publish) {
        this.z = z;
        if (publish)
            publishCoordinate();
    }

    public float getRx() {
        return rx;
    }


    public void setRx(float rx) {
        setRx(rx, true);
    }
    public void setRx(float rx, boolean publish) {
        this.rx = rx;
        if (publish)
            publishCoordinate();
    }

    public float getRy() {
        return ry;
    }

    public void setRy(float ry) {
        setRy(ry, true);
    }

    public void setRy(float ry, boolean publish) {
        this.ry = ry;
        if (publish)
            publishCoordinate();
    }

    public float getRz() {
        return rz;
    }

    public void setRz(float rz) {
        setRz(rz, true);
    }

    public void setRz(float rz, boolean publish) {
        this.rz = rz;
        if (publish)
            publishCoordinate();
    }

    public void setCoordinates(float x, float y, float z, float rx, float ry, float rz, boolean publish){
        this.x = x;
        this.y = y;
        this.z = z;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        if (publish)
            publishCoordinate();

    }

    public void setCoordinates(float x, float y, float z, float rx, float ry, float rz){
        setCoordinates(x, y, z, rx, ry, rz, true);

    }
    // Translate params.
	protected float x = 0;

	protected float y = 0;

	protected float z = 0;

	// Rotate params.
	protected float rx = 0;

	protected float ry = 0;

	protected float rz = 0;




	public void draw(GL10 gl) {
		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CCW);
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Specifies the location and data format of an array of vertex
		// coordinates to use when rendering.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
		// Set flat color
		gl.glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
		// Smooth color
		if (colorBuffer != null) {
			// Enable the color array buffer to be used during rendering.
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
		}

		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);

		// Point out the where the color buffer is.
		gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices,
				GL10.GL_UNSIGNED_SHORT, indicesBuffer);
		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        if (colorBuffer != null) {
            // Enable the color array buffer to be used during rendering.
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }
		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE);
	}

	protected void setVertices(float[] vertices) {
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		verticesBuffer = vbb.asFloatBuffer();
		verticesBuffer.put(vertices);
		verticesBuffer.position(0);
	}

	protected void setIndices(short[] indices) {
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indicesBuffer = ibb.asShortBuffer();
		indicesBuffer.put(indices);
		indicesBuffer.position(0);
		numOfIndices = indices.length;
	}

	protected void setColor(float red, float green, float blue, float alpha) {
		// Setting the flat color.
		rgba[0] = red;
		rgba[1] = green;
		rgba[2] = blue;
		rgba[3] = alpha;
	}

	protected void setColors(float[] colors) {
		// float has 4 bytes.
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		colorBuffer = cbb.asFloatBuffer();
		colorBuffer.put(colors);
		colorBuffer.position(0);
	}
}
