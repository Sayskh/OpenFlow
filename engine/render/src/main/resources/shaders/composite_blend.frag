precision mediump float;

varying vec2 vTexCoord;

uniform sampler2D uBaseTexture;
uniform sampler2D uOverlayTexture;
uniform float uOpacity;
// Blend mode: 0 = Normal, 1 = Multiply, 2 = Screen, 3 = Overlay, 4 = Darken, 5 = Lighten
uniform int uBlendMode;

vec3 blendOverlay(vec3 base, vec3 blend) {
    return mix(
        2.0 * base * blend,
        1.0 - 2.0 * (1.0 - base) * (1.0 - blend),
        step(vec3(0.5), base)
    );
}

void main() {
    vec4 baseColor = texture2D(uBaseTexture, vTexCoord);
    vec4 overlayColor = texture2D(uOverlayTexture, vTexCoord);

    vec3 blended = baseColor.rgb;

    if (uBlendMode == 0) {
        // Normal
        blended = overlayColor.rgb;
    } else if (uBlendMode == 1) {
        // Multiply
        blended = baseColor.rgb * overlayColor.rgb;
    } else if (uBlendMode == 2) {
        // Screen
        blended = 1.0 - (1.0 - baseColor.rgb) * (1.0 - overlayColor.rgb);
    } else if (uBlendMode == 3) {
        // Overlay
        blended = blendOverlay(baseColor.rgb, overlayColor.rgb);
    } else if (uBlendMode == 4) {
        // Darken
        blended = min(baseColor.rgb, overlayColor.rgb);
    } else if (uBlendMode == 5) {
        // Lighten
        blended = max(baseColor.rgb, overlayColor.rgb);
    }

    vec3 finalRgb = mix(baseColor.rgb, blended, overlayColor.a * uOpacity);
    float finalAlpha = max(baseColor.a, overlayColor.a * uOpacity);

    gl_FragColor = vec4(finalRgb, finalAlpha);
}
