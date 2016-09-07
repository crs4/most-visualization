package it.crs4.most.visualization.augmentedreality.mesh;


public class Pyramid extends Mesh {
    public Pyramid(float width, float height, float depth) {
        width /= 2;
        depth /= 2;
//        height /= 2;

         vertices = new float []{
            -width, 0, -depth, // 0
            width, 0, -depth, // 1
            width, 0, depth, // 2
            -width, 0, depth, // 3
            0, height, 0, // 4

        };

        short indices[] = {
            0, 3, 2,
            0, 2, 1,
            0, 1, 4,
            1, 2, 4,
            2, 3, 4,
            3, 0, 4

        };

        setIndices(indices);
        setVertices(vertices);
        setColor(1f, 1f, 1f, 1f);
        float colors[] = {
            0, 0, 0, 0.5f,
            1, 0, 0, 0.5f,
            0, 1, 0, 0.5f,
            0, 0, 1, 0.5f,
            0, 0, 0, 0.5f,
            1, 0, 0, 0.5f,
            0, 1, 0, 0.5f,
            0, 0, 1, 0.5f
        };
        setColors(colors);
    }
}
