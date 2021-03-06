package engine.graphics;

import engine.graphics.lighting.DirectionalLight;
import engine.graphics.lighting.PointLight;
import engine.graphics.lighting.SpotLight;
import engine.graphics.renderable.Material;
import engine.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

//Info Codes Used: 0

public class ShaderProgram {

    //Data
    private int vertexShaderID;
    private int fragmentShaderID;
    private int programID;
    private Map<String, Integer> uniforms;

    //Constructor
    public ShaderProgram() {
        this.programID = glCreateProgram();
        if (this.programID == 0) {
            IllegalStateException e = new IllegalStateException("Unable to create shader program");
            Utils.log(e, "engine.graphics.ShaderProgram");
            throw e;
        }
        this.uniforms = new HashMap<>();
    }

    /**
     * Creates a vertex shader and sets it as this shader program's vertex shader
     * @param resourcePath the path of the vertex shader source code
     */
    public void createVertexShader(String resourcePath) {

        //load code into string and create shader
        String code = Utils.loadResourceIntoString(resourcePath);
        this.vertexShaderID = this.createShader(code, GL_VERTEX_SHADER);
    }

    /**
     * Creates a fragment shader and sets it as this shader program's fragment shader
     * @param resourcePath the path of the fragment shader source code
     */
    public void createFragmentShader(String resourcePath) {

        //load code into string and create shader
        String code = Utils.loadResourceIntoString(resourcePath);
        this.fragmentShaderID = this.createShader(code, GL_FRAGMENT_SHADER);
    }

    /**
     * Links the shader program
     */
    public void link() {

        //link shader and check for errors
        glLinkProgram(this.programID);
        if (glGetProgrami(this.programID, GL_LINK_STATUS) == 0) {
            IllegalStateException e = new IllegalStateException("Unable to link shades");
            Utils.log(e, "engine.graphics.ShaderProgram");
            throw e;
        }

        //detach shaders
        if (this.vertexShaderID != 0) glDetachShader(this.programID, this.vertexShaderID);
        if (this.fragmentShaderID != 0) glDetachShader(this.programID, this.fragmentShaderID);

        //validate program
        glValidateProgram(this.programID);
        if (glGetProgrami(this.programID, GL_VALIDATE_STATUS) == 0)
            Utils.log("Shader validation warning: " + glGetProgramInfoLog(this.programID),
                    "engine.graphics.ShaderProgram", 0, true);
    }

    //Uniform Creation Methods
    public void createUniform(String name) {

        //find uniform and put its location in the uniforms map
        int location = glGetUniformLocation(this.programID, name);
        if (location < 0) {
            IllegalStateException e = new IllegalStateException("Unable to find uniform '" + name + "'");
            Utils.log(e, "engine.graphics.ShaderProgram");
            throw e;
        }
        this.uniforms.put(name, location);
    }
    public void createMaterialUniform(String name) {
        this.createUniform(name + ".hasTexture");
        this.createUniform(name + ".reflectance");
        this.createUniform(name + ".ambientColor");
        this.createUniform(name + ".diffuseColor");
        this.createUniform(name + ".specularColor");
    }
    public void createPointLightUniform(String name) {
        this.createUniform(name + ".color");
        this.createUniform(name + ".position");
        this.createUniform(name + ".intensity");
        this.createUniform(name + ".attenuation.constant");
        this.createUniform(name + ".attenuation.linear");
        this.createUniform(name + ".attenuation.exponent");
    }
    public void createPointLightUniforms(String name, int size) {
        for (int i = 0; i < size; i++) createPointLightUniform(name + "[" + i + "]");
    }
    public void createSpotLightUniform(String name) {
        this.createPointLightUniform(name + ".pointLight");
        this.createUniform(name + ".direction");
        this.createUniform(name + ".cutOff");
    }
    public void createSpotLightUniforms(String name, int size) {
        for (int i = 0; i < size; i++) createSpotLightUniform(name + "[" + i + "]");
    }
    public void createDirectionalLightUniform(String name) {
        this.createUniform(name + ".color");
        this.createUniform(name + ".direction");
        this.createUniform(name + ".intensity");
    }

    //Uniform Setting Methods
    public void setUniform(String name, int value) { glUniform1i(this.uniforms.get(name), value); }
    public void setUniform(String name, float value) { glUniform1f(this.uniforms.get(name), value); }
    public void setUniform(String name, Vector3f value) { glUniform3f(this.uniforms.get(name), value.x, value.y,
            value.z); }
    public void setUniform(String name, Vector4f value) { glUniform4f(this.uniforms.get(name), value.x, value.y,
            value.z, value.w); }
    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buf = stack.mallocFloat(16);
            value.get(buf);
            glUniformMatrix4fv(this.uniforms.get(name), false, buf);
        } catch (Exception e) {
            Utils.log(e, "engine.graphics.ShaderProgram");
            e.printStackTrace();
        }
    }
    public void setUniform(String name, Material value) {
        this.setUniform(name + ".hasTexture", value.isTextured() ? 1 : 0);
        this.setUniform(name + ".reflectance", value.getReflectance());
        this.setUniform(name + ".ambientColor", value.getAmbientColor());
        this.setUniform(name + ".diffuseColor", value.getDiffuseColor());
        this.setUniform(name + ".specularColor", value.getSpecularColor());
    }
    public void setUniform(String name, PointLight value) {
        this.setUniform(name + ".color", value.getColor());
        this.setUniform(name + ".position", value.getPosition());
        this.setUniform(name + ".intensity", value.getIntensity());
        PointLight.Attenuation att = value.getAttenuation();
        setUniform(name + ".attenuation.constant", att.getConstant());
        setUniform(name + ".attenuation.linear", att.getLinear());
        setUniform(name + ".attenuation.exponent", att.getExponent());
    }
    public void setUniform(String name, PointLight[] value) {
        int count = value != null ? value.length : 0;
        for (int i = 0; i < count; i++) setUniform(name + "[" + i + "]", value[i]);
    }
    public void setUniform(String name, SpotLight value) {
        this.setUniform(name + ".pointLight", value.getPointLight());
        this.setUniform(name + ".direction", value.getDirection());
        this.setUniform(name + ".cutOff", value.getCutOff());
    }
    public void setUniform(String name, SpotLight[] value) {
        int count = value != null ? value.length : 0;
        for (int i = 0; i < count; i++) setUniform(name + "[" + i + "]", value[i]);
    }
    public void setUniform(String name, DirectionalLight value) {
        this.setUniform(name + ".color", value.getColor());
        this.setUniform(name + ".direction", value.getDirection());
        this.setUniform(name + ".intensity", value.getIntensity());
    }

    //Binding/Unbinding Methods
    public void bind() { glUseProgram(this.programID); }
    public void unbind() { glUseProgram(0); }

    //Cleanup Method
    public void cleanup() {
        this.unbind(); //unbind shader
        if (this.programID != 0) glDeleteProgram(this.programID); //delete program
    }

    /**
     * Creates a shader
     * @param code a string containing all of the shader code
     * @param type the type of shader, use one of OpenGL's constants for this
     * @return the id of the shader
     */
    private int createShader(String code, int type) {

        //create shader
        int shaderID = glCreateShader(type);
        if (shaderID == 0) {
            IllegalStateException e = new IllegalStateException("Unable to create shader of type " + type);
            Utils.log(e, "engine.graphics.ShaderProgram");
            throw e;
        }

        //set source and compile shader
        glShaderSource(shaderID, code);
        glCompileShader(shaderID);

        //check compilation result and attach shader
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) {
            IllegalStateException e = new IllegalStateException("Unable to compile shader code: " +
                    glGetShaderInfoLog(shaderID));
            Utils.log(e, "engine.graphics.ShaderProgram");
            throw e;
        }
        glAttachShader(this.programID, shaderID);

        //return shader id
        return shaderID;
    }
}
