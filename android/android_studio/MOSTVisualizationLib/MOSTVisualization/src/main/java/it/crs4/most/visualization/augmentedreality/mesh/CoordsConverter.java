package it.crs4.most.visualization.augmentedreality.mesh;


public class CoordsConverter {

    float xFactor, yFactor, zFactor = 1;

    public CoordsConverter(){

    }

    public CoordsConverter(float xFactor, float yFactor, float zFactor){
        this.xFactor = xFactor;
        this.yFactor = yFactor;
        this.zFactor = zFactor;
    }

    public float [] convert(float x, float y, float z){
        float [] result = {x*xFactor, y*yFactor, z*zFactor};
        return result;
    }

    public float getxFactor() {
        return xFactor;
    }

    public void setxFactor(float xFactor) {
        this.xFactor = xFactor;
    }

    public float getyFactor() {
        return yFactor;
    }

    public void setyFactor(float yFactor) {
        this.yFactor = yFactor;
    }

    public float getzFactor() {
        return zFactor;
    }

    public void setzFactor(float zFactor) {
        this.zFactor = zFactor;
    }

}
