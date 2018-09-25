package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D {

    private float rotationY = 0;
    private Point3DBuffer vertexBuffer;
    private IntBuffer indicesBuffer;
    private int verticesName;
    private int indicesName;
    private Terrain terrain;
    private TriangleMesh tree; 
    
    public World(Terrain terrain) {
    		super("Assignment 2", 800, 600);
        this.terrain = terrain;
   
    }
   
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        World world = new World(terrain);
        world.start();
    }

	@Override
	public void display(GL3 gl) {
		super.display(gl);
		Shader.setPenColor(gl, Color.cyan);
        CoordFrame3D frame = CoordFrame3D.identity();
        drawTerrain(gl, frame.rotateY(rotationY));
        drawTree(gl, 1.5f, 1.5f, frame.rotateY(rotationY));
        rotationY+=1;
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
        gl.glDeleteBuffers(2, new int[] { indicesName, verticesName }, 0);
	}
	
    public void drawTerrain(GL3 gl, CoordFrame3D frame) {
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawElements(GL.GL_TRIANGLES, indicesBuffer.capacity(), 
                    GL.GL_UNSIGNED_INT, 0);
    }
    
    /**
     * draws a tree at the specified x and z coordinates 
     * @param gl
     * @param x
     * @param z
     */
    public void drawTree (GL3 gl, float x, float z, CoordFrame3D terrainFrame) {
    		float altitude = (float) terrain.altitude(x, z);
    	//	CoordFrame3D position = CoordFrame3D.identity().translate(new Point3D(x, altitude, z)).rotateY(rotationY).scale(0.2f, 0.2f, 0.2f);
  //  		Matrix4 transformation = position.getMatrix().multiply(terrainFrame.getMatrix());
   // 		CoordFrame3D finalPos = new CoordFrame3D(transformation);
    	//	tree.draw(gl, position);
    }

	@Override
	public void init(GL3 gl) {
		super.init(gl);
		//TERRAIN LOADING 
		vertexBuffer = terrain.generateVertexBuffer();
		indicesBuffer = terrain.generateIndexBuffer();
		//generates two buffers in gl object
        int[] names = new int[2];
        gl.glGenBuffers(2, names, 0);
        
        verticesName = names[0];
        indicesName = names[1];
        //binds name to array buffer and binds buffer data to array buffer
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 3 * Float.BYTES, 
        					vertexBuffer.getBuffer(), GL.GL_STATIC_DRAW);
                
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity()*Float.BYTES, 
        					indicesBuffer, GL.GL_STATIC_DRAW);
        //TREE LOADING 
        try {
            tree = new TriangleMesh("res/models/tree.ply");
            tree.init(gl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}
	

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}
}
