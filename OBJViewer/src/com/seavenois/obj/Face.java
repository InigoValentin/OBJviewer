package com.seavenois.obj;

import android.util.Log;

/**
 * A simple class representing a face. Contains info about all it's {@link Vertex} and it's {@link Material}.
 * Implements Comparable to sort the faces.
 */
public class Face implements Comparable<Face>{

	/**
	 * Maximum number of vertices that a face can have.
	 */
	public static final int MAX_VERTICES = 64;
	
	//The Vertex array containing the vertices of the face.
	private Vertex[] vert = new Vertex[MAX_VERTICES];
	
	//The material of the face
	private Material mat;
	
	//Vertex count
	private int vertexCount;
	
	/**
	 * Class constructor. Initializes a face with no vertices and no material.
	 */
	public Face(){
		vertexCount = 0;
	}
	
	/**
	 * Returns an integer containing the number of vertices the face has.
	 * @return The number of vertices the face has.
	 */
	public int getVertexCount(){
		return vertexCount;
	}
	
	/**
	 * Returns the {@link Material} assigned to the face.
	 * @return The face's {@link Material} the face has.
	 * @see Material
	 */
	public Material getMaterial(){
		return mat;
	}
	
	/**
	 * Method that indicates if the face has a {@link Material} assigned.
	 * @return true if the face has a {@link Material} assigned, false otherwise
	 * @see Material
	 */
	public boolean hasMaterial(){
		if (mat == null)
			return false;
		else
			return true;
	}
	
	/**
	 * Method that adds a {@link Vertex} to the face. If the maximum number of vertices has been 
	 * reached for the face, a {@link Log} entry will be written and the {@link Vertex}
	 * will not be added.
	 * @param v The {@link Vertex} to add.
	 * @see Vertex
	 */
	public void addVertex(Vertex v){
		if (vertexCount < MAX_VERTICES){
			vert[vertexCount] = v;
			vertexCount ++;
		}
		else{
			Log.e("Face error", "Unable to add more than " + Integer.toString(MAX_VERTICES) + " vertices");
		}
	}
	
	/**
	 * Method that return the {@link Vertex} of the face in the selected position. If 
	 * there is not such vertex, a {@link Log} entry will be written null will be returned.
	 * 
	 * @param i The {@link Vertex} index to get.
	 * @return The {@link Vertex} in the selected position, null if it doesn't exist. 
	 * @see Vertex
	 */
	public Vertex getVertex(int i){
		if (i >= MAX_VERTICES)
			Log.w("Face warning", "Requesting null vertex " + Integer.toString(i));
		return vert[i];
	}
	
	/**
	 * Method that return the {@link Vertex} array containing every {@link Vertex} of the face.
	 * 
	 * @return The {@link Vertex} array for the face.
	 * @see Vertex
	 */
	public Vertex[] getVertices(){
		return vert;
	}
	
	/**
	 * Sets the {@link Material} assigned to the face.
	 * @param mat The {@link Material} the face will have.
	 * @see Material
	 */
	public void setMaterial(Material mat){
		this.mat = mat;
	}

	/*
	 * Overridden method. Allows to compare faces, so the ones in the front are pushed back
	 * in the array, and by doing so, being drawn later, beeing in the front. 
	 */
	@Override
	public int compareTo(Face another) {
		float lZ, rZ;
		int i = 0;
		float sum = .0f;
		
		while (i <  this.getVertexCount()){
			sum = sum + this.getVertex(i).getZ();
			i = i + 1;
		}
		lZ = sum / i;
		i = 0;
		sum = 0;
		while (i <  another.getVertexCount()){
			sum = sum + another.getVertex(i).getZ();
			i = i + 1;
		}
		rZ = sum / i;
		i = 0;
		if (lZ < rZ) return -1;
		if (lZ > rZ) return 1;
		return 0;
	}
}
