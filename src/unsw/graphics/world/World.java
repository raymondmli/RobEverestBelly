package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Line3D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D implements KeyListener {

    private static final boolean USE_LIGHTING = true;
	private float aspectRatio;
    private static final float ROTATION_SCALE = 0.5f;
    private float rotationY = 0;
    private int verticesName;
    private int indicesName;
    private Terrain terrain;
    private TriangleMesh tree;
    private TriangleMesh terrainMesh;
    private Camera camera;
    private Line3D p;
   
    public World(Terrain terrain) {
    		super("Assignment 2", 800, 600);
        this.terrain = terrain;
        terrainMesh = new TriangleMesh(terrain.generateVertices(), terrain.generateIndices(), true);
        try {
			tree = new TriangleMesh("res/models/tree.ply", true, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
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
	public void init(GL3 gl) {
		super.init(gl);
		tree.init(gl);
        terrainMesh.init(gl);
        
        camera = new Camera();
		getWindow().addKeyListener(this);

        Shader shader = null;
//        shader = new Shader(gl, "shaders/vertex_phong.glsl",
//                "shaders/fragment_phong_dir.glsl");
//        shader.use(gl);
        
	}
	
	@Override
	public void display(GL3 gl) {
		super.display(gl);
		//Shader.setViewMatrix(gl, camera.getMatrix());
		camera.setView(gl);
		
        if (USE_LIGHTING) {
            Shader.setPoint3D(gl, "lightPos", terrain.getSunlight());
            Shader.setColor(gl, "lightIntensity", Color.WHITE);
            Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
            
            // Set the material properties
            Shader.setColor(gl, "ambientCoeff", Color.WHITE);
            Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
            Shader.setColor(gl, "specularCoeff", new Color(0.2f, 0.2f, 0.2f));
            Shader.setFloat(gl, "phongExp", 16f);
        }
        CoordFrame3D frame = CoordFrame3D.identity();
        frame.draw(gl);
        float translateZ = -9;
		Shader.setPenColor(gl, Color.CYAN);
        terrainMesh.draw(gl, frame.translate(0, 0, translateZ));
        for(Tree t: terrain.trees()) {
        		drawTree(gl, t.getPosition().getX(), t.getPosition().getZ(), translateZ);
        }
        
       // rotationY+=1;
        // Set the lighting properties

	}

   
    /**
     * draws a tree at the specified x and z coordinates 
     * @param gl
     * @param x
     * @param z
     */
    public void drawTree (GL3 gl, float x, float z, float translation) {
    		float altitude = (float) terrain.altitude(x, z);
  //  		Matrix4 transformation = position.getMatrix().multiply(terrainFrame.getMatrix());
    		CoordFrame3D position1 = CoordFrame3D.identity().translate(0, 1f, translation)
    								.translate(new Point3D(x, altitude, z)).scale(0.2f,0.2f,0.2f);
    		position1.draw(gl);
    		Shader.setPenColor(gl, Color.RED);
    		tree.draw(gl, position1);
    }


	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        camera.reshape(width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}
	@Override
	public void keyPressed(KeyEvent e) {
		short code = e.getKeyCode();
		
		if (code == 149) {
			cameraLeft();
		} else if (code == 150) {
			cameraForward();			
		} else if (code == 151) {
			cameraRight();
		} else if (code == 152) {
			cameraBackwards();
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}

//xd
	private void cameraBackwards() {
		System.out.println("back");
		camera.backwards(terrain);
		
	}

	private void cameraRight() {
		System.out.println("right");	
		camera.turnRight();
	}

	private void cameraForward() {
		System.out.println("front");	
		camera.forwards(terrain);
	}

	private void cameraLeft() {
		System.out.println("left");
		camera.turnLeft();
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
        gl.glDeleteBuffers(2, new int[] { indicesName, verticesName }, 0);
        tree.destroy(gl);
        terrainMesh.destroy(gl);
	}
	
}
