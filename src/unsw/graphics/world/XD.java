package unsw.graphics.world;

import java.awt.Color;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;

public class XD {
//  private Point3DBuffer vertexBuffer;
//  private IntBuffer indicesBuffer;
//  private Point3DBuffer normalBuffer; 
//  private Integer[] indices; 
//  private ArrayList<Point3D> vertices; 
	
//	System.out.println(terrain.altitude(6, 3));
//	System.out.println(terrain.altitude(6, 4));
//	System.out.println(terrain.altitude(5, 3));
//
//	System.out.println(terrain.altitude(5.8f, 3.1f));
//	System.out.println(terrain.altitude(6.0f, 3.1f));
//	System.out.println(terrain.altitude(5.0f, 3.0f));
//	System.out.println(terrain.altitude(3.1f, 5.8f));
//	System.out.println(terrain.altitude(3.0f, 5.0f));
//	System.out.println(terrain.altitude(8.9f, 5.7f));
//	System.out.println(terrain.altitude(-0.1f, 9f));
//	System.out.println(terrain.altitude(1f, -1f));
//	System.out.println(terrain.altitude(1f, 10f));
	
	
//	//TERRAIN LOADING 
//	vertexBuffer = terrain.generateVertexBuffer();
//	indicesBuffer = terrain.generateIndexBuffer();
//	//generates two buffers in gl object
//    int[] names = new int[2];
//    gl.glGenBuffers(2, names, 0);
//    
//    verticesName = names[0];
//    indicesName = names[1];
//    //binds name to array buffer and binds buffer data to array buffer
//    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
//    gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 3 * Float.BYTES, 
//    					vertexBuffer.getBuffer(), GL.GL_STATIC_DRAW);
//            
//    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
//    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity()*Float.BYTES, 
//    					indicesBuffer, GL.GL_STATIC_DRAW);
	
//    /**
//     * loads indices of the faces into the index buffer and returns it. 
//     */
//    public IntBuffer generateIndexBuffer() {
//    	//	System.out.println("width: " + width + "depth: " + depth + "\n");
//    		int numIndices = (width - 1) * (depth - 1) * 2 * 3; 
//    		int[] indices = new int[numIndices];
//    		int index = 0;
//    		for(int z = 0; z < (depth - 1); z++) {
//    			//needs width - 1 for no overlapping purposes 
//    			for (int x = 0; x < (width - 1); x++) {
//    				int vertexIndex = z * width + x;
//    				//top triangle
//    				indices[index++] = vertexIndex;
//    				indices[index++] = vertexIndex + width + 1;
//    				indices[index++] = vertexIndex + width;
//    				//bottom triangle
//    				indices[index++] = vertexIndex; 
//    				indices[index++] = vertexIndex + 1;
//    				indices[index++] = vertexIndex + width + 1;
//    			}
//    		}
//    		indicesBuffer = GLBuffers.newDirectIntBuffer(indices);
//    		return indicesBuffer;
//    }
	
//  /** Called after generate index buffer has been called 
//  * REMEMBER NORMALS NEED TO BE NORMALISED IN THE SHADER (or wherever you want to normalise them) 
//  * @precondition called after generateIndexBuffer() has been called 
//  * @postcondition returns normal buffer which contains a normal for every single vertex in the indices buffer 
//  * (some of which are indexed twice, so as a result, some vertices will have two vertices. 
//  */
// public Point3DBuffer generateNormalBuffer() {
// 		ArrayList<Point3D> normals = new ArrayList <Point3D>();
// 		int numTriangles = (width - 1) * (depth - 1) * 2;
// 		for(int i = 0; i < numTriangles; i++) {
// 			Point3D v0 = vertices.get(indices[i]);
// 			Point3D v1 = vertices.get(indices[i + 1]);
// 			Point3D v2 = vertices.get(indices[i + 2]);
// 			
// 			Vector3 normal = normal(v0,v1,v2);
// 			normals.add(indices[i], new Point3D(0,0,0).translate(normal));
// 			normals.add(indices[i + 1], new Point3D(0,0,0).translate(normal));
// 			normals.add(indices[i + 2], new Point3D(0,0,0).translate(normal));
// 		}
// 		normalBuffer = new Point3DBuffer(normals);
// 		return normalBuffer;
// }
// 
// private Vector3 normal (Point3D p1, Point3D p2, Point3D p3) {
// 		Vector3 a = p2.minus(p1);
// 		Vector3 b = p3.minus(p1);
// 		return a.cross(b).normalize();
// }

//  /**
//  * loads into the vertex buffer
//  * no idea if this is right. 
//  * do we have to scale the terrain and make a bunch of assertions? 
//  */
// public Point3DBuffer generateVertexBuffer() {
// 		ArrayList <Point3D> vertices = new ArrayList<Point3D>();
// 		for (int z = 0; z < depth; z++) {
// 			for(int x = 0; x < width; x++) {
// 				vertices.add(new Point3D((float) x,  (float) getGridAltitude(x,z), (float) z));
// 			}
// 		}
// 		this.vertices = vertices; 
// 		vertexBuffer = new Point3DBuffer(vertices);
// 		return vertexBuffer;
// }
	
