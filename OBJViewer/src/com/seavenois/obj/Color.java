package com.seavenois.obj;

public class Color {
	int r, g, b, a;

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}
	
	public int getA(){
		return a;
	}

	public Color(){
		this(0, 0, 0, 255);
	}
	
	public void setR(int r) {
		this.r = r;
	}

	public void setG(int g) {
		this.g = g;
	}

	public void setB(int b) {
		this.b = b;
	}

	public void setA(int a) {
		this.a = a;
	}

	public Color(int r, int g, int b){
		this(r, g, b, 255);
	}

	public Color(int r, int g, int b, int a) {
		super();
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
}
