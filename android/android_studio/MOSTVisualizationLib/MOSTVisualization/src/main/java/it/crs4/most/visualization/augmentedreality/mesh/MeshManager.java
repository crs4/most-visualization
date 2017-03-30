package it.crs4.most.visualization.augmentedreality.mesh;

import android.opengl.Matrix;
import android.util.Log;

import org.artoolkit.ar.base.ARToolKit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.crs4.most.visualization.augmentedreality.MarkerFactory.Marker;


public class MeshManager {
    private HashMap<String, Mesh> meshes = new HashMap<>();
    private HashMap<Integer, Marker> markersID = new HashMap<>();
    private HashMap<Integer, List<Mesh>> markerToMeshes = new HashMap<>();
    private static int MARKERLESS_ID = -1000;
    private HashSet<Marker> markersAdded = new HashSet<>();
    private final static String TAG = "MESHMANAGER";
    private boolean sceneConfigured = false;

    public void addMesh(Mesh mesh){
        meshes.put(mesh.getId(), mesh);
    }

    private boolean addMarker(Mesh mesh, Marker marker){
        int markerID;
        if (!markersAdded.contains(marker)) {
            if (marker != null) {
                markerID = ARToolKit.getInstance().addMarker(marker.toString());
                if (markerID < 0){
                    return false;
                }
                marker.setArtoolkitID(markerID);
            }
            else {
                markerID = MARKERLESS_ID;
            }

            markersAdded.add(marker);
            markersID.put(markerID, marker);
        }
        else {
            markerID = marker != null? marker.getArtoolkitID() : MARKERLESS_ID;
        }
        List<Mesh> meshes;
        if (!markerToMeshes.containsKey(markerID)) {
            meshes = new ArrayList<Mesh>();
            markerToMeshes.put(markerID, meshes);
        } else {
            meshes = markerToMeshes.get(markerID);
        }
        meshes.add(mesh);
        return true;
    }

    public boolean configureScene(){
        return configureScene(false);

    }

    private void reset() {
        markersAdded = new HashSet<>();
        markersID = new HashMap<>();
        markerToMeshes = new HashMap<>();
    }

    public boolean configureScene(boolean force){
        if (sceneConfigured && !force)
            return true;

        if (force)
            reset();

        int markerID;
        for (Mesh mesh : meshes.values()) {
            List<Marker> markers = mesh.getMarkers();
            if (markers.size() == 0){
                addMarker(mesh, null);
                continue;
            }

            for (Marker marker: markers){
                if (!markersAdded.contains(marker)) {
                    if(marker == null){
                        continue;
                    }

                    markerID = ARToolKit.getInstance().addMarker(marker.toString());
                    if (markerID < 0){
                        return false;
                    }
                    marker.setArtoolkitID(markerID);
                    markersAdded.add(marker);
                    markersID.put(markerID, marker);

                    List<Mesh> meshes;
                    if (!markerToMeshes.containsKey(markerID)) {
                        meshes = new ArrayList<Mesh>();
                        markerToMeshes.put(markerID, meshes);
                    } else {
                        meshes = markerToMeshes.get(markerID);
                    }
                    meshes.add(mesh);
                }
            }

        }
        sceneConfigured = true;
        return true;
    }

    public HashMap<float [], List<Mesh>> getVisibleMeshes(){
        float [] modelView;
        HashMap<float [], List<Mesh>> result = new HashMap<>();

        for (int markerID : markersID.keySet()) {
            if (markerID == MARKERLESS_ID){
                modelView = new float[16];
                Matrix.setIdentityM(modelView, 0);

            }
            else if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
                modelView = ARToolKit.getInstance().queryMarkerTransformation(markerID);

                }
            else{
                continue;
            }
            result.put(modelView, markerToMeshes.get(markerID));
        }
        return result;
    }
    public Mesh getSelectedMesh(float winX, float winY){
        // FIXME
//        Mesh [] meshes = (Mesh []) getVisibleMeshes().values().toArray();
        HashMap<float [], List<Mesh>> tmp = getVisibleMeshes();
        for(Map.Entry<float [], List<Mesh>> entry: tmp.entrySet()){
            return tmp.get(entry.getKey()).get(0);
        }
        return null;
    }

    public Mesh getMeshByID(String id){
        return meshes.get(id);
    }

    public List<Mesh> getMeshes(){
        return new ArrayList<>(meshes.values());
    }

    public List <Mesh> getMeshesByGroup(String group) {
        List <Mesh> result = new ArrayList<>();

        for (Mesh mesh: getMeshes()){
            for (Marker marker: mesh.getMarkers()) {
                if (marker.getGroup().equals(group)) {
                    result.add(mesh);
                    break;
                }
            }
        }
//        for (HashMap.Entry<Integer, Marker> entry: markersID.entrySet()) {
//            String markerGroup = entry.getValue().getGroup();
//            if (markerGroup != null && markerGroup.equals(group)) {
//                result.addAll(markerToMeshes.get(entry.getKey()));
//            }
//        }
//        if (result.size() == 0) {
//            Log.w(TAG, String.format("no mesh found for group %s, be sure it matches db side", group));
//        }
        return result;
    }


    public Set<Integer> getMarkersID(){
        return markersID.keySet();
    }

    public List<Marker> getVisibleMarkers() {
        List<Marker> result = new ArrayList<>();

        for (HashMap.Entry<Integer, Marker> entry: markersID.entrySet()) {
            if (ARToolKit.getInstance().queryMarkerVisible(entry.getKey())) {
                result.add(entry.getValue());
            }
        }


        return result;
    }
}