    // Indices to draw the shape
//  List<Integer> shapeIndices = Arrays.asList(0,1,2, 2,3,0);
//  TriangleMesh front = new TriangleMesh(shape, shapeIndices, true);
	
//  // Indices for the extruded shape
//  List<Integer> shapeExtIndices = new ArrayList<>(shapeIndices);
//  Collections.reverse(shapeExtIndices);
  
  // The extruded shape as its own mesh
//  TriangleMesh back = new TriangleMesh(shapeExt, shapeExtIndices, true);
	
//	List<Road> roads = new ArrayList<>();
//roads = terrain.roads();
//for(Road r: roads) {
//		for(int i = 0; i < 32; i++) {
//			float t = i * 1.0f/32;
//			Point2D p = r.point(t);
//			Point3D p3D = new Point3D(p.getX(), 1, p.getY());
//		//	p3D.draw(gl, position);
//			CoordFrame3D f = new CoordFrame3D(r.frenet(t)).translate(0, 0, translation);
//			f.draw(gl);
//			
//			
//			
//			
//			Point2D p0 = new Point2D(1.5f,3.0f);
//			int altitude = 1;
//			int width = 3;
//    		//front face is a rectangle with centre point of bottom edge being origin and height 0.1 (it is facing the positive z direction)
//	        Point3D left = new Point3D(- width/2,0, 0);
//	        Point3D right = new Point3D(+ width/2, 0, 0);
//	        List<Point3D> shape = Arrays.asList(left, right);
//	        
//	        // The extruded flat surface
//	        // Remember size refers to number of cubic curves in the spine and segments refers to # of individual segments that approximate curve
//	        ArrayList<Point3D> shapeVert = new ArrayList<Point3D>(); //contains all the surface vertices
//        		float param = t;
//        		//calculating all the vertices 
//	        for (Point3D s : shape) {
//	        		Point3D s1 = r.frenet(param).multiply(s.asHomogenous()).asPoint3D();
//	            shapeVert.add(r.frenet(param).multiply(s.asHomogenous()).asPoint3D());
//	            Shader.setPenColor(gl, Color.PINK);
//	            s1.draw(gl, position);
//	        }
//	        
//		}
	
//}
	
	
//	//end test code 
//	Road r1 = terrain.roads().get(0);
//CoordFrame3D f1 = new CoordFrame3D(r1.frenet(1.5f)).translate(0, 0, translation);
//CoordFrame3D f2 = new CoordFrame3D(r1.frenet(0.5f)).translate(0, 0, translation);
//Point3D p1 = new Point3D(r1.point(1.5f).getX(), 0, r1.point(1.5f).getY());
//Point3D p2 = new Point3D(r1.point(0.5f).getX(), 0, r1.point(0.5f).getY());
//Shader.setPenColor(gl, Color.PINK);
//
//p1.draw(gl);
//p2.draw(gl);
//f1.draw(gl);
//f2.draw(gl);
//Shader.setPenColor(gl, Color.RED);
////end test code 
}
