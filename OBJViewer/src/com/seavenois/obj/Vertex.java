package com.seavenois.obj;

/**
 * Basic class representing a vertex, containing its coordinates.
 */
public class Vertex {
	float x, y, z;

	/**
	 * Class constructor. Initializes a vertex in the coordinates origin.
	 * @see  Vertex(float x, float y, float z)
	 */
	Vertex(){
		this(0, 0, 0);
	}
	
	/**
	 * Class constructor. Initializes a vertex in the given coordinates.
	 * 
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @param z The Z coordinate.
	 * @see  Vertex(float x, float y, float z)
	 */
	Vertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Returns the X coordinate of the vertex.
	 * 
	 * @return      The X coordinate of the vertex.
	 * @see         setX(float x)
	 * @see			getY()
	 * @see			getZ()
	 */
	public float getX() {
		return x;
	}

	/**
	 * Sets the X coordinate of the vertex.
	 * 
	 * @param x The X coordinate.
	 * @see			getX()
	 * @see         setY(float y)
	 * @see         setZ(float z)
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Returns the Y coordinate of the vertex.
	 * 
	 * @return      The Y coordinate of the vertex.
	 * @see         setX(float y)
	 * @see			getX()
	 * @see			getZ()
	 */
	public float getY() {
		return y;
	}

	/**
	 * Sets the Y coordinate of the vertex.
	 * 
	 * @param y The Y coordinate.
	 * @see			getY()
	 * @see         setX(float x)
	 * @see         setZ(float z)
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Returns the Z coordinate of the vertex.
	 * 
	 * @return      The Z coordinate of the vertex.
	 * @see         setZ(float z)
	 * @see			getX()
	 * @see			getY()
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Sets the Z coordinate of the vertex.
	 * 
	 * @param z The Z coordinate.
	 * @see			getZ()
	 * @see         setX(float x)
	 * @see         setY(float y)
	 */
	public void setZ(float z) {
		this.z = z;
	}
}
