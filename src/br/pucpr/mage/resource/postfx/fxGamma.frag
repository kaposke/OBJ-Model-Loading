#version 330

uniform sampler2D uTexture;

in vec2 vTexCoord;
out vec4 outColor;

vec3 ChangeExposure(vec3 col, float exposure, float gamma){
    vec3 mapped = vec3(1.0) - exp(-col * exposure);
    mapped = pow(mapped, vec3(1.0 / gamma));
    return mapped;
}

void main(void) {


    vec3 color = texture(uTexture, vTexCoord).xyz;

    float lumi = (color.r + color.g + color.b)/3.0;

    vec3 highExposure = ChangeExposure(color, 2.0, 1.5);
    vec3 lowExposure = color;

    outColor = vec4(mix(lowExposure,highExposure,sin(1.0-lumi)), 1.0);
}