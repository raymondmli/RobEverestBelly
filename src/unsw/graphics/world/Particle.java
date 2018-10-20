package unsw.graphics.world;

import java.util.Random;

public class Particle {
	float life;
	float r,g,b;
	float x,y,z;
	float speedX, speedY, speedZ;
	
	private Random rand = new Random();
	
	public Particle() {
		rain();
	}
	
	public void rain() {
		//decide position
		float range = 10.0f;
		float sign = rand.nextFloat();
		if (sign < 0.5) {
			x = rand.nextFloat() * (-1.0f) * range;
		} else {
			x = rand.nextFloat() * range;
		}
		
		sign = rand.nextFloat();
		if (sign < 0.5) {
			z = rand.nextFloat() * (-1.0f) * range;
		} else {
			z = rand.nextFloat() * range;
		}
		
		y = range;
		
		float maxSpeed = 0.1f;
		float speed = 0.02f + (rand.nextFloat() - 0.5f)  * maxSpeed;
		float angle = (float) Math.toRadians(rand.nextInt(360));
		
		speedX = speedZ = 0;
		speedY = speed * (float) Math.sin(angle);
				
		r = g = 0;
		b = 1.0f;
		
		life = 1.0f;
		
	}
	

}
