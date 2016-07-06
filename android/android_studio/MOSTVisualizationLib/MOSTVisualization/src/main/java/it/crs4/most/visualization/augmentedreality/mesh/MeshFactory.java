package it.crs4.most.visualization.augmentedreality.mesh;

import org.json.JSONException;
import org.json.JSONObject;

public class MeshFactory {

    public static class MeshCreationFail extends Exception{
        public MeshCreationFail(String message) {
            super(message);
        }

        public MeshCreationFail(Throwable t){
            super(t);
        }

        public MeshCreationFail(String s, Throwable t){
            super(s, t);
        }
    }

    static public Mesh createMesh(JSONObject json) throws MeshCreationFail{
        Mesh mesh;
        try {

            //FIXME
            mesh =  new Plane(
                    Float.valueOf(json.get("width").toString()),
                    Float.valueOf(json.get("height").toString()),
                    (String) json.get("id"));
        } catch (JSONException e) {
            e.printStackTrace();
            throw  new MeshCreationFail(e.getMessage());
        }

        return mesh;
    }
}
