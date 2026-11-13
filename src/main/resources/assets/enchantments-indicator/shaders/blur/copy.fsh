#version 150

uniform sampler2D uTexture;
uniform vec2 uRectMin;
uniform vec2 uRectSize;
uniform float uCornerRadius;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 localPos = gl_FragCoord.xy - uRectMin;
    vec2 halfSize = uRectSize * 0.5;
    float radius = clamp(uCornerRadius, 0.0, min(halfSize.x, halfSize.y));
    vec2 q = abs(localPos - halfSize) - (halfSize - vec2(radius));
    float signedDistance = length(max(q, vec2(0.0))) + min(max(q.x, q.y), 0.0) - radius;

    if (signedDistance > 0.0) {
        discard;
    }

    fragColor = texture(uTexture, texCoord);
}
