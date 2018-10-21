package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * class to do with all sorts of lighting for the objects in unsw.graph.world
 * assuming that only diffuse and ambient light are changed on transition to night 
 * i.e. specular highlights are not affected (stuff doesn't get any less shinier at night) 
 * @author raymondli
 */
public class LightingProperties {
	private Terrain t; 
	private float prevTime;
	private float currTime; 
	private int currPeriod; 
	private static final int MIDNIGHT = 1;
	private static final int DAWN = 2; 
	private static final int MORNING = 3;
	private static final int MIDDAY = 4;
	private static final int AFTERNOON = 5; 
	
	private int timeDir; 
	
	public LightingProperties() {
		currPeriod = DAWN; 
		currTime = 0;
		prevTime = -0.1f;
		timeDir = 1;
	}
	
	
    public static void main(String[] args) throws FileNotFoundException {
		LightingProperties l = new LightingProperties();
	//	System.out.println(l.getPeriod(0));
    }
	
    public void setPrevTime(float time) {
    		this.prevTime = time; 
    }
    
    public void setTimeDir(int dir) {
    		this.timeDir = dir; 
    }
    
	public int getPeriod(float time) {
		if(time < -0.2 && time >= -1) 
			return MIDNIGHT;
		if(time > -0.2 &&  time <= 0.1 && timeDir == 1)
			return DAWN;
		if(timeDir == 1 && time > 0.1 && time <= 1)
			return MORNING;
		if(timeDir == 0 && time < 1 && time >= 0.3)
			return MIDDAY; 
		if(time < 0.3 && timeDir == 0)
			return AFTERNOON; 			
		return -1; 
	}
	
	public void setPeriod(int period) {
		this.currPeriod = period; 
	}
	
	public Color getSunColour(float time) {
		if(currPeriod == MIDNIGHT) { //-1 -> -0.2 
			return new Color(1 + time, 0 , -time * 160/255);
		}
		if(currPeriod == DAWN) { //
			float convertedTime = 3*time + 0.6f;
			return new Color(255/255, convertedTime, 0);
		}
		if(currPeriod == MORNING) { //0.1 - 1
			return new Color(255/255, 255/255, time);
		}
		if(currPeriod == MIDDAY) { //1 - 0.3
			return new Color(1, 1, time - 0.2f);
		}
		if(currPeriod == AFTERNOON) {
			float convertedTime = (time + 0.2f) * 2;
			return new Color (1, convertedTime, 0.1f);
		}
		return new Color(255,100,180);
	}
	
}
