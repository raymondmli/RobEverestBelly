package unsw.graphics.world;

import unsw.graphics.world.Terrain;
import unsw.graphics.world.MathUtil;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.Vector4;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;
import java.util.List;

import unsw.graphics.geometry.Point2D;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {

    private List<Point2D> points;
    private float width;
    private float altitude;
    private final float HEIGHT = 0.1f;
    private final int SEGMENTS = 64;
    private Terrain t; 
    
    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(float width, List<Point2D> spine) {
        this.width = width;
        this.points = spine;
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return width;
    }
        
    public double getAltitude() {
    		return altitude;
    }
    
    public void setAltitude(float altitude) {
    		this.altitude = altitude;
    }
    
    public void setTerrain(Terrain t) {
    		this.t = t;
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return points.size() / 6;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public Point2D controlPoint(int i) {
        return points.get(i);
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public Point2D point(float t) {
    	
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 3;
        
        Point2D p0 = points.get(i++);
        Point2D p1 = points.get(i++);
        Point2D p2 = points.get(i++);
        Point2D p3 = points.get(i++);
        

        float x = b(0, t) * p0.getX() + b(1, t) * p1.getX() + b(2, t) * p2.getX() + b(3, t) * p3.getX();
        float z = b(0, t) * p0.getY() + b(1, t) * p1.getY() + b(2, t) * p2.getY() + b(3, t) * p3.getY();        
        
        return new Point2D(x, z);
    }
    
    /**
     * @precondition terrain is set 
     * @return first element of list is triangle mesh for front face. last is for back face. intermediate is mesh for sides
     */
    public ArrayList<TriangleMesh> makeExtrusion() {
        Point2D p0 = points.get(0);
        System.out.println("x: " + p0.getX() + " y: " + p0.getY());
        setAltitude(t.altitude(p0.getX(), p0.getY()));
    		//front face is a rectangle with centre point of bottom edge being origin and height 0.1 (it is facing the positive z direction)
        float fz = p0.getY();
        Point3D left = new Point3D(- width/2,0, 0);
        Point3D right = new Point3D(+ width/2, 0, 0);
        List<Point3D> shape = Arrays.asList(left, right);
        
        // The extruded flat surface
        // Remember size refers to number of cubic curves in the spine and segments refers to # of individual segments that approximate curve
        ArrayList<Point3D> shapeVert = new ArrayList<Point3D>(); //contains all the surface vertices
        for(int i = 0; i < SEGMENTS * (size()+1); i++) {
        		float param = i * 1.0f/SEGMENTS;
        		System.out.println("param: " + param + " i: " + i);
        		//calculating all the vertices 
	        for (Point3D p : shape)
	            shapeVert.add(frenet(param).multiply(p.asHomogenous()).asPoint3D());
        }
		int numIndices = (SEGMENTS - 1) * (size() + 1) * 2 * 3; //# triangles * 3 
		Integer[] indices = new Integer[numIndices];
		int index = 0;
		for(int z = 0; z < (SEGMENTS - 1) * (size() + 1); z++) {
			int vertexIndex = z * 2;
//				//top triangle
			indices[index++] = vertexIndex;
			indices[index++] = vertexIndex + 3;
			indices[index++] = vertexIndex + 2;
//				//bottom triangle
			indices[index++] = vertexIndex; 
			indices[index++] = vertexIndex + 1;
			indices[index++] = vertexIndex + 3;	
		}

        TriangleMesh sidesMesh = new TriangleMesh(shapeVert,Arrays.asList(indices), true);
        ArrayList<TriangleMesh> meshes = new ArrayList<TriangleMesh>();
        meshes.add(sidesMesh);
                
    		return meshes;
    }
    
    /**
     * Calculate the frenet frame at a particular t value 
     * remembering that the > 1 part of t value (referred to as s) represents the s -1 'th segment/curve on the spine 
     */
    public Matrix4 frenet(float t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 3;
        
        Point2D p0 = points.get(i++);	//p0 refers to the 'origin' of the particular segment/curve
        Point2D p1 = points.get(i++);
        Point2D p2 = points.get(i++);
        Point2D p3 = points.get(i++);

        //calculating the k/z axis 
        float dx = db(0, t) * p0.getX() + db(1, t) * p1.getX() + db(2, t) * p2.getX() + db(3, t) * p3.getX();
        float dy = 0;
        float dz = db(0, t) * p0.getY() + db(1, t) * p1.getY() + db(2, t) * p2.getY() + db(3, t) * p3.getY();   
        Vector3 k = new Vector3(dx,dy,dz).normalize();
        //finding i axis
        float ix = - k.getZ();
        float iy = 0;
        float iz = k.getX();
        Vector3 iaxis = new Vector3(ix,iy,iz).normalize();
        //finding j axis
        Vector3 j = k.cross(iaxis).normalize();
        Vector3 jFlip = new Vector3(-j.getX(), -j.getY(), -j.getZ()).normalize();
        
        float[] values = new float[] {
                iaxis.getX(), iaxis.getY(), iaxis.getZ(), 0, // i
                jFlip.getX(), jFlip.getY(), jFlip.getZ(), 0, // j
                k.getX(), k.getY(), k.getZ(), 0, // k
                point(t).getX(), altitude, point(t).getY(), 1  // phi
            };
        System.out.println(t);
        System.out.println(new Matrix4(values));
        return new Matrix4(values);
    }
    
    /**
     * Calculate the differentiated Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private float db(int i, float t) {
        
        switch(i) {
        
        case 0:
            return -3 * (1-t) * (1-t);

        case 1:
            return 3 * (t-1) * (3*t-1);
            
        case 2:
            return 3 * t * (2 - 3 * t);

        case 3:
            return 3 * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException(" d " + i);
    }

    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private float b(int i, float t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }


}
