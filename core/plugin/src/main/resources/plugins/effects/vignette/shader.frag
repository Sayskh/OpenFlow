#version 300 es
precision mediump float;

in vec2 vTexCoord;
out vec4 fragColor;

uniform sampler2D uTexture;
uniform float radius;
uniform float softness;

void main() {
    vec4 color = texture(uTexture, vTexCoord);
    vec2 position = vTexCoord - vec2(0.5);
    float dist = length(position);

    float vignette = smoothstep(radius, radius - softness, dist);
    fragColor = vec4(color.rgb * vignette, color.a);
}
