package it.crs4.most.visualization.augmentedreality.mesh;

public class Cube extends Mesh {

    private float width;
    private float height;
    private float depth;


    public Cube(){
        this(1f);
    }

    public Cube(float size){
        this(size, size, size);
    }

    public Cube(float size, String id){
        this(size, size, size);
        setId(id);
    }

    public Cube(float width, float height, float depth) {
        this.width = width / 2;
        this.height = height / 2;
        this.depth = depth / 2;

        vertices = new float []{-this.width, -this.height, -this.depth, // 0
            this.width, -this.height, -this.depth, // 1
            this.width, this.height, -this.depth, // 2
            -this.width, this.height, -this.depth, // 3
            -this.width, -this.height, this.depth, // 4
            this.width, -this.height, this.depth, // 5
            this.width, this.height, this.depth, // 6
            -this.width, this.height, this.depth, // 7
        };

        indices = new short[] {0, 4, 5,
            0, 5, 1,
            1, 5, 6,
            1, 6, 2,
            2, 6, 7,
            2, 7, 3,
            3, 7, 4,
            3, 4, 0,
            4, 7, 6,
            4, 6, 5,
            3, 0, 1,
            3, 1, 2,};

        setIndices(indices);
        setVertices(vertices);
        setColor(1f, 1f, 1f, 1f);
        float colors[] = {
            0, 0, 0, 1f,
            1, 0, 0, 1f,
            1, 0, 0, 1f,
            0, 1, 0, 1f,
            0, 0, 1, 1f
        };
        setColors(colors);
    }

    public Cube(float width, float height, float depth, String id) {
        this(width, height, depth);
        setId(id);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getDepth() {
        return depth;
    }

}
