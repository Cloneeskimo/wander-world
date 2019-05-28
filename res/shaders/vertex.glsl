
//GLSL Version
#version 330

//Layouts
layout (location = 0) in vec3 position; //position, vao slot 0

//Main Function
void main() {
    gl_Position = vec4(position, 1.0f); //set position
}
