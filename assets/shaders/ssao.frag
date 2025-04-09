#version 330 core
varying vec2 v_texCoord;

uniform sampler2D u_depthTexture;
uniform vec2 u_resolution;
uniform float u_radius;
uniform float u_near;
uniform float u_far;
uniform float u_scale;

const int SAMPLES = 16;

float linearizeDepth(float depth) {
    float z = depth * 2.0 - 1.0;
    return (2.0 * u_near * u_far) / (u_far + u_near - z * (u_far - u_near));
}

vec3 getViewPosition(vec2 uv) {
    float depth = linearizeDepth(texture2D(u_depthTexture, uv).r);
    return vec3(uv * 2.0 - 1.0, depth);
}

float calculateOcclusion(vec3 pos, vec3 samplePos) {
    vec3 delta = samplePos - pos;
    float distance = length(delta);
    float bias = 0.025;
    if (samplePos.z > pos.z + bias) return 0.0;
    return max(0.0, 1.0 - distance / u_radius);
}

void main() {
    vec3 pos = getViewPosition(v_texCoord);
    float occlusion = 0.0;

    for (int i = 0; i < SAMPLES; i++) {
        vec2 offset = vec2(
        cos(float(i) * 6.283185 / float(SAMPLES)),
        sin(float(i) * 6.283185 / float(SAMPLES))
        ) * u_scale;

        vec3 samplePos = getViewPosition(v_texCoord + offset);
        occlusion += calculateOcclusion(pos, samplePos);
    }

    occlusion = 1.0 - (occlusion / float(SAMPLES));
    gl_FragColor = vec4(vec3(occlusion), 1.0);
}
