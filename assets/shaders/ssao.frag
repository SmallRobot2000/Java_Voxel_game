#version 330 core

varying vec2 v_texCoord;

uniform sampler2D u_depthTexture; // Depth texture from framebuffer
uniform vec2 u_resolution;       // Screen resolution
uniform float u_radius;          // Sampling radius

const int SAMPLES = 16;          // Number of samples for AO calculation

vec3 getViewPosition(vec2 uv) {
    float depth = texture2D(u_depthTexture, uv).r;
    return vec3(uv * 2.0 - 1.0, depth); // Convert UV to view space position
}

float calculateOcclusion(vec3 pos, vec3 samplePos) {
    vec3 delta = samplePos - pos;
    float distance = length(delta);
    return max(0.0, 1.0 - distance / u_radius); // Simple occlusion formula
}

void main() {
    vec3 pos = getViewPosition(v_texCoord);
    float occlusion = 0.0;

    for (int i = 0; i < SAMPLES; i++) {
        vec2 offset = vec2(
            cos(float(i) * 6.283185 / float(SAMPLES)),
            sin(float(i) * 6.283185 / float(SAMPLES))
        ) * u_radius / u_resolution;

        vec3 samplePos = getViewPosition(v_texCoord + offset);
        occlusion += calculateOcclusion(pos, samplePos);
    }

    occlusion = 1.0 - (occlusion / float(SAMPLES)); // Normalize occlusion value
    gl_FragColor = vec4(vec3(occlusion), 1.0);      // Output AO as grayscale
}
