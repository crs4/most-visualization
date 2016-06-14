package it.crs4.remotear.mesh;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import it.crs4.most.visualization.StreamViewerFragment;

public class Plane extends Mesh {
    float width;
    float height;

	public Plane() {
        this(1, 1, 1, 1);
	}
    public Plane(String id) {
        this(1, 1, 1, 1, id);
	}

	public Plane(float width, float height) {this(width, height, 1, 1);}

    public Plane(float width, float height, String id) {
		this(width, height, 1, 1, id);
	}

	public Plane(float width, float height, int widthSegments, int heightSegments) {
        this(width, height, widthSegments, heightSegments, null);
    }
	public Plane(float width, float height, int widthSegments, int heightSegments, String id) {
        this.width = width;
        this.height = height;

        float[] vertices = new float[(widthSegments + 1) * (heightSegments + 1)
				* 3];
		short[] indices = new short[(widthSegments + 1) * (heightSegments + 1)
				* 6];

		float xOffset = width / -2;
		float yOffset = height / -2;
		float xWidth = width / (widthSegments);
		float yHeight = height / (heightSegments);
		int currentVertex = 0;
		int currentIndex = 0;
		short w = (short) (widthSegments + 1);
		for (int y = 0; y < heightSegments + 1; y++) {
			for (int x = 0; x < widthSegments + 1; x++) {
				vertices[currentVertex] = xOffset + x * xWidth;
				vertices[currentVertex + 1] = yOffset + y * yHeight;
				vertices[currentVertex + 2] = 0;
				currentVertex += 3;

				int n = y * (widthSegments + 1) + x;

				if (y < heightSegments && x < widthSegments) {
					// Face one
					indices[currentIndex] = (short) n;
					indices[currentIndex + 1] = (short) (n + 1);
					indices[currentIndex + 2] = (short) (n + w);
					// Face two
					indices[currentIndex + 3] = (short) (n + 1);
					indices[currentIndex + 4] = (short) (n + 1 + w);
					indices[currentIndex + 5] = (short) (n + 1 + w - 1);

					currentIndex += 6;
				}
			}
		}

		setIndices(indices);
		setVertices(vertices);
        this.id = id != null ? id: UUID.randomUUID().toString();
	}

	@Override
	public String toJson() throws JSONException {
		JSONObject obj = getBaseJsonObj();
        obj.put("width", width);
        obj.put("height", height);
		obj.put("class", "Plane");

		return obj.toString();
	}

	@Override
	public JSONObject getBaseJsonObj() throws JSONException{
		JSONObject base = super.getBaseJsonObj();
		base.put("height", height);
		base.put("width", width);
		return base;
	}

}
