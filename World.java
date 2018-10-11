package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point2D;
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
    private ArrayList<ArrayList<TriangleMesh>> roadMeshes; 
    private TriangleMesh tree;
    private TriangleMesh terrainMesh;
    private Camera camera;
   
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
        shader = new Shader(gl, "shaders/vertex_phong.glsl",
                "shaders/fragment_phong_dir.glsl");
        shader.use(gl);

        initRoads(gl);
        
	}
	
	@Override
	public void display(GL3 gl) {
		super.display(gl);
		Shader.setViewMatrix(gl, camera.getMatrix());
		
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
        float translateZ = 0;
		Shader.setPenColor(gl, Color.CYAN);
        terrainMesh.draw(gl, frame.translate(0, 0, translateZ));
        for(Tree t: terrain.trees()) {
        		drawTree(gl, t.getPosition().getX(), t.getPosition().getZ(), translateZ);
        }
        drawRoads(gl,translateZ);

       // rotationY+=1;
        // Set the lighting properties

	}

   
    /**
     * draws a tree at the specified x and z coordinates 
     * @param gl
     * @param x
     * @param z
     */
    private void drawTree (GL3 gl, float x, float z, float translation) {
    		float altitude = (float) terrain.altitude(x, z);
  //  		Matrix4 transformation = position.getMatrix().multiply(terrainFrame.getMatrix());
    		CoordFrame3D position1 = CoordFrame3D.identity().translate(0, 1f, translation)
    								.translate(new Point3D(x, altitude, z)).scale(0.2f,0.2f,0.2f);
    		position1.draw(gl);
    		Shader.setPenColor(gl, Color.RED);
    		tree.draw(gl, position1);
    }

    /**
     * draws roads 
     */
    private void drawRoads (GL3 gl, float translation) {
    		CoordFrame3D position = CoordFrame3D.identity().translate(0, 0, translation);
    	    for(ArrayList<TriangleMesh> roadMesh: roadMeshes) {
    	    		for(TriangleMesh roadSegment: roadMesh) {
    	    			Shader.setPenColor(gl, Color.PINK);
    	    			roadSegment.draw(gl, position);
    	    		}
    	    }
    		List<Road> roads = new ArrayList<>();
	    roads = terrain.roads();
	    for(Road r: roads) {
	    		for(int i = 0; i < 32; i++) {
	    			float t = i * 1.0f/32;
	    			Point2D p = r.point(t);
	    			Point3D p3D = new Point3D(p.getX(), 1, p.getY());
	    		//	p3D.draw(gl, position);
	    			CoordFrame3D f = new CoordFrame3D(r.frenet(t)).translate(0, 0, translation);
	    			f.draw(gl);
	    			
	    			
	    			
	    			
	    			Point2D p0 = new Point2D(1.5f,3.0f);
	    			int altitude = 1;
	    			int width = 3;
	        		//front face is a rectangle with centre point of bottom edge being origin and height 0.1 (it is facing the positive z direction)
	    	        Point3D left = new Point3D(- width/2,0, 0);
	    	        Point3D right = new Point3D(+ width/2, 0, 0);
	    	        List<Point3D> shape = Arrays.asList(left, right);
	    	        
	    	        // The extruded flat surface
	    	        // Remember size refers to number of cubic curves in the spine and segments refers to # of individual segments that approximate curve
	    	        ArrayList<Point3D> shapeVert = new ArrayList<Point3D>(); //contains all the surface vertices
    	        		float param = t;
    	        		//calculating all the vertices 
    		        for (Point3D s : shape) {
    		        		Point3D s1 = r.frenet(param).multiply(s.asHomogenous()).asPoint3D();
    		            shapeVert.add(r.frenet(param).multiply(s.asHomogenous()).asPoint3D());
    		            Shader.setPenColor(gl, Color.PINK);
    		            s1.draw(gl, position);
    		        }
	    	        
	    		}
	    }
    }
    /**
     * init function for drawRoads that initialises each mesh within the list of lists
     */
    private void initRoads(GL3 gl) {
    		List<Road> roads = new ArrayList<>();
	    roads = terrain.roads();
	  //list of arraylists...each element contains an arraylist corresponding to the three meshes of each road
	    ArrayList<ArrayList<TriangleMesh>> roadMeshes = new ArrayList<ArrayList<TriangleMesh>>(); 
	    for(Road r: roads) {
	    		r.setTerrain(terrain);
	    		ArrayList<TriangleMesh> currRoadMeshes = r.makeExtrusion();
	    		roadMeshes.add(currRoadMeshes);
	    		for(TriangleMesh currRoadSegment: currRoadMeshes) {
	    			currRoadSegment.init(gl);
	    		}
	    }
	    this.roadMeshes = roadMeshes;
    }
    

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        aspectRatio = width/(float)height;
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
