package unsw.graphics.world;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Avatar {
	private CoordFrame3D frame;
	private float rotation = 0;
	private float oldAltitude;
	private float currAltitude; 
	private float aspectRatio;
	private Terrain t; 
	private TriangleMesh mesh; 
	private Camera camera;
	private int time; 
	
	public Avatar(Terrain t, TriangleMesh mesh, int time) {
		frame = CoordFrame3D.identity(); 
		oldAltitude = 0;
		currAltitude = 0;
		this.t = t;
		this.mesh = mesh; 
		this.time = time; 
	}
		
	//ALL THE MOVEMENT CODE IS COPIED FROM CAMERA 
	public float getX() {
		return getMatrix().getValues()[12];
	}
	public float getY() {
		return getMatrix().getValues()[13];
	}
	public float getZ() {
		return getMatrix().getValues()[14];
	}
	public Point3D getPosition() {
		return new Point3D(getX(),getY(),getZ());
	}
	public void reshape(int width, int height) {
        aspectRatio = (1f * width) / height;            
    }
	
	public Matrix4 getMatrix() {
		return frame.getMatrix();		
	}
	public CoordFrame3D getFrame() {
		return frame;
	}
	public void turnLeft() {
		frame = frame.rotateY(3);
		rotation+=3;
	}	
	
	/**
	 * has to be called after updateAltitude 
	 * @return
	 */
	private float altitudeChange() {
		float altitudeChange = currAltitude - oldAltitude; 
		return altitudeChange;
	}
	
	private void updateAltitude() {
		oldAltitude = currAltitude; 
		currAltitude = getCurrentAltitude();
	}
	
	private float getCurrentAltitude() {
		return t.altitude(getX(), getZ());
	}
	
	public void turnRight() {
		frame = frame.rotateY(-3);
		rotation-=3;
	}
	public void backwards(Terrain t) {
		updateAltitude();
		System.out.println(getMatrix().getValues()[8] + " " + getMatrix().getValues()[9] +" "+ getMatrix().getValues()[10]);
		frame = frame.translate(0,altitudeChange(),0.1f) ;
	}
	
	public void forwards(Terrain t) {
		updateAltitude();
		frame = frame.translate(0,altitudeChange(),-0.1f);
	}
	//END MOVEMENT CODE 
	/**
	 * 
	 * @return returns direction vector i.e. k vector of coordinate frame 
	 */
	public Point3D getDirection() {
		return(new Point3D(getMatrix().getValues()[8], getMatrix().getValues()[9] ,getMatrix().getValues()[10]));
	}
	
	
//	public Matrix4 setView(GL3 gl) {
//		Matrix4 mat = Matrix4.rotationY(-this.rotation)
//				.multiply(Matrix4.translation(new Point3D(-getX(), -getY(), -getZ())))
//				.multiply(Matrix4.scale(1, 1, 1));
//		return mat;
//	}

	
}
