package com.seavenois.obj;

import android.util.Log;

public class Face {
	
	static final int MAX_VERTIZES = 64;
	
	Vertex[] vert = new Vertex[MAX_VERTIZES];
	Material mat;
	int vertexCount;
	
	public Face(){
		vertexCount = 0;
	}
	
	
	public int getVertexCount(){
		return vertexCount;
	}
	
	public Material getMaterial(){
		return mat;
	}
	
	public void addVertex(Vertex v){
		if (vertexCount < MAX_VERTIZES){
			vert[vertexCount] = v;
			vertexCount ++;
		}
		else{
			Log.e("Face error", "Unable to add more than " + Integer.toString(MAX_VERTIZES) + " vertizes");
		}
	}
	
	public Vertex getVertex(int i){
		if (i >= MAX_VERTIZES)
			Log.w("Face warning", "Requesting null vertex " + Integer.toString(i));
		return vert[i];
	}
	
	public void setMaterial(Material mat){
		this.mat = mat;
	}
}
