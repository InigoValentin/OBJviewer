package com.seavenois.obj;

/**
 * Basic class representing colors.
 */
public class Color {
	int r, g, b, a;

	/**
	 * Returns an integer with the red value of the color.
	 * 
	 * @return      The red value of the color (0 - 255).
	 * @see         setR(int r)
	 * @see			getG()
	 * @see			getB()
	 */
	public int getR() {
		return r;
	}

	/**
	 * Returns an integer with the green value of the color.
	 * 
	 * @return      The green value of the color (0 - 255).
	 * @see         setG(int g)
	 * @see			getR()
	 * @see			getB()
	 */
	public int getG() {
		return g;
	}

	/**
	 * Returns an integer with the blue value of the color.
	 * 
	 * @return      The blue value of the color (0 - 255).
	 * @see         setB(int b)
	 * @see			getR()
	 * @see			getG()
	 */
	public int getB() {
		return b;
	}
	
	/**
	 * Returns an integer with the alpha value of the color.
	 * 
	 * @return      The alpha value of the color (0 - 255).
	 * @see         setA(int r)
	 */
	public int getA(){
		return a;
	}
	
	/**
	 * Sets the red component (0-255) of the color.
	 * 
	 * @param  r  The red value of the color (0 - 255). If lower than 0, 
	 * it will be set to 0. If greater than 255, it will be set to 255.
	 * @see         getR()
	 * @see         setG(int g)
	 * @see         setB(int b)
	 */
	public void setR(int r) {
		if (r < 0)
			this.r = 0;
		else if (r > 255)
			this.r = 255;
		else
			this.r = r;
	}

	/**
	 * Sets the green component (0-255) of the color.
	 * 
	 * @param  g  The green value of the color (0 - 255). If lower than 0, 
	 * it will be set to 0. If greater than 255, it will be set to 255.
	 * @see         getG()
	 * @see         setR(int r)
	 * @see         setB(int b)
	 */
	public void setG(int g) {
		if (g < 0)
			this.g = 0;
		else if (g > 255)
			this.g = 255;
		else
			this.g = g;
	}

	/**
	 * Sets the blue component (0-255) of the color.
	 * 
	 * @param  b  The blue value of the color (0 - 255). If lower than 0, 
	 * it will be set to 0. If greater than 255, it will be set to 255.
	 * @see         getB()
	 * @see         setR(int r)
	 * @see         setG(int g)
	 */
	public void setB(int b) {
		if (b < 0)
			this.b = 0;
		else if (b > 255)
			this.b = 255;
		else
			this.b = b;
	}

	/**
	 * Sets the alpha component (0-255) of the color.
	 * 
	 * @param  a  The alpha value of the color (0 - 255). If lower than 0, 
	 * it will be set to 0. If greater than 255, it will be set to 255.
	 * @see         getA()
	 */
	public void setA(int a) {
		if (a < 0)
			this.a = 0;
		else if (a > 255)
			this.a = 255;
		else
			this.a = a;
	}

	/**
	 * Class constructor. Initializes a black, fully opaque color.
	 * @see  Color(int r, int g, int b)
	 * @see  Color(int r, int g, int b, int a)
	 */
	public Color(){
		this(0, 0, 0, 255);
	}
	
	/**
	 * Class constructor. Initializes the color with values for the red,
	 * green and blue, but not for the alpha, that will be set to fully opaque.
	 * 
	 * @param  r  The red value of the color (0 - 255). If lower than 0, 
	 * it will be set to 0. If greater than 255, it will be set to 255.
	 * @param  g  The green value of the color (0 - 255). If lower than 0, 
	 * it will be set to 0. If greater than 255, it will be set to 255.
	 * @param  b  The blue value of the color (0 - 255). If lower than 0, 
	 * it will be set to 0. If greater than 255, it will be set to 255.
	 * @see  Color()
	 * @see  Color(int r, int g, int b, int a)
	 */
	public Color(int r, int g, int b){
		this(r, g, b, 255);
	}

	/**
	 * Class constructor. Initializes the color with values for the red,
	 * green and blue and alpha.
	 * 
	 * @param  r  The red value of the color (0 - 255). If lower than 0, 
	 * it will be set to 0. If greater than 255, it will be set to 255.
	 * @param  g  The green value of the color (0 - 255). If lower than 0, 
	 * it will be set to 0. If greater than 255, it will be set to 255.
	 * @param  b  The blue value of the color (0 - 255). If lower than 0, 
	 * it will be set to 0. If greater than 255, it will be set to 255.
	 * @param  a  The alpha value of the color (0 - 255). If lower than 0, 
	 * it will be set to 0. If greater than 255, it will be set to 255.
	 * @see  Color()
	 * @see  Color(int r, int g, int b, int a)
	 */
	public Color(int r, int g, int b, int a) {
		
		//Red
		if (r < 0)
			this.r = 0;
		else if (r > 255)
			this.r = 255;
		else
			this.r = r;
		
		//Green
		if (g < 0)
			this.g = 0;
		else if (g > 255)
			this.g = 255;
		else
			this.g = g;
		
		//Blue
		if (b < 0)
			this.b = 0;
		else if (b > 255)
			this.b = 255;
		else
			this.b = b;
		
		//Alpha
		if (a < 0)
			this.a = 0;
		else if (a > 255)
			this.a = 255;
		else
			this.a = a;
	}
}
