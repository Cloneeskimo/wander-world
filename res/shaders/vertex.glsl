
//GLSL Version
#version 330

//Layouts
layout (location = 0) in vec3 position; //position, vao slot 0

//uniforms
uniform mat4 projectionMatrix;

//Main Function
void main() {
    gl_Position = projectionMatrix * vec4(position, 1.0f); //project position
}
