precision mediump float;

varying vec2 vTexCoord;

uniform sampler2D uTexture;
uniform vec2 uCenter;       // Center of radial blur [0.0 .. 1.0]
uniform float uBlurStrength; // Blur intensity (e.g. 0.05)
const int SAMPLES = 10;

void main() {
    vec2 dir = uCenter - vTexCoord;
    vec4 sum = vec4(0.0);

    for (int i = 0; i < SAMPLES; i++) {
        float scale = 1.0 - uBlurStrength * (float(i) / float(SAMPLES - 1));
        vec2 sampleCoord = (vTexCoord - uCenter) * scale + uCenter;
        sum += texture2D(uTexture, clamp(sampleCoord, 0.0, 1.0));
    }

    gl_FragColor = sum / float(SAMPLES);
}
