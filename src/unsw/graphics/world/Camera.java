package unsw.graphics.world;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point3D;

public class Camera {
	
	private Point3D pos;
	private CoordFrame3D frame;
	private float rotation = 0;
	private float altitude = 0; 
	private float aspectRatio;
	private float oldX;
	private float oldZ;
	
	public Camera() {
		frame = CoordFrame3D.identity().translate(0, 1, 0); 
		oldX = 0;
		oldZ = 0;
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
		frame = frame.translate(0,t.altitude(getX(), getY()) - t.altitude(oldX, oldZ),0.1f);
		oldX = getX();
		oldZ = getZ();
		System.out.println(frame.getMatrix());
	}
	
	public void forwards(Terrain t) {
		frame = frame.translate(0,t.altitude(getX(), getY()) - t.altitude(oldX, oldZ),-0.1f);
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
	
	public void setView(GL3 gl) {
		Matrix4 mat = Matrix4.rotationY(-this.rotation)
				.multiply(Matrix4.translation(new Point3D(-getX(), -getY(), -getZ())))
				.multiply(Matrix4.scale(1, aspectRatio, 1));
		
		Shader.setViewMatrix(gl, mat);
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