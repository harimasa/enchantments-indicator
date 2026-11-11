#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 tex = texture(Sampler0, texCoord0);
    if (tex.a < 0.1) {
        discard;
    }
    float brightness = max(tex.r, max(tex.g, tex.b));

    vec3 fromColor = sqrt(vertexColor.rgb);
    vec3 toColor = sqrt(ColorModulator.rgb);

    float t = clamp(2.0 * gl_FragCoord.y / ScreenSize.y - 0.5, 0.0, 1.0);
    vec3 tint = mix(toColor, fromColor, t);

    float intensity = ColorModulator.a * 2.0;
    float fade = (1.0f - total_fog_value(sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd)) * GlintAlpha;

    fragColor = vec4(brightness * tint * fade * sqrt(intensity), tex.a);
}