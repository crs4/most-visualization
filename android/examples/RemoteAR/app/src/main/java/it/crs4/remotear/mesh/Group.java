package it.crs4.remotear.mesh;

import java.util.Vector;
import javax.microedition.khronos.opengles.GL10;

public class Group extends Mesh {
	private Vector<Mesh> children = new Vector<Mesh>();
	
	@Override
	public void draw(GL10 gl) {

		int size = children.size();
		for( int i = 0; i < size; i++){
            gl.glPushMatrix();
			children.get(i).draw(gl);
            gl.glPopMatrix();
	    }
	}

	/**
	 * @param location
	 * @param object
	 * @see java.util.Vector#add(int, java.lang.Object)
	 */
	public void add(int location, Mesh object) {
		children.add(location, object);
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public boolean add(Mesh object) {
		return children.add(object);
	}

	/**
	 * 
	 * @see java.util.Vector#clear()
	 */
	public void clear() {
		children.clear();
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.Vector#get(int)
	 */
	public Mesh get(int location) {
		return children.get(location);
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.Vector#remove(int)
	 */
	public Mesh remove(int location) {
		return children.remove(location);
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#remove(java.lang.Object)
	 */
	public boolean remove(Object object) {
		return children.remove(object);
	}

	/**
	 * @return
	 * @see java.util.Vector#size()
	 */
	public int size() {
		return children.size();
	}


    @Override
	public void setX(float x, boolean publish){
        for(Mesh child: children){
            child.x =  x - this.x + child.x;
        }
        this.x = x;
        if (publish)
            publishCoordinate();
    }

    @Override
    public void setY(float y, boolean publish){
        for(Mesh child: children){
            child.y =  y - this.y + child.y;
        }
        this.y = y;
        if (publish)
            publishCoordinate();
    }

    @Override
    public void setZ(float z, boolean publish){
        for(Mesh child: children){
            child.z =  z - this.z + child.z;
        }
        this.z = z;
        if (publish)
            publishCoordinate();
    }

    @Override
    public void setRx(float rx, boolean publish){
        for(Mesh child: children){
            child.rx =  rx - this.rx + child.rx;
        }
        this.rx = rx;
        if (publish)
            publishCoordinate();
    }

    @Override
    public void setRz(float rz, boolean publish){
        for(Mesh child: children){
            child.rz =  rz - this.rz + child.rz;
        }
        this.rz = rz;
        if (publish)
            publishCoordinate();
    }

    @Override
    public void setRy(float ry, boolean publish){
        for(Mesh child: children){
//            child.ry =  ry - this.ry + child.ry;
            child.ry =  ry;
        }
        this.ry = ry;
        if (publish)
            publishCoordinate();
    }


    @Override
    public void setCoordinates(float x, float y, float z, float rx, float ry, float rz, boolean publish) {
        setX(x, false);
        setY(y, false);
        setZ(z, false);
        setRx(rx, false);
        setRy(ry, false);
        setRz(rz, false);
        if (publish)
            publishCoordinate();
    }
	
}
