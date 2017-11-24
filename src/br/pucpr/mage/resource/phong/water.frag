#version 330

uniform sampler2D uRefraction;
uniform sampler2D uReflection;

in vec4 refractPos;
in vec4 reflexPos;

in vec3 vViewPath;

out vec4 outColor;

void main(void) 
{
    vec2 refractCoords = vec2(    
        refractPos.x / refractPos.w / 2.0 + 0.5,
        refractPos.y /refractPos.w / 2.0 + 0.5
    );
    
    vec2 reflexCoords = vec2(    
        reflexPos.x / reflexPos.w / 2.0 + 0.5,
        reflexPos.y /reflexPos.w / 2.0 + 0.5
    );
          
    vec4 refractColor = texture(uRefraction, refractCoords);
    vec4 reflexColor = texture(uReflection, reflexCoords);
        
    float fresnel = dot(normalize(vViewPath), vec3(0,1,0));
    outColor = mix(reflexColor, refractColor, fresnel);
}