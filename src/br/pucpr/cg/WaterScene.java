package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.IOException;

import br.pucpr.mage.*;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import br.pucpr.mage.phong.DirectionalLight;
import br.pucpr.mage.phong.MultiTextureMaterial;
import br.pucpr.mage.phong.SkyMaterial;
import br.pucpr.mage.phong.WaterMaterial;
import br.pucpr.mage.postfx.PostFXMaterial;

public class WaterScene implements Scene {
    private static final String PATH = "E:/Documentos/img/opengl/";
    private static final float WATER_H = 11.0f;
    
    private Keyboard keys = Keyboard.getInstance();

    //Dados ddo modelo
    Model model;
    Mesh lulaMolusco;
    SimpleMaterial simpleMaterial;

    //Dados da cena
    private Camera camera = new Camera();
    private DirectionalLight light;

    //Dados da malha
    private Mesh mesh;
    private MultiTextureMaterial material; 
    
    //Dados do skydome
    private Mesh skydome;
    private SkyMaterial skyMaterial;
    
    //Dados da água
    private Mesh water;
    private WaterMaterial waterMaterial;
    
    private FrameBuffer refractionFB;
    private FrameBuffer reflectionFB;
    
    private float lookX = 0.0f;
    private float lookY = 0.0f;

    private Mesh canvas;
    private FrameBuffer fb;
    private PostFXMaterial postFX;

    private Matrix3f sharpenKernel = new Matrix3f(
            0.0f,-1.0f,0.0f,
            -1.0f,5.0f,-1.0f,
            0.0f,-1.0f,0.0f);

    private Matrix3f borderKernel = new Matrix3f(
            -1.0f,0.0f,1.0f,
            -2.0f,0.0f,2.0f,
            -1.0f,0.0f,1.0f);

    
    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glPolygonMode(GL_FRONT_FACE, GL_LINE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        camera.getPosition().set(0.0f, 00.0f, 5.0f);
        
        light = new DirectionalLight(
                new Vector3f( 1.0f, -1.0f, -1.0f),    //direction
                new Vector3f( 0.1f,  0.1f,  0.1f), //ambient
                new Vector3f( 1.0f,  1.0f,  1.0f),    //diffuse
                new Vector3f( 1.0f,  1.0f,  1.0f));   //specular

        //Carga do Modelo
        try {
            model = ModelLoader.loadModel(PATH + "bora.obj");
        } catch (IOException ex) {
            System.out.println("Failed to load model with the following error: " + ex);
        }
        lulaMolusco = MeshFactory.createModel(model);
        //simpleMaterial = new SimpleMaterial("phong");

        //Carga do terreno
        try {
            mesh = MeshFactory.loadTerrain(new File(PATH + "heights/river.png"), 0.4f, 3);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
        material = new MultiTextureMaterial(
                new Vector3f(1.0f, 1.0f, 1.0f), //ambient
                new Vector3f(0.9f, 0.9f, 0.9f), //diffuse
                new Vector3f(0.0f, 0.0f, 0.0f), //specular
                0.0f);                          //specular power
        
        material.setTextures(
                new Texture(PATH + "textures/sand.png"),
                new Texture(PATH + "textures/grass.png"),
                new Texture(PATH + "textures/rock.png"),
                new Texture(PATH + "textures/snow.png")
                );
        
        //Carga do Skydome
        skydome = MeshFactory.createSphere(20, 20);
        
        skyMaterial = new SkyMaterial();
        skyMaterial.setCloud1(new Texture(PATH + "textures/cloud1.png"));
        skyMaterial.setCloud2(new Texture(PATH + "textures/cloud2.png"));
        
        //Carga da água
        water = MeshFactory.createXZSquare(400, 300, WATER_H);
        waterMaterial = new WaterMaterial();
        
        refractionFB = FrameBuffer.forCurrentViewport();
        waterMaterial.setRefraction(refractionFB);
        
        reflectionFB = FrameBuffer.forCurrentViewport();
        waterMaterial.setReflection(reflectionFB);
        
        //Carga do canvas para o PostFX
        canvas = MeshFactory.createCanvas();
        fb = FrameBuffer.forCurrentViewport();
        postFX = PostFXMaterial.defaultPostFX("fxGamma", fb);
    }

    @Override
    public void update(float secs) {
        float SPEED = 1000 * secs;
        
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }
        
        if (keys.isDown(GLFW_KEY_LEFT_SHIFT)) {
            SPEED *= 10;
        }
        
        if (keys.isDown(GLFW_KEY_LEFT)) {
            lookX -= SPEED * secs;
        } else if (keys.isDown(GLFW_KEY_RIGHT)) {
            lookX += SPEED * secs;
        }
        
        if (keys.isDown(GLFW_KEY_UP)) {
            lookY += SPEED * secs;
        } else if (keys.isDown(GLFW_KEY_DOWN)) {
            lookY -= SPEED * secs;
        }    
        
        if (keys.isDown(GLFW_KEY_SPACE)) {
            lookX = 0;
            lookY = 0;              
        }

        if (keys.isDown(GLFW_KEY_KP_SUBTRACT)) {
            postFX.setIntensity(postFX.getIntensity() - 0.01f);
        }

        if (keys.isDown(GLFW_KEY_KP_ADD)) {
            postFX.setIntensity(postFX.getIntensity() + 0.01f);
        }

        camera.getTarget().set(lookX, lookY, 0.0f);
        skyMaterial.addTime(secs);
    }

