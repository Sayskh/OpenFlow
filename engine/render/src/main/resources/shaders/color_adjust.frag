precision mediump float;

varying vec2 vTexCoord;

uniform sampler2D uTexture;
uniform float uBrightness;   // Default 0.0 [-1.0 .. 1.0]
uniform float uContrast;     // Default 1.0 [0.0 .. 2.0]
uniform float uSaturation;   // Default 1.0 [0.0 .. 2.0]
uniform float uExposure;     // Default 0.0 [-2.0 .. 2.0]
uniform float uGamma;        // Default 1.0 [0.1 .. 3.0]

const vec3 W = vec3(0.2125, 0.7154, 0.0721);

void main() {
    vec4 color = texture2D(uTexture, vTexCoord);
    vec3 rgb = color.rgb;

    // Exposure
    rgb = rgb * pow(2.0, uExposure);

    // Brightness
    rgb = rgb + vec3(uBrightness);

    // Contrast
    rgb = (rgb - 0.5) * uContrast + 0.5;

    // Saturation
    float luminance = dot(rgb, W);
    rgb = mix(vec3(luminance), rgb, uSaturation);

    // Gamma correction
    rgb = pow(clamp(rgb, 0.0, 1.0), vec3(1.0 / max(uGamma, 0.0001)));

    gl_FragColor = vec4(rgb, color.a);
}
