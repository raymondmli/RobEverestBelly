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
import unsw.graphics.Texture;
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
    private static final boolean USE_TEXTURE = true;
    private Texture texTree;	//index 0
    private Texture texTerrain; //index 1
    private Texture texRoad; //index 2
    private Texture texAvatar; //index 3
    
    private Terrain terrain;
    private TriangleMesh tree;
    private TriangleMesh terrainMesh;
    private TriangleMesh avatarMesh; 
    private ArrayList<ArrayList<TriangleMesh>> roadMeshes; 

    private Camera camera;
	private float aspectRatio;
	
	private Avatar avatar; 
	
	private int time; 
	private static final int MIDNIGHT = -1;
	private static final int MIDDAY = 1; 
	private static final int DUSK = 0;

    public World(Terrain terrain) {
    		super("Assignment 2", 800, 600);
        this.terrain = terrain;
        time = 0;
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
        terrainMesh = new TriangleMesh(terrain.generateVertices(), terrain.generateIndices()
        					, true, terrain.getTexCoords());
		texTree = new Texture(gl, "res/textures/rock.bmp","bmp", false); 
		texTerrain = new Texture(gl, "res/textures/grass.bmp", "bmp", true);
		texRoad = new Texture(gl, "res/textures/asphaltTexture.jpg","jpg", true);
		texAvatar = new Texture(gl, "res/textures/tigerSkin3.jpg", "jpg", false);
        try {
            avatarMesh = new TriangleMesh("res/models/bunny_res3.ply", true, true);
            tree = new TriangleMesh("res/models/tree.ply", true, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		tree.init(gl);
		avatarMesh.init(gl);
        terrainMesh.init(gl);
        
        camera = new Camera(terrain);
		getWindow().addKeyListener(this);

        Shader shader = null;
        shader = new Shader(gl, "shaders/vertex_phong_dir.glsl",
                "shaders/fragment_phong_dir.glsl");
        shader.use(gl);

        initRoads(gl);
        
        avatar = new Avatar(terrain, avatarMesh);
	}
	
	@Override
	public void display(GL3 gl) {
		super.display(gl);
	//	camera.setView(gl);
		Shader.setViewMatrix(gl, camera.setView(gl));
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
        drawTerrain(gl, translateZ);
        terrainMesh.draw(gl, frame.translate(0, 0, translateZ));
        for(Tree t: terrain.trees()) {
        		drawTree(gl, t.getPosition().getX(), t.getPosition().getZ(), translateZ);
        }
        //draw roads
		gl.glEnable(GL3.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(-1.0f, -1.0f);
        drawRoads(gl,translateZ);
        gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
       // rotationY+=1;
        
        drawAvatar(gl, 0, 0, 0);

	}
	/**
	 * draws avatar at specified lx
	 * @param gl
	 * @param x
	 * @param z
	 * @param translation
	 */
    private void drawAvatar (GL3 gl, float x, float z, float translation) {
		float altitude = (float) terrain.altitude(x, z);
//  		Matrix4 transformation = position.getMatrix().multiply(terrainFrame.getMatrix());
		CoordFrame3D position1 = avatar.getFrame();         //.translate(0, 1f, translation)
								                                  //.translate(new Point3D(x, altitude, z)).scale(0.2f,0.2f,0.2f);
		position1.draw(gl);
	    if (USE_LIGHTING) {          
	        // Set the material properties
	        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
	        Shader.setColor(gl, "diffuseCoeff", new Color(0.8f, 0.8f, 0.8f));
	        Shader.setColor(gl, "specularCoeff", new Color(0.4f, 0.4f, 0.4f));
	        Shader.setFloat(gl, "phongExp", 16f);
	    }
	    Shader.setInt(gl, "tex", 3);
	    
	    gl.glActiveTexture(GL.GL_TEXTURE3);
	    gl.glBindTexture(GL.GL_TEXTURE_2D, texAvatar.getId());
	    //255,192,203
	    Shader.setPenColor(gl, new Color(255f/256,192f/256,203f/256));
		avatarMesh.draw(gl, position1);
    }

    private void drawTerrain(GL3 gl, float translation) {
        if (USE_LIGHTING) {          
            // Set the material properties
            Shader.setColor(gl, "ambientCoeff", Color.WHITE);
            Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
            Shader.setColor(gl, "specularCoeff", new Color(0.1f, 0.1f, 0.1f));
            Shader.setFloat(gl, "phongExp", 16f);
        }
        Shader.setInt(gl, "tex", 1);

        gl.glActiveTexture(GL.GL_TEXTURE1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texTerrain.getId());
        
        Shader.setPenColor(gl, new Color(0.49f, 0.99f, 0f));
        terrainMesh.draw(gl, CoordFrame3D.identity().translate(0, 0, translation));
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
        if (USE_LIGHTING) {          
            // Set the material properties
            Shader.setColor(gl, "ambientCoeff", Color.WHITE);
            Shader.setColor(gl, "diffuseCoeff", new Color(0.8f, 0.8f, 0.8f));
            Shader.setColor(gl, "specularCoeff", new Color(0.4f, 0.4f, 0.4f));
            Shader.setFloat(gl, "phongExp", 16f);
        }
        Shader.setInt(gl, "tex", 0);
        
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texTree.getId());
        //166,142,94
        Shader.setPenColor(gl, new Color(255f/256,222f/256,173f/256));
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
    	    	        if (USE_LIGHTING) {          
    	    	            // Set the material properties
    	    	            Shader.setColor(gl, "ambientCoeff", Color.WHITE);
    	    	            Shader.setColor(gl, "diffuseCoeff", new Color(0.4f, 0.4f, 0.4f));
    	    	            Shader.setColor(gl, "specularCoeff", new Color(0.1f, 0.1f, 0.1f));
    	    	            Shader.setFloat(gl, "phongExp", 16f);
    	    	        }
    	    	        Shader.setInt(gl, "tex", 2);

    	    	        gl.glActiveTexture(GL.GL_TEXTURE2);
    	    	        gl.glBindTexture(GL.GL_TEXTURE_2D, texRoad.getId());
    	    	        
    	    	        Shader.setPenColor(gl, Color.WHITE);
    	    			roadSegment.draw(gl, position);
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
	    		ArrayList<TriangleMesh> currRoadMeshes = r.makeExtrusion(r.size());
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
		} else if (code == 87) { //w
			avatarBackwards();
		} else if (code == 65) {  //a
			avatarLeft();
		} else if (code == 83) { //s
			avatarForward();
		} else if (code == 68) {  //d
			avatarRight();
		} else if (code == 78) {
			toggleDayNight();
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
	
	private void avatarBackwards() {
		System.out.println("back");
		avatar.backwards(terrain);
		
	}

	private void avatarRight() {
		System.out.println("right");	
		avatar.turnRight();
	}

	private void avatarForward() {
		System.out.println("front");	
		avatar.forwards(terrain);
	}

	private void avatarLeft() {
		System.out.println("left");
		avatar.turnLeft();
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
        tree.destroy(gl);
        terrainMesh.destroy(gl);
	}
	
	public void toggleDayNight() {
		if(time == MIDNIGHT) {
			time = MIDDAY;
			return; 
		}
		time = MIDNIGHT; 
	}
	
}
