#version 330

in vec3 aPosition;
				
uniform mat4 uWorld;
uniform mat4 uView;
uniform mat4 uReflexView;
uniform mat4 uProjection;

uniform vec3 uCameraPosition;

out vec4 refractPos;
out vec4 reflexPos;
out vec3 vViewPath;

void main(void) 
{
	vec4 worldPos = uWorld * vec4(aPosition, 1.0);
		
    refractPos = uProjection * uView * worldPos;    
    reflexPos = uProjection * uReflexView * worldPos;
    gl_Position = refractPos;
    
    vViewPath = uCameraPosition - worldPos.xyz;	    	
}