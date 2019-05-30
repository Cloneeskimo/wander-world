
//GLSL Version
#version 330

//Layouts
layout (location = 0) in vec3 position; //position, vao slot 0
layout (location = 1) in vec2 texCoord; //texture coordinate, vao slot 1
layout (location = 2) in vec3 normal; //normal vector, vao slot 2

//Outputs
out vec2 texCoordFrag;

//Uniforms
uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

//Main Function
void main() {

    //set position and pass through texture coordinates
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0f); //project position
    texCoordFrag = texCoord;
}
