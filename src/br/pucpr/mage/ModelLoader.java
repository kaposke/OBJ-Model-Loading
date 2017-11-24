package br.pucpr.mage;


import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.*;
import java.util.ArrayList;


public class ModelLoader {
    public static Model loadModel(String path) throws FileNotFoundException, IOException {
        ArrayList<Integer> vertexIndices = new ArrayList<>();
        ArrayList<Integer> texIndices = new ArrayList<>();
        ArrayList<Integer> normalIndices = new ArrayList<>();
        ArrayList<Vector3f> vertices = new ArrayList<>();
        ArrayList<Vector2f> texCoords = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();

        ArrayList<Vector3f> finalVertices = new ArrayList<>();
        ArrayList<Vector2f> finalTexCoords = new ArrayList<>();
        ArrayList<Vector3f> finalNormals = new ArrayList<>();

        File obj = new File(path);
        FileReader fileReader = new FileReader(obj);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;

        while(true){
            line = bufferedReader.readLine();
            if(line == null){
                break;
            }

            line = line.trim();

            if(line.startsWith("v") && !line.startsWith("vt") && !line.startsWith("vn")){
                Vector3f vertex = new Vector3f();
                String[] values = line.replaceAll("v", "").trim().split("\\s+");
                vertex.x = Float.parseFloat(values[0]);
                vertex.y = Float.parseFloat(values[1]);
                vertex.z = Float.parseFloat(values[2]);

                vertices.add(vertex);
            } else if(line.startsWith("vt")){
                Vector2f texCoord = new Vector2f();
                String[] values = line.replaceAll("vt", "").trim().split("\\s+");
                texCoord.x = Float.parseFloat(values[0]);
                texCoord.y = Float.parseFloat(values[1]);

                texCoords.add(texCoord);
            } else if(line.startsWith("vn")){
                Vector3f normal = new Vector3f();
                String[] values = line.replaceAll("vn", "").trim().split("\\s+");
                normal.x = Float.parseFloat(values[0]);
                normal.y = Float.parseFloat(values[1]);
                normal.z = Float.parseFloat(values[2]);

                normals.add(normal);
            } else if(line.startsWith("f")){
                String[] values = line.replaceAll("f", "")
                        .replaceAll("/", " ").trim().split("\\s+");

                for (int i = 0; i < 3; i++) {
                    vertexIndices.add(Integer.parseInt(values[i * 3]));
                    texIndices.add(Integer.parseInt(values[i * 3 + 1]));
                    normalIndices.add(Integer.parseInt(values[i * 3 + 2]));
                }
            }



            for (int i = 0; i < vertexIndices.size(); i++) {
                int vertexIndex = vertexIndices.get(i);
                finalVertices.add(vertices.get(vertexIndex - 1));
                int texIndex = texIndices.get(i);
                finalTexCoords.add(texCoords.get(texIndex - 1));
                int normalIndex = normalIndices.get(i);
                finalNormals.add(normals.get(normalIndex - 1));
            }

        }
        return new Model(finalVertices,finalTexCoords,finalNormals);
    }
}
