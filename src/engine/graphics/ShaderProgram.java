package engine.graphics;

import engine.graphics.renderable.Material;
import engine.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

//Error Codes Used: 0 - 6

public class ShaderProgram {

    //Data
    private int vertexShaderID;
    private int fragmentShaderID;
    private int programID;
    private Map<String, Integer> uniforms;

    //Constructor
    public ShaderProgram() {
        this.programID = glCreateProgram();
        if (this.programID == 0) Utils.error("Unable to create shader program",
                "engine.graphics.ShaderProgram", 0, Utils.FATAL);
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
        if (glGetProgrami(this.programID, GL_LINK_STATUS) == 0)
            Utils.error("Unable to link shaders: " + glGetProgramInfoLog(this.programID),
                    "engine.graphics.ShaderProgram", 3, Utils.FATAL);

        //detach shaders
        if (this.vertexShaderID != 0) glDetachShader(this.programID, this.vertexShaderID);
        if (this.fragmentShaderID != 0) glDetachShader(this.programID, this.fragmentShaderID);

        //validate program
        glValidateProgram(this.programID);
        if (glGetProgrami(this.programID, GL_VALIDATE_STATUS) == 0)
            Utils.error("Shader validation warning: " + glGetProgramInfoLog(this.programID),
                    "engine.graphics.ShaderProgram", 4, Utils.INFO);
    }

    /**
     * Creates a uniform for this shader program
     * @param name the name of the uniform
     */
    public void createUniform(String name) {

        //find uniform and put its location in the uniforms map
        int location = glGetUniformLocation(this.programID, name);
        if (location < 0) Utils.error("Unable to find uniform '" + name + "'",
                "engine.graphics.ShaderProgram", 5, Utils.FATAL);
        this.uniforms.put(name, location);
    }

    /**
     * Creates a material uniform
     * @param name the name of the uniform
     */
    public void createMaterialUniform(String name) {
        this.createUniform(name + ".hasTexture");
        this.createUniform(name + ".color");
    }

    //Uniform Setting Methods
    public void setUniform(String name, int value) { glUniform1i(this.uniforms.get(name), value); }
    public void setUniform(String name, Vector4f value) { glUniform4f(this.uniforms.get(name), value.x, value.y,
            value.z, value.w); }
    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buf = stack.mallocFloat(16);
            value.get(buf);
            glUniformMatrix4fv(this.uniforms.get(name), false, buf);
        } catch (Exception e) {
            Utils.error("Unable to set uniform '" + name + "': " + e.getMessage(),
                    "engine.graphics.ShaderProgram", 6, Utils.FATAL);
        }
    }
    public void setUniform(String name, Material value) {
        this.setUniform(name + ".hasTexture", value.isTextured() ? 1 : 0);
        this.setUniform(name + ".color", value.getColor());
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
        if (shaderID == 0) Utils.error("Unable to create shader of type " + type,
                "engine.graphics.ShaderProgram", 1, Utils.FATAL);

        //set source and compile shader
        glShaderSource(shaderID, code);
        glCompileShader(shaderID);

        //check compilation result and attach shader
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0)
            Utils.error("Unable to compile shader code: " + glGetShaderInfoLog(shaderID),
                    "engine.graphics.ShaderProgram", 2, Utils.FATAL);
        glAttachShader(this.programID, shaderID);

        //return shader id
        return shaderID;
    }
}
