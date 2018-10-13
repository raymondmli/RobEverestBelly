package unsw.graphics.world;

import java.awt.Color;

/**
 * class to do with all sorts of lighting for the objects in unsw.graph.world
 * assuming that only diffuse and ambient light are changed on transition to night 
 * i.e. specular highlights are not affected (stuff doesn't get any less shinier at night) 
 * @author raymondli
 */
public class LightingProperties {
	private Terrain t; 
	private static final int NIGHT = 1;
	private static final int DAY = 0; 
	
	public LightingProperties(Terrain t) {
		this.t = t;
	}
	public Color terrainDiffuse(int time) {
		return Color.black;
	}

}
