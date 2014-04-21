package com.seavenois.obj;

/**
 * Basic class representing a Material.
 */
public class Material {
	private String name;
	private Color color;
	
	/**
	 * Class constructor that creates a material with no name 
	 * and no {@link Color}.
	 */
	public Material(){
		this(null, null);
	}
	
	/**
	 * Class constructor that creates a material with name 
	 * but no {@link Color}.
	 * 
	 * @param name The name for the material.
	 */
	public Material(String name){
		this(name, null);
	}
	
	/**
	 * Class constructor that creates a material with {@link Color}
	 * but no name.
	 * 
	 * @param color The color of the material.
	 * @see Color
	 */
	public Material(Color color){
		this(null, color);
	}
	
	/**
	 * Class constructor that creates a material with {@link Color}
	 * and name.
	 * 
	 * @param name The name for the material.
	 * @param color The color of the material.
	 * @see Color
	 */
	public Material(String name, Color color) {
		super();
		this.name = name;
		this.color = color;
	}
	
	/**
	 * Returns a {@link String} with the name of the material.
	 * 
	 * @return The name of the material.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets a new name to the material.
	 * 
	 * @param name The name to be given to the material.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the material {@link Color}.
	 * 
	 * @return The {@link Color} of the material. 
	 * @see Color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Sets a new {@link Color} for the material.
	 * 
	 * @param color {@link Color} to be assigned to the material. 
	 * @see Color
	 */
	public void setColor(Color color) {
		this.color = color;
	}
}
