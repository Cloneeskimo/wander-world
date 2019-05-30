
//GLSL Version
#version 330

//Light Count Constants
const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

//Inputs
in vec2 texCoordFrag;
in vec3 modelViewPosition;
in vec3 modelViewNormal;

//Outputs
out vec4 fragColor;

//Attenuation Struct
struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

//PointLight Struct
struct PointLight {
    vec3 color;
    vec3 position;
    float intensity; //0.0f - 1.0f;
    Attenuation attenuation;
};

//SpotLight Struct
struct SpotLight {
    PointLight pointLight;
    vec3 direction;
    float cutOff;
};

//DirectionalLight Struct
struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

//Material Struct
struct Material {
    vec4 ambientColor;
    vec4 diffuseColor;
    vec4 specularColor;
    float reflectance;
    int hasTexture; //1 -> texture; 0 -> color
};

//Misc Uniforms
uniform Material material;          //the material characteristics to color along
uniform sampler2D textureSampler;   //the texture unit/bank of the graphics card to use
uniform vec3 ambientLight;          //a color which will affect every fragment in the same way
uniform float specularPower;        //exponent used in specular light calculation

//Light Object Uniforms
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform DirectionalLight directionalLight;

/**
  * Global variables which define the base color for all light calculations will be
  * the texture color if hasTexture is true, or the material's predefined color otherwise
  */
vec4 ambientC;  //ambient color
vec4 diffuseC;  //diffuse color
vec4 specularC; //specular color

/**
  * Sets up the base colors above
  */
void setupColors(Material material, vec2 texCoord) {

    //if material is textured, set colors to texture pixel, otherwise set to material colors
    if (material.hasTexture == 1) {
        ambientC = diffuseC = specularC = texture(textureSampler, texCoord);
    } else {
        ambientC = material.ambientColor;
        diffuseC = material.diffuseColor;
        specularC = material.specularColor;
    }
}

/**
  * Calculates the final light color using diffuse and specular light, given the defined parameters
  */
vec4 calcLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDirection, vec3 normal) {

    //create diffuse and specular vectors
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    //calculate diffuse light
    float diffuseFactor = max(dot(normal, toLightDirection), 0.0);
    diffuseColor = diffuseC * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

    //calculate specular light
    vec3 camDirection = normalize(-position);
    vec3 fromLightDirection = -toLightDirection;
    vec3 reflectedLight = normalize(reflect(fromLightDirection, normal));
    float specularFactor = max(dot(camDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColor = specularC * lightIntensity * specularFactor * material.reflectance * vec4(lightColor, 1.0f);

    //combine and return
    return (diffuseColor + specularColor);
}

/**
  * Calculates the light effect of a PointLight
  */
vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) {

    //get base light color
    vec3 lightDirection = light.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec4 lightColor = calcLightColor(light.color, light.intensity, position, toLightDirection, normal);

    //apply attenuation
    float distance = length(lightDirection);
    float attenuation = light.attenuation.constant + light.attenuation.linear * distance + light.attenuation.exponent * (distance * distance);
    return lightColor / attenuation;
}

/**
  * Calculates the light effect of a SpotLight
  */
vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) {

    //get vector from light to fragment
    vec3 lightDirection = light.pointLight.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec3 fromLightDrection = -toLightDirection;
    float spotAlfa = dot(fromLightDrection, normalize(light.direction));

    //apply lighting if within cone of spot light
    vec4 lightColor = vec4(0, 0, 0, 0);
    if (spotAlfa > light.cutOff) {
        lightColor = calcPointLight(light.pointLight, position, normal);
        lightColor *= (1.0 - (1.0 - spotAlfa)/(1.0 - light.cutOff));
    }
    return lightColor;
}

/**
  * Calculates the light effect of a DirectionalLight
  */
vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

//Main Function
void main() {

    //setup base colors
    setupColors(material, texCoordFrag);

    //account for DirectionalLight
    vec4 diffSpecColor = calcDirectionalLight(directionalLight, modelViewPosition, modelViewNormal);

    //account for PointLights
    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (pointLights[i].intensity > 0) { //if there is a PointLight at index i of the array
            diffSpecColor += calcPointLight(pointLights[i], modelViewPosition, modelViewNormal);
        }
    }

    //account for SpotLights
    for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
        if (spotLights[i].pointLight.intensity > 0) {
            diffSpecColor += calcSpotLight(spotLights[i], modelViewPosition, modelViewNormal);
        }
    }

    //account for ambient light
    fragColor = ambientC * vec4(ambientLight, 1) + diffSpecColor;
}
