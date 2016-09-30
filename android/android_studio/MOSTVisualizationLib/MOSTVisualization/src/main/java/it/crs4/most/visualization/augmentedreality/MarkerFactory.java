package it.crs4.most.visualization.augmentedreality;

import java.util.HashMap;
import java.util.Map;


public class MarkerFactory {
    protected static Map<String, Marker> markers = new HashMap<>();

    public static class Marker{
        private String cfg;
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
