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
	private boolean fpp = false;
	
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
	
	private float time; 
	private int timeDir;
	private static final int MIDNIGHT = -1;
	private static final int MIDDAY = 1; 
	private static final int DUSK = 0;
	private LightingProperties lighting; 

    public World(Terrain terrain) {
    		super("Assignment 2", 800, 600);
        this.terrain = terrain;
        lighting = new LightingProperties();
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
        time = 0;
        timeDir = 1;
        terrainMesh = new TriangleMesh(terrain.generateVertices(), terrain.generateIndices()
        					, true, terrain.getTexCoords());
		texTree = new Texture(gl, "res/textures/rock.bmp","bmp", false); 
		texTerrain = new Texture(gl, "res/textures/grass.bmp", "bmp", true);
		texRoad = new Texture(gl, "res/textures/asphaltTexture.jpg","jpg", true);
		texAvatar = new Texture(gl, "res/textures/tigerSkin3.jpg", "jpg", false);
        try {
            avatarMesh = new TriangleMesh("res/models/apple.ply", true, true);
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
        
        avatar = new Avatar(terrain, avatarMesh, 1);
	}
	
	@Override
	public void display(GL3 gl) {
		super.display(gl);
	//	camera.getFrame().draw(gl);
	//	camera.setView(gl);
		if (fpp) {
			Shader.setViewMatrix(gl, camera.setView());
		} else {
			camera.setFrame(avatar.getFrame().translate(0f, 0.2f, 0.3f));
			Shader.setViewMatrix(gl, camera.setView());
			drawAvatar(gl, 1, 0 , 0);
		}
		Color night = new Color(0.2f,0.2f,0.2f);
		Color day = Color.WHITE;
		Color currColor = Color.white;

		currColor = lighting.getSunColour(time);
		float x = (time + 3); 
		float y = (time + 1) * (time + 1); 
		Point3D sunlight = new Point3D(x,y,0); 
        if (USE_LIGHTING) {
            Shader.setPoint3D(gl, "lightPos", sunlight);
            Shader.setColor(gl, "lightIntensity", currColor);
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
        
      //  drawAvatar(gl, 0, 0, 0);

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
//	        uniform float constAttenuation;
//	        uniform float linearAttenuation;
//	        uniform float quadAttenuation;
	    }
	    //"torch" section  
	    if(time <= 0) {
	    		//turn the torch on...which means setting torch intensity to not 0. 
	        Shader.setPoint3D(gl, "torchPos", camera.getLightPosition());
	        Shader.setColor(gl, "torchIntensity", Color.WHITE);
	        Shader.setPoint3D(gl, "torchDir", camera.getDirection());
	        Shader.setFloat(gl, "torchCutoffCos", (float)Math.cos(8*(Math.PI/180)));
	        Shader.setFloat(gl, "torchAttenuation", 64f);
	        Shader.setFloat(gl, "constAttenuation", 0.05f); //EXPECTS VALUE BETWEEN 0.01 AND 0.1
	        Shader.setFloat(gl, "linearAttenuation", 0.05f);
	        Shader.setFloat(gl, "quadAttenuation", 0.05f);
	    }
	    if(time > 0) 
	        Shader.setColor(gl, "torchIntensity", Color.BLACK);
	    //end torch section 
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
    	    	        Shader.setPenColor(gl, Color.PINK);
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
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 0.001f, 100));
	}
	@Override
	public void keyPressed(KeyEvent e) {
		short code = e.getKeyCode();
		
		if (code == 149 || code == 65) { //leftarrow or a
			cameraLeft();
			avatarLeft();
		} else if (code == 150 || code == 87) { //uparrow or w
			cameraForward();
			avatarForward();
		} else if (code == 151 || code == 68) { //rightarrow or d
			cameraRight();
			avatarRight();
		} else if (code == 152 || code == 83) { //downarrow or s
			cameraBackwards();
			avatarBackwards();
		} else if (code == 78) {
			toggleDayNight();
		} else if (code == 84) { //t
			addTime();
		} else if (code == 70) { //f
			toggleView();
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
	
	private void toggleDayNight() {
	//	System.out.println(time);
		if(time <= 0) {
			time = MIDDAY;
			return; 
		}
		time = MIDNIGHT; 
	}
	
	private void addTime() {
		lighting.setPrevTime(time);
		if(time == 1.0) 
			timeDir = 0;
		if(time == -1)
			timeDir = 1;
		if(timeDir == 1) {
			time += 0.0625f;
		}
		if(timeDir == 0)
			time -= 0.0625;
		lighting.setTimeDir(timeDir);
	//	System.out.println("time: " + lighting.getPeriod(time));
		lighting.setPeriod(lighting.getPeriod(time));
		System.out.println(time);
		System.out.println(lighting.getSunColour(time));
	}
	
	public void toggleView() {
		if(fpp == true) {
			fpp = false;
//			camera.setFrame(camera.getFrame().translate(0f,0.4f,0.5f));
		} else if (fpp == false) {
			fpp = true;
//			camera.setFrame(camera.getFrame().translate(0f,-0.4f,-0.5f));
		}
	}

}
