package com.seavenois.obj;

import android.util.Log;

public class Face implements Comparable<Face>{
	
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
	
	public boolean hasMaterial(){
		if (mat == null)
			return false;
		else
			return true;
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

	@Override
	public int compareTo(Face another) {
		float lZ, rZ;
		int i = 0;
		float sum = .0f;
		
		while (i <  this.getVertexCount()){
			sum = sum + this.getVertex(i).getZ();//lhparseFloat(vert[a[i]][2]);
			i = i + 1;
		}
		lZ = sum / i;
		i = 0;
		sum = 0;
		while (i <  another.getVertexCount()){
			sum = sum + another.getVertex(i).getZ();//lhparseFloat(vert[a[i]][2]);
			i = i + 1;
		}
		rZ = sum / i;
		i = 0;
		if (lZ < rZ) return -1;
		if (lZ > rZ) return 1;
		return 0;
	}
}
