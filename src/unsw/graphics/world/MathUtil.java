package unsw.graphics.world;

import java.io.File;
import java.io.FileNotFoundException;

import unsw.graphics.Vector3;

public class MathUtil {
	
	
    public static void main(String[] args) {
//    		Vector3 t = lerp(0,0,2, 2,2,5, 0.3f, 'y');
//    		System.out.println(t.getX() + " " + t.getY() + " " + t.getZ());
    		float blerp = biLerp(6,4,2, 6,3,2, 6,4,2, 5,3,2, 5.8f, 3.1f);
    		float blerp2 = biLerp(5,3,2, 5,4,3, 5,3,2, 6,4,2, 5.1f, 3.9f);
    		System.out.println(blerp + "\n" + blerp2);
    }
	/**
	 * function that gets the gradient given two points 
	 * @param a0
	 * @param b0
	 * @param a1
	 * @param b1
	 * @return
	 */
	public static float getSlope(float a0, float b0, float a1, float b1) {
		if(a1 - a0 == 0)
			return -1;
		return (b1-b0)/(a1-a0);
	}
	/**
	 * Efficient version of getSlope. 
	 * @param a0
	 * @param b0
	 * @param a1
	 * @param b1
	 * @return 2 if slope is infinite 
	 */
	public static float eGetSlope(int a0, int b0, int a1, int b1) {
		if(a1 - a0 == 0)
			return -1;
		return (b1-b0)/(a1-a0);
	}
	/** performs a 'vertical' linear interpolation (lerp) on two points and an 'in-between' value 
	 * Only interpolating between 3 cases. gradient == 0, == 1 and == infinity 
	 * @param a0 'x' value of the first coordinate. Reverse a and b if you are performing a horizontal lerp
	 * @param a1	 'y' value of the second coordinate
	 * @param b0
	 * @param b1
	 * @param lerp x or y coordinate value we are performing interpolation on 
	 * @param c0 'z' value of the first coordinate.
	 * @param c1 
	 * @param orient orientation of the lerp 
	 * @return
	 */
	public static Vector3 lerp (float a0, float b0,float c0, float a1, float b1, float c1, float lerp, char orient) {
		float gradient = getSlope(a0,b0,a1,b1);
	//	System.out.println("g" + gradient);
	//	System.out.println("orient" + orient);
		float a2 = 0, c2 = 0;
		Vector3 lerped = null;
		if(orient == 'y')  {
			if(gradient == -1) 
				a2 = a0;
			else 
				a2 = (lerp-b0)/gradient + a0;	//x-coordinate of point we are 'lerping' 
			c2 = ((lerp - b0)/(b1 - b0)) * c1 + ((b1 - lerp)/(b1 - b0)) * c0;	//z-coordinate of lerped point
//			System.out.println("c2:" + c2);
			lerped = new Vector3(a2, lerp, c2);

		}
		if(orient == 'x') {
			if(gradient == 0)	//gradient should be always 0 since we only use this function for that case
				a2 = lerp;
//			else
//				a2 = (lerp - a0)/gradient + b0;
			c2 = ((lerp - a0)/(a1 - a0)) * c1 + ((a1 - lerp)/(a1 - a0)) * c0;
			lerped = new Vector3(lerp, b0, c2);
		}		
		return lerped;
	}
	
	public static Vector3 lerp (int a0, int b0,float c0, int a1, int b1, float c1, float lerp, char orient) {
		float gradient = eGetSlope(a0,b0,a1,b1);
		float a2 = 0, c2 = 0;
		Vector3 lerped = null;
		if(orient == 'y')  {
			if(gradient == -1)
				a2 = a0;
			else 
				a2 = (lerp-b0)/gradient + a0;	//x-coordinate of point we are 'lerping' 
			c2 = ((lerp - b0)/(b1 - b0)) * c1 + ((b1 - lerp)/(b1 - b0)) * c0;	//z-coordinate of lerped point
			lerped = new Vector3(a2, lerp, c2);

		}
//		if(orient == 'x') {
//			a2 = gradient*(lerp - a0) + b0;
//			if(gradient == -1)
//				a2 = b0;
//			c2 = ((lerp - a0)/(a1 - a0)) * c1 + ((a1 - lerp)/(a1 - a0)) * c0;
//			lerped = new Vector3(lerp, a2, c2);
//		}		
		return lerped;
	}
	
	/** 
	 * Biliner interpolation but takes ints instead of vector3's. 
	 * @precondition only takes ints  
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param lerp
	 * @param xLerp
	 * @return float 
	 */
	public static float biLerp (int a0,int a1, float a2, int b0, int b1, float b2, int c0, int c1, 
								float c2, int d0, int d1, float d2,float xLerp, float yLerp) {
		Vector3 lerpAB = lerp(a0, a1, a2, b0, b1, b2, yLerp, 'y');
		Vector3 lerpCD = lerp(c0, c1, c2, d0, d1, d2, yLerp, 'y');
		Vector3 biLerped = lerp( lerpAB.getX(),  lerpAB.getY(), lerpAB.getZ(), lerpCD.getX(), 
							lerpCD.getY(), lerpCD.getZ(), xLerp, 'x');
		return biLerped.getZ();
	}
}
