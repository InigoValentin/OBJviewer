package com.seavenois.obj;

public class Material {
	String name;
	Color color;
	
	public Material(){
		this(null, null);
	}
	
	public Material(String name){
		this(name, null);
	}
	
	public Material(Color color){
		this(null, color);
	}
	
	public Material(String name, Color color) {
		super();
		this.name = name;
		this.color = color;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
}
