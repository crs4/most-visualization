package it.crs4.most.visualization.augmentedreality.mesh;



import android.opengl.GLES10;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

/**
 * Fixed version of the Artoolkit one.
 *
 */

public class Line {

    int vertexLength = 3; //We only work with position vectors with three elements
    private float[] start = new float[3];
    private float[] end = new float[3];
    private float width;
    private float[] color = {1, 0, 0, 1};
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;

    /**
     * Should only be used when instantiating a Line using {@link org.artoolkit.ar.base.rendering.gles20.LineGLES20}
     */
    protected Line() {

    }

    /**
     * @param start Vector were the line starts
     * @param end   Vector were the line ends
     * @param width Width of the vector
     */
    public Line(float[] start, float[] end, float width) {
        setStart(start);
        setEnd(end);
        this.width = width;
        setArrays();
    }

    protected void setArrays() {

        float[] vertices = new float[vertexLength * 2];

        for (int i = 0; i < vertexLength; i++) {
            vertices[i] = start[i];
            vertices[i + vertexLength] = end[i];
        }

        mVertexBuffer = RenderUtils.buildFloatBuffer(vertices);
        mColorBuffer = RenderUtils.buildFloatBuffer(color);
    }

    public void draw(GL10 gl) {
        gl.glVertexPointer(vertexLength, GLES10.GL_FLOAT, 0, mVertexBuffer);

        gl.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
//        gl.glColor4f(1, 0, 0, 1); // Red
        gl.glColor4f(color[0],color[1],color[2],color[3]); // Red
        gl.glLineWidth(this.width);
        gl.glDrawArrays(GLES10.GL_LINES, 0, 2);
        gl.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public FloatBuffer getMVertexBuffer() {
        return this.mVertexBuffer;
    }

    public float[] getStart() {
        return start;
    }

    public void setStart(float[] start) {
        if (start.length > vertexLength) {
            this.start[0] = start[0];
            this.start[1] = start[1];
            this.start[2] = start[2];
        } else {
            this.start = start;
        }
    }

    public float[] getEnd() {
        return end;
    }

    public void setEnd(float[] end) {
        if (end.length > vertexLength) {
            this.end[0] = end[0];
            this.end[1] = end[1];
            this.end[2] = end[2];
        } else {
            this.end = end;
        }
    }


    public float[] getColor() {
        return color;
    }

    public void setColor(float[] color) {
        this.color = color;
    }


    public FloatBuffer getmColorBuffer() {
        return mColorBuffer;
    }
}