package br.pucpr.mage;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Model {
    ArrayList<Vector3f> vertices, normals;
    ArrayList<Vector2f> texCoords;

    public Model(Vector3f[] vertices, Vector2f[] texCoords, Vector3f[] normals){
        this.vertices = new ArrayList<Vector3f>();
        this.texCoords = new ArrayList<Vector2f>();
        this.normals = new ArrayList<Vector3f>();

        for (int i = 0; i < this.vertices.size(); i++) {
            this.vertices.add(vertices[i]);
            this.texCoords.add(texCoords[i]);
            this.normals.add(normals[i]);
        }
    }
    public Model(ArrayList<Vector3f> vertices, ArrayList<Vector2f> texCoords,ArrayList<Vector3f> normals){
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.normals = normals;
    }

    public void setVertices(ArrayList<Vector3f> vertices) {
        this.vertices = vertices;
    }

    public void setTexCoords(ArrayList<Vector2f> texCoords) {
        this.texCoords = texCoords;
    }

    public void setNormals(ArrayList<Vector3f> normals) {
        this.normals = normals;
    }

    public ArrayList<Vector3f> getVertices() {
        return vertices;
    }

    public ArrayList<Vector2f> getTexCoords() {
        return texCoords;
    }

    public ArrayList<Vector3f> getNormals() {
        return normals;
    }
}
