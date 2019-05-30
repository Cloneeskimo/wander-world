
//GLSL Version
#version 330

//Inputs
in vec2 texCoordFrag;

//Outputs
out vec4 fragColor;

//Material Struct
struct Material {
    vec4 color;
    int hasTexture; //1 -> texture; 0 -> color
};

//Uniforms
uniform Material material;
uniform sampler2D textureSampler;

//Main Function
void main() {

    //set color
    if (material.hasTexture == 1) {
        fragColor = texture(textureSampler, texCoordFrag);
    } else {
        fragColor = material.color;
    }
}
