
//GLSL Version
#version 330

//Layouts
layout (location = 0) in vec3 position; //position, vao slot 0
layout (location = 1) in vec2 texCoord; //texture coordinate, vao slot 1
layout (location = 2) in vec3 normal; //normal vector, vao slot 2

//Outputs
out vec2 texCoordFrag;      //gets passed through for texture mapping
out vec3 modelViewPosition; //gets passed through for lighting
out vec3 modelViewNormal;   //gets passed through for lighting

//Uniforms
uniform mat4 modelViewMatrix;  //world space -> model view space
uniform mat4 projectionMatrix; //model view space -> screen space

//Main Function
void main() {

    //pass through texture coordinate
    texCoordFrag = texCoord;

    //convert position to modelview
    vec4 mvPos = modelViewMatrix * vec4(position, 1.0);
    modelViewPosition = mvPos.xyz;

    //convert normal to modelview
    modelViewNormal = normalize(modelViewMatrix * vec4(normal, 0.0)).xyz;

    //set position
    gl_Position = projectionMatrix * mvPos; //project position
}
