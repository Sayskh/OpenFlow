precision mediump float;

varying vec2 vTexCoord;

uniform sampler2D uTexture;
uniform float uTime;
uniform float uFrequency;  // Shake frequency
uniform float uAmplitude;  // Shake amplitude (e.g. 0.02)

void main() {
    // Horizontal and vertical wave distortion
    float waveX = sin(vTexCoord.y * uFrequency + uTime * 6.28318) * uAmplitude;
    float waveY = cos(vTexCoord.x * uFrequency + uTime * 6.28318) * uAmplitude;

    vec2 distortedCoord = vTexCoord + vec2(waveX, waveY);

    // Clamp coordinates to prevent border wrapping artifacts
    gl_FragColor = texture2D(uTexture, clamp(distortedCoord, 0.0, 1.0));
}
