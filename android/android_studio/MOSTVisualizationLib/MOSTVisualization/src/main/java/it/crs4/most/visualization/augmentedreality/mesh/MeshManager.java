package it.crs4.most.visualization.augmentedreality.mesh;

import org.artoolkit.ar.base.ARToolKit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class MeshManager {
    private HashMap<String, Mesh> meshes = new HashMap<>();
    private HashMap<Integer, String> markersID = new HashMap<>();
    private HashMap<Integer, List<Mesh>> markerToMeshes = new HashMap<>();

    public void addMesh(Mesh mesh){
        meshes.put(mesh.getId(), mesh);
    }

    public boolean addMarkersToScene(){
        HashSet<String> markersAdded = new HashSet<>();
        int markerID;
        for (Mesh mesh : meshes.values()) {
            String marker = mesh.getMarker();
            if (!markersAdded.contains(marker)) {
                markerID = ARToolKit.getInstance().addMarker(marker);
                if (markerID < 0){
                    return false;
                }
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
        return true;
    }

    public HashMap<float [], List<Mesh>> getVisibleMeshes(){

        HashMap<float [], List<Mesh>> result = new HashMap<>();
        for (int markerID : markersID.keySet()) {
            if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
                float [] modelView = ARToolKit.getInstance().queryMarkerTransformation(markerID);
                result.put(modelView, markerToMeshes.get(markerID));
                }
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
}
