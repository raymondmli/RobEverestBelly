package unsw.graphics.world;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Camera {
	
	private CoordFrame3D frame;
	private float rotation = 0;
	private float oldAltitude;
	private float currAltitude; 
	private float aspectRatio;
	private float oldX;
	private float oldZ;
	private Terrain t; 
	
	public Camera(Terrain t) {
		frame = CoordFrame3D.identity().translate(0, 1.5f, 0); 
		oldX = 0;
		oldZ = 0;
		oldAltitude = 0;
		currAltitude = 0;
		this.t = t;
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
	
	public void turnLeft() {
		frame = frame.rotateY(3);
		rotation+=3;
		System.out.println(frame.getMatrix());

	}	
	public void turnRight() {
		frame = frame.rotateY(-3);
		rotation-=3;
		System.out.println(frame.getMatrix());
	}
	public void backwards(Terrain t) {
		updateAltitude();
		frame = frame.translate(0,altitudeChange(),0.1f) ;
		oldX = getX();
		oldZ = getZ();
		System.out.println(frame.getMatrix());
	}
	
	public void forwards(Terrain t) {
		updateAltitude();
		frame = frame.translate(0,altitudeChange(),-0.1f);
		oldX = getX();
		oldZ = getZ();
		System.out.println(frame.getMatrix());
	}
	public float getX() {
		return getMatrix().getValues()[12];
	}
	public float getY() {
		return getMatrix().getValues()[13];
	}
	public float getZ() {
		return getMatrix().getValues()[14];
	}
	
	public Matrix4 setView(GL3 gl) {
		Matrix4 mat = Matrix4.rotationY(-this.rotation)
				.multiply(Matrix4.translation(new Point3D(-getX(), -getY(), -getZ())))
				.multiply(Matrix4.scale(1, 1, 1));
		return mat;
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

}