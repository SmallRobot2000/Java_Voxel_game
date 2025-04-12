#version 330 core

varying vec2 v_texCoord;

uniform sampler2D u_sceneTexture; // Original scene color
uniform sampler2D u_ssaoTexture;  // SSAO occlusion map

void main() {
    vec3 sceneColor = texture2D(u_sceneTexture, v_texCoord).rgb;
    float occlusion = texture2D(u_ssaoTexture, v_texCoord).r;

    // Darken occluded areas
    vec3 finalColor = sceneColor * occlusion;
    gl_FragColor = vec4(finalColor, 1.0);
}
