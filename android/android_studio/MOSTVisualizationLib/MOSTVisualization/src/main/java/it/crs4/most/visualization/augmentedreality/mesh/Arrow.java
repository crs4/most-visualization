package it.crs4.most.visualization.augmentedreality.mesh;


public class Arrow extends Group {
    Pyramid pyramid;
    Cube cube;

    public Arrow(String id) {
        this(id, 1);
    }

    public Arrow(String id, float scale) {
        super(id);
        pyramid = new Pyramid(40f*scale, 20f*scale, 40f*scale);
        cube = new Cube(30f*scale, 20f*scale, 30f*scale);
        pyramid.setRz(180);
//        pyramid.setX(-40f);
        pyramid.setY(pyramid.getHeight());
        cube.setY(pyramid.getHeight() + cube.getHeight()/2);
        add(cube);
        add(pyramid);
    }

    public float getHeight(){
        return pyramid.getHeight() + cube.getHeight();
    }

    public float getWidth(){
        return pyramid.getWidth() + cube.getWidth();
    }
}
