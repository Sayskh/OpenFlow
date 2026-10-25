#version 300 es
precision mediump float;

in vec2 vTexCoord;
out vec4 fragColor;

uniform sampler2D uTexture;
uniform float offset;
uniform float angle;

#define PI 3.14159265359

void main() {
    float rad = radians(angle);
    vec2 dir = vec2(cos(rad), sin(rad)) * offset;

    float r = texture(uTexture, vTexCoord + dir).r;
    float g = texture(uTexture, vTexCoord).g;
    float b = texture(uTexture, vTexCoord - dir).b;
    float a = texture(uTexture, vTexCoord).a;

    fragColor = vec4(r, g, b, a);
}
