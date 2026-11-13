#version 150

uniform sampler2D uTexture;
uniform vec2 uTexelSize;
uniform vec2 uDirection;
uniform float uRadius;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 stepDirection = uDirection * uTexelSize * (uRadius / 4.0);

    vec4 color = texture(uTexture, texCoord) * 0.20416369;
    color += texture(uTexture, texCoord + stepDirection * 1.0) * 0.18017382;
    color += texture(uTexture, texCoord - stepDirection * 1.0) * 0.18017382;
    color += texture(uTexture, texCoord + stepDirection * 2.0) * 0.12383154;
    color += texture(uTexture, texCoord - stepDirection * 2.0) * 0.12383154;
    color += texture(uTexture, texCoord + stepDirection * 3.0) * 0.06628225;
    color += texture(uTexture, texCoord - stepDirection * 3.0) * 0.06628225;
    color += texture(uTexture, texCoord + stepDirection * 4.0) * 0.02763055;
    color += texture(uTexture, texCoord - stepDirection * 4.0) * 0.02763055;

    fragColor = color;
}