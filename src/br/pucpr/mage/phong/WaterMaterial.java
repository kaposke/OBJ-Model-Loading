package br.pucpr.mage.phong;

import br.pucpr.mage.FrameBuffer;
import br.pucpr.mage.SimpleMaterial;

public class WaterMaterial extends SimpleMaterial {
    public WaterMaterial() {
        super("/br/pucpr/mage/resource/phong/water");
    }
    
    public void setReflection(FrameBuffer reflection) {
        setTexture("uReflection", reflection.getTexture());
    }
    
    public void setRefraction(FrameBuffer refraction) {
        setTexture("uRefraction", refraction.getTexture());
    }
}
