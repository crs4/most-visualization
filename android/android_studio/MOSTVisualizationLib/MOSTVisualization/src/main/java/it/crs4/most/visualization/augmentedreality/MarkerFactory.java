package it.crs4.most.visualization.augmentedreality;

import java.util.HashMap;
import java.util.Map;


public class MarkerFactory {
    protected static Map<String, Marker> markers = new HashMap<>();

    public static class Marker{
        private String cfg;
        private String group;
        private int artoolkitID = -1;
        private float [] modelMatrix = new float[] {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        };

        private Marker(String cfg){
            this.cfg = cfg;
        }

        public String toString() {
            return cfg;
        }

        public float[] getModelMatrix() {
            return modelMatrix;
        }

        public void setModelMatrix(float[] modelMatrix) {
            this.modelMatrix = modelMatrix;
        }

        public int getArtoolkitID() {
            return artoolkitID;
        }

        public void setArtoolkitID(int artoolkitID) {
            this.artoolkitID = artoolkitID;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }
    }

    public static Marker getMarker(String cfg){
        Marker marker;
        if (markers.containsKey(cfg)){
            marker = markers.get(cfg);
        }
        else {
            marker = new Marker(cfg);
            markers.put(cfg, marker);
        }
        return marker;
    }

}