    public void drawSky(Matrix4f viewMatrix) {        
        glDisable(GL_DEPTH_TEST);    
        Shader shader = skyMaterial.getShader();
        shader.bind()
            .setUniform("uProjection", camera.getProjectionMatrix())
            .setUniform("uView", viewMatrix)            
        .unbind();
                
        skydome.setUniform("uWorld", new Matrix4f().scale(300));
        skydome.draw(skyMaterial);
        glEnable(GL_DEPTH_TEST);
    }
    
    public void drawSky() {
        drawSky(camera.getViewMatrix());
    }
    
    public void drawTerrain(Matrix4f viewMatrix) {
        Shader shader = material.getShader();
        shader.bind()
            .setUniform("uProjection", camera.getProjectionMatrix())
            .setUniform("uView", viewMatrix)
            .setUniform("uCameraPosition", camera.getPosition());        
        light.apply(shader);        
        shader.unbind();
    
        mesh.setUniform("uWorld", new Matrix4f().rotateY((float)Math.toRadians(85)));
        mesh.draw(material);
    }
    
    public void drawTerrain() {
        drawTerrain(camera.getViewMatrix());
    }
    
    public void drawRefraction() {
        material.setClippingPlane(new Vector4f(0, -1, 0, WATER_H));
        refractionFB.bind();   
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            drawTerrain();        
        refractionFB.unbind();
        material.setClippingPlane(null);
    }
        
    public Matrix4f getReflexView() {
        Vector3f reflPos = new Vector3f(camera.getPosition());
        reflPos.y = -reflPos.y + WATER_H * 2;        
        
        Matrix4f reflexView = new Matrix4f();
        reflexView.lookAt(
                reflPos,                                   //Onde está 
                new Vector3f(lookX, -lookY + WATER_H * 2, 0),  //Para onde olha
                new Vector3f(0, 1, 0));                    //Onde o céu está
        return reflexView;
    }
    
    public void drawReflection() {
        Matrix4f reflexView = getReflexView();
        material.setClippingPlane(new Vector4f(0, 1, 0, -WATER_H + 0.5f));
        reflectionFB.bind();            
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);            
            drawSky(reflexView);            
            drawTerrain(reflexView);
        reflectionFB.unbind();
        material.setClippingPlane(null);        
    }
    
    public void drawWater() {
        Shader shader = waterMaterial.getShader();
        shader.bind()
            .setUniform("uProjection", camera.getProjectionMatrix())
            .setUniform("uView", camera.getViewMatrix())
            .setUniform("uReflexView", getReflexView())
            .setUniform("uCameraPosition", camera.getPosition());
        shader.unbind();        
        water.setUniform("uWorld", new Matrix4f());
        water.draw(waterMaterial);
        "".contains("a");
    }

    public void drawModel(){
        Shader shader = material.getShader();
        shader.bind()
                .setUniform("uProjection", camera.getProjectionMatrix())
                .setUniform("uView", camera.getViewMatrix())
                .setUniform("uCameraPosition", camera.getPosition());
        shader.unbind();
        lulaMolusco.setUniform("uWorld", new Matrix4f());
        lulaMolusco.draw(material);
    }
    
    public void drawScene() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawModel();
//        drawSky();
//        drawTerrain();
//        drawWater();
    }
    
    @Override
    public void draw() {
        drawRefraction();
        drawReflection();

        fb.bind();
        drawScene();
        fb.unbind();
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);        
        canvas.draw(postFX);
    }

    @Override
    public void deinit() {
    }

    public static void main(String[] args) {        
        new Window(new WaterScene(), "Water", 1024, 748).show();
    }
}
