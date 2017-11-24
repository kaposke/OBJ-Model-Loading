package br.pucpr.mage.postfx;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import br.pucpr.mage.FrameBuffer;
import br.pucpr.mage.Material;
import br.pucpr.mage.Shader;
import org.joml.Matrix3f;

public class PostFXMaterial implements Material {
    private FrameBuffer frameBuffer;
    private Shader shader;
    private Matrix3f kernel;
    private float intensity;
    private float gamma;
    
    public PostFXMaterial(String effectName, FrameBuffer fb) {
        shader = Shader.loadProgram("/br/pucpr/mage/resource/postfx/fxVertexShader.vert",
                effectName + ".frag");
        frameBuffer = fb;
        intensity = 1.0f;
        gamma = 1.0f;
    }

    public PostFXMaterial(String effectName, FrameBuffer fb, Matrix3f kernel) {
        this(effectName,fb);
        this.kernel = kernel;
    }
    
    public static PostFXMaterial defaultPostFX(String name, FrameBuffer fb) {
        return new PostFXMaterial("/br/pucpr/mage/resource/postfx/" + name, fb);
    }

    public static PostFXMaterial defaultPostFX(String name, FrameBuffer fb, Matrix3f kernel) {
        return new PostFXMaterial("/br/pucpr/mage/resource/postfx/" + name, fb, kernel);
    }

    public void setIntensity(float intensity) {
        this.intensity = (intensity < 0) ? 0 : (intensity > 1) ? 1 : intensity;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setFrameBuffer(FrameBuffer frameBuffer) {
        this.frameBuffer = frameBuffer;
    }

    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }

    @Override
    public void setShader(Shader shader) {
        this.shader = shader;
    }

    @Override
    public Shader getShader() {
        return shader;
    }

    @Override
    public void apply() {
        shader.setUniform("width", frameBuffer.getWidth());
        shader.setUniform("height", frameBuffer.getHeight());
        shader.setUniform("kernelMatrix", kernel);
        shader.setUniform("kernelIntensity", intensity);
        shader.setUniform("uGamma", gamma);
        
        glActiveTexture(GL_TEXTURE0);
        frameBuffer.getTexture().bind();
        shader.setUniform("uTexture", 0);
    }
}
