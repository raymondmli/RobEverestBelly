package unsw.graphics.world;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.geometry.Point3D;

public class Camera {
	
	private Point3D pos;
	private CoordFrame3D frame;
	
	public Camera() {
		frame = CoordFrame3D.identity(); 
	}
	
	public void turnRight() {
		frame = frame.rotateY(3);
		System.out.println(frame.getMatrix());

	}	
	public void turnLeft() {
		frame = frame.rotateY(-3);
		System.out.println(frame.getMatrix());
	}
	public void forwards(Terrain t) {
		frame = frame.translate(0,t.altitude(getX(), getZ()),0.1f);
		//frame = frame.translate(0,0f,0.1f);
		System.out.println(frame.getMatrix());

	}
	public void backwards(Terrain t) {
		frame = frame.translate(0,t.altitude(getX(), getZ()),-0.1f);
		//frame = frame.translate(0,0f,-0.1f);
		System.out.println(frame.getMatrix());

	}
	public float getX() {
		return getMatrix().getValues()[12];
	}
	public float getZ() {
		return getMatrix().getValues()[14];
	}
	
	public Matrix4 getMatrix() {
		return frame.getMatrix();		
	}
	public CoordFrame3D getFrame() {
		return frame;
	}

}
