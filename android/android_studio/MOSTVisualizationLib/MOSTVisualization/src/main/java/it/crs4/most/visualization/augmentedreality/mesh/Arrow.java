package it.crs4.most.visualization.augmentedreality.mesh;


public class Arrow extends Group {
    Pyramid pyramid;
    Cube cube;
    private static float pyramidScale = 0.7f;
    private static float cubeScale = 1 - pyramidScale;


    public Arrow(float width, float height, float depth) {
        pyramid = new Pyramid(width * pyramidScale, height * pyramidScale, depth * pyramidScale);
        cube = new Cube(width * cubeScale, height * cubeScale, depth * cubeScale);
        cube.setY(cube.getHeight()/2 + pyramid.getHeight());
        add(cube);
        add(pyramid);

    }
    public Arrow(float width, float height, float depth, String id) {
        this(width, height, depth);
        setId(id);
    }

    public Arrow() {
        this(1, 1, 1);
    }

    public Arrow(String id) {
        this();
        setId(id);
    }

    public float getHeight(){
        return pyramid.getHeight() + cube.getHeight();
    }

    public float getWidth(){
        return pyramid.getWidth() + cube.getWidth();
    }
}
