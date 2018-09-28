package unsw.graphics.world;

import java.nio.IntBuffer;

import com.jogamp.opengl.util.GLBuffers;

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
}
