package it.crs4.most.visualization.augmentedreality.mesh;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.UUID;

import javax.microedition.khronos.opengles.GL10;

import it.crs4.most.visualization.utils.zmq.IPublisher;


public abstract class Mesh {
    public IPublisher publisher;
    protected String id;
    // Translate params.
    protected float x = 0;
    protected float y = 0;
    protected float z = 0;
    // Rotate params.
    protected float rx = 0;
    protected float ry = 0;
    protected float rz = 0;
    // Our vertex buffer.
    private FloatBuffer verticesBuffer = null;
    // Our index buffer.
    private ShortBuffer indicesBuffer = null;
    // The number of indices.
    private int numOfIndices = -1;
    // Flat Color
    private float[] rgba = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    // Smooth Colors
    private FloatBuffer colorBuffer = null;
    private String TAG = "MESH";
    private String marker;
    private CoordsConverter coordsConverter;
    private float [] xLimits;
    private float [] yLimits;
    private float [] zLimits;

    public Mesh() {
        setId(null);
    }


    public Mesh(String id) {
        setId(id);
    }

    public String getId() {
        return id;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    protected void setId(String id) {
        this.id = id != null ? id : UUID.randomUUID().toString();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        setX(x, true);
    }


    protected JSONObject getBaseJsonObj() throws JSONException {
        float [] coords;
        CoordsConverter converter;
        if (coordsConverter == null){
            coords = new float [] {x, y, z};
        }
        else{
            coords = coordsConverter.convert(x, y, z);
        }

        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("x", coords[0]);
        obj.put("y", coords[1]);
        obj.put("z", coords[2]);
        obj.put("rx", rx);
        obj.put("ry", ry);
        obj.put("rz", rz);
        return obj;
    }

    protected void publishCoordinate() {
        if (publisher != null) {
            try {
                JSONObject base = getBaseJsonObj();
                base.put("msgType", "coord");
                publisher.send(base.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private float getCoord(float coord, float [] coordLimits){
        if (coordLimits == null || (coord >= coordLimits[0] && coord <= coordLimits[1])){
            return coord;
        }
        if (coord < coordLimits[0]){
            return coordLimits[0];
        }
        return coordLimits[1];
    }

    public void setX(float x, boolean publish) {
        this.x = getCoord(x, xLimits);
        if (publish)
            publishCoordinate();
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        setY(y, true);

    }

    public void setY(float y, boolean publish) {
        this.y = getCoord(y, yLimits);
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
        this.z = getCoord(z, zLimits);
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

    public void setCoordinates(float x, float y, float z, float rx, float ry, float rz, boolean publish) {
        setX(x, false);
        setY(y, false);
        setZ(z, false);
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        if (publish)
            publishCoordinate();

    }

    public void setCoordinates(float x, float y, float z, float rx, float ry, float rz) {
        setCoordinates(x, y, z, rx, ry, rz, true);

    }

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

    public String toJson() throws JSONException {
        return getBaseJsonObj().toString();
    }

    public void publish() {

        if (publisher != null) {
            try {
                JSONObject base = getBaseJsonObj();
                base.put("msgType", "newObj");
                publisher.send(base.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public CoordsConverter getCoordsConverter() {
        return coordsConverter;
    }

    public void setCoordsConverter(CoordsConverter coordsConverter) {
        this.coordsConverter = coordsConverter;
    }


    public float[] getzLimits() {
        return zLimits;
    }

    public void setzLimits(float lowerLimit, float upperLimit) {
        this.zLimits = new float []{lowerLimit, upperLimit};
    }

    public float[] getyLimits() {
        return yLimits;
    }

    public void setyLimits(float lowerLimit, float upperLimit) {
        this.yLimits = new float []{lowerLimit, upperLimit};
    }

    public float[] getxLimits() {
        return xLimits;
    }

    public void setxLimits(float lowerLimit, float upperLimit) {
        this.xLimits = new float []{lowerLimit, upperLimit};
    }

}
