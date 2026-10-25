/**
 * Inertia bounce expression calculation.
 * Returns interpolated value with decaying sinusoidal bounce.
 */
function evaluate(t, value, velocity, amp, freq, decay) {
    if (t <= 0.0) {
        return value;
    }
    var w = freq * Math.PI * 2.0;
    var bounce = (velocity * amp) * Math.sin(t * w) / Math.exp(decay * t);
    return value + bounce;
}
