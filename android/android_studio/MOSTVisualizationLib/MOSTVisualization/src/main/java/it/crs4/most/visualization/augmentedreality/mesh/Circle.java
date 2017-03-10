package it.crs4.most.visualization.augmentedreality.mesh;


import android.opengl.GLES10;
import android.opengl.Visibility;

import org.artoolkit.ar.base.rendering.RenderUtils;

import javax.microedition.khronos.opengles.GL10;

public class Circle extends Mesh {
    private int vertexLength = 3; //We only work with position vectors with three elements
    private float width = 5;
    private float [] vertices4Visibility;
    private float radX;
    private float radY;

    static float PI = (float) Math.PI;
    public static float[] MakeCircle2d(float radX, float radY,int points) {
//        float[] vertices = new float[points*2+2];
//        boolean first = true;
//        float fx = 0;
//        float fy = 0;
//        int c = 0;
//        for (int i = 0; i < points; i++) {
//            float fi = 2* (PI)*i/points;
//            float xa = radX * (float) Math.sin(fi + PI) ;
//            float ya = radX * (float) Math.cos(fi + PI);
//            if(first)
//            {
//                first=false;
//                fx=xa;
//                fy=ya;
//            }
//            vertices[c]=xa;
//            vertices[c+1]=ya;
//            c+=2;
//        }
//        vertices[c]=fx;
//        vertices[c+1]=fy;

        float [] vertices = new float[points*3];

        int angleCounter = 0;
        for (int i = 0; i < vertices.length; i += 3) {
            // x value
            float fi = 2* (PI)*angleCounter/points;
            vertices[i]   = radX * (float)Math.sin(fi);
                    // y value
            vertices[i+1] = radY * (float)Math.cos(fi);
            vertices[i+2] = 0;
            angleCounter++;
        }

        return vertices;
    }


    public Circle(float radX, float radY, float width, String id) {
        this(radX, radY, width);
        setId(id);
    }

    public Circle(float radX, float radY, float width) {
        this.radX = radX;
        this.radY = radY;
        this.width = width;

        int points = 16;
        vertices = MakeCircle2d(radX, radY, points);
        verticesBuffer = RenderUtils.buildFloatBuffer(vertices);
        indices = new short[points*3]; // n vertices -1 = n triangles inside circle

        //add a vertex at the center, so we can call visibilityTest assuming the circle is a mesh triangle
        vertices4Visibility = new float[vertices.length + 3];
        for (int i = 0; i < vertices.length; i++) {
            vertices4Visibility[i] = vertices[i];
        }
        vertices4Visibility[vertices4Visibility.length - 3] = 0;
        vertices4Visibility[vertices4Visibility.length - 2] = 0;
        vertices4Visibility[vertices4Visibility.length - 1] = 0;

        short vertexIndex = 0;
        for (int i = 0; i < indices.length; i+=3) {
            indices[i] = (short) (points);
            indices[i + 1] = (short) ((vertexIndex  + 1) %  points);
            indices[i + 2] = (short) (vertexIndex %  points);
            vertexIndex += 1;
        }

    }
//
    public void draw(GL10 gl) {
        gl.glTranslatef(x, y, z);
        gl.glScalef(sx, sy, sz);

        gl.glVertexPointer(vertexLength, GLES10.GL_FLOAT, 0, verticesBuffer);
        gl.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
        gl.glColor4f(1, 0, 0, 1); // Red
//        gl.glColor4f(rgba[0],rgba[1],rgba[2],rgba[3]); // Red
        gl.glLineWidth(this.width);
        gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, vertices.length / 3);
        gl.glDisableClientState(GLES10.GL_VERTEX_ARRAY);

    }

    public int isMeshVisible(float [] projModelViewMatrix) {
        short[] indices = getIndices();
        char[] charIndices = new char[indices.length];

        // method needs char[]
        for (int i = 0; i < indices.length; i++) {
            short shortIndex = indices[i];
            charIndices[i] = (char) shortIndex;
        }
        return Visibility.visibilityTest(projModelViewMatrix, 0, vertices4Visibility, 0, charIndices, 0, indices.length);
    }

    public float getRadX() {
        return radX;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
