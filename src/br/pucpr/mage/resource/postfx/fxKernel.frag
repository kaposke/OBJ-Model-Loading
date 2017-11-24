#version 330

uniform sampler2D uTexture;
uniform int width;
uniform int height;
uniform float kernelIntensity;

uniform mat3 kernelMatrix;
in vec2 vTexCoord;
out vec4 outColor;

void main(void)
{
    float dx = 1.0 / width;
    float dy = 1.0 / height;

	float r = 0, g = 0, b = 0;

	for(int y = 0; y < 3; y++) {
	    for(int x = 0; x < 3; x++) {
            vec4 color = texture(uTexture, vTexCoord + vec2(dx * x-1, dy * y-1));
            r += color.r * kernelMatrix[y][x];
            g += color.g * kernelMatrix[y][x];
            b += color.b * kernelMatrix[y][x];
        }
	}

    outColor = mix(texture(uTexture,vTexCoord), vec4(r,g,b,1.0f), kernelIntensity);
}