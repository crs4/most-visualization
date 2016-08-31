package it.crs4.most.visualization.augmentedreality.mesh;


public class Arrow extends Group {
    public Arrow(String id) {
        this(id, 1);
    }

    public Arrow(String id, float scale) {
        super(id);
        Pyramid pyramid = new Pyramid(40f*scale, 20f*scale, 40f*scale);
        Cube cube = new Cube(30f*scale, 20f*scale, 30f*scale);
        pyramid.setRz(180);
//        pyramid.setX(-40f);
        pyramid.setY(-1f * cube.height);
        add(cube);
        add(pyramid);
    }
}
