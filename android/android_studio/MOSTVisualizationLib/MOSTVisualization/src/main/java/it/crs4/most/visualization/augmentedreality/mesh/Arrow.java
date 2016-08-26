package it.crs4.most.visualization.augmentedreality.mesh;


public class Arrow extends Group {
    public Arrow(String id) {
        super(id);
        Pyramid pyramid = new Pyramid(40f, 20f, 40f);
        Cube cube = new Cube(30f, 20f, 30f);
        pyramid.setRz(180);
//        pyramid.setX(-40f);
        pyramid.setY(-1f * cube.height);
        add(cube);
        add(pyramid);
    }

}
