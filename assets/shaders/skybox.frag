#version 330 core

uniform samplerCube u_cubemap; // Cubemap texture
varying vec3 v_texCoords;

void main() {
    gl_FragColor = textureCube(u_cubemap, v_texCoords); // Sample from cubemap
}
