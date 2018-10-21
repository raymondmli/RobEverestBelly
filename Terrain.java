package unsw.graphics.world;


import unsw.graphics.world.MathUtil;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {
	
    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;	//is the sunlight vector3 just a point? 
    private ArrayList<Point2D> texCoords; //generated same time as vertices 

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, Vector3 sunlight) {
        this.width = width;
        this.depth = depth;
        altitudes = new float[width][depth];
        trees = new ArrayList<Tree>();
        roads = new ArrayList<Road>();
        this.sunlight = sunlight;
        texCoords = new ArrayList<Point2D>();
    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }
    
    public ArrayList<Point2D> getTexCoords() {
    		return texCoords;
    }

    public Point3D getSunlight() {
        return new Point3D(0,0,0).translate(sunlight);
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        sunlight = new Vector3(dx, dy, dz);      
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return altitudes[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {
        altitudes[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points. Remember 'x' and 'z' are the 'x' and 'y' axes  
     * 
     * @param x
     * @param z
     * @return altitude (y coordinate) 
     */
    public float altitude(float x, float z) {
        if(x > (width - 1) || z > (depth - 1) || x < 0 || z < 0) 
    			return 0;
        float altitudeA = altitudes[(int) x][(int) z];	//altitude of floored coordinate

        
        if(x == (int) x && z == (int) z)									//doesn't check for infinite case 
        		return altitudeA;
        //testing if point is above a line formed by x,z and x+1,z+1. If yes, point is above line. 
        //x - ((int) x + 1) - z + ((int) z + 1) >= 0
        //(x - (int) x) + (z - (int) z + 1) >= 0
        if(x - ((int) x + 1) - z + ((int) z + 1) >= 0) {	
        	
        		return MathUtil.biLerp(
        						(int)x + 1,(int)z+1,altitudes[(int) x + 1][(int) z + 1]
	        					, (int)x,(int)z ,altitudes[(int) x][(int) z ]
	        					, (int)x + 1,(int)z+1,altitudes[(int) x + 1][(int) z + 1]
	        					, (int)x + 1,(int)z,altitudes[(int) x + 1][(int) z]
	        					, x, z);
        		
        }
        return MathUtil.biLerp((int)x,(int)z,altitudeA
	        					, (int)x ,(int)z + 1, altitudes[(int) x][(int) z + 1]
	        					, (int)x,(int)z,altitudeA
	        					, (int)x + 1, (int)z + 1, altitudes[(int) x + 1][(int) z + 1]
	        					, x, z);
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(float x, float z) {
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }
    
    public List<Tree> getTrees() {
    		return trees;
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);
        roads.add(road);        
    }
    
    
    /**
     * version that supports triangle mesh
     * @return
     */
    public List<Point3D> generateVertices() {
		ArrayList <Point3D> vertices = new ArrayList<Point3D>();
		for (int z = 0; z < depth; z++) {
			for(int x = 0; x < width; x++) {
				vertices.add(new Point3D((float) x,  (float) getGridAltitude(x,z), (float) z));
				texCoords.add(new Point2D((float) x, (float) z));
			}
		}
		return vertices;
    }
    
    /**
     * Version that supports triangle mesh
     * loads indices of the faces into the index buffer and returns it. 
     */
    public List<Integer> generateIndices() {
    	//	System.out.println("width: " + width + "depth: " + depth + "\n");
    		int numIndices = (width - 1) * (depth - 1) * 2 * 3; 
    		Integer[] indices = new Integer[numIndices];
    		int index = 0;
    		for(int z = 0; z < (depth - 1); z++) {
    			//needs width - 1 for no overlapping purposes 
    			for (int x = 0; x < (width - 1); x++) {
    				int vertexIndex = z * width + x;
    				//top triangle
    				indices[index++] = vertexIndex;
    				indices[index++] = vertexIndex + width;
    				indices[index++] = vertexIndex + width + 1;
    				//bottom triangle
    				indices[index++] = vertexIndex; 
    				indices[index++] = vertexIndex + width +1;
    				indices[index++] = vertexIndex + 1;
    			}
    		}
    		return Arrays.asList(indices);
    }
    
    public Integer[] generateIndicesA() {
    	//	System.out.println("width: " + width + "depth: " + depth + "\n");
    		int numIndices = (width - 1) * (depth - 1) * 2 * 3; 
    		Integer[] indices = new Integer[numIndices];
    		int index = 0;
    		for(int z = 0; z < (depth - 1); z++) {
    			//needs width - 1 for no overlapping purposes 
    			for (int x = 0; x < (width - 1); x++) {
    				int vertexIndex = z * width + x;
    				//top triangle
    				indices[index++] = vertexIndex;
    				indices[index++] = vertexIndex + width;
    				indices[index++] = vertexIndex + width + 1;
    				//bottom triangle
    				indices[index++] = vertexIndex; 
    				indices[index++] = vertexIndex + width +1;
    				indices[index++] = vertexIndex + 1;
    			}
    		}
    		return indices;
    }
    
    public List<Vector3> generateNormals(List<Point3D>vertices, Integer[]indices) {
 		List<Vector3> normals = new ArrayList<Vector3>();
 		int numTriangles = (width ) * (depth ) * 2;
 		for(int i = 0; i < numTriangles; i++) {
 			normals.add(i, new Vector3(0,1,0)); //fills in the list. 
 		}
 		for(int i = 0; i < numTriangles; i++) {
// 			int a = indices[i];
// 			int b = indices[i + 1];
// 			int c = indices[i+2];
 			Point3D v0 = vertices.get(indices[i]);
 			Point3D v1 = vertices.get(indices[i + 1]);
 			Point3D v2 = vertices.get(indices[i + 2]);
 			
 			Vector3 normal = normal(v0,v1,v2);
 			normals.set(indices[i], normal);
 			normals.set(indices[i + 1], normal);
 			normals.set(indices[i + 2], normal);
 		}
 		for(Vector3 v: normals) {
 			v.normalize();
 		}
 		return normals; 
    }
    
    private Vector3 normal (Point3D p1, Point3D p2, Point3D p3) {
 		Vector3 a = p2.minus(p1);
 		Vector3 b = p3.minus(p1);
 		return a.cross(b).normalize();
    }
   
}
