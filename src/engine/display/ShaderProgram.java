package engine.display;

import engine.utils.Utils;

import static org.lwjgl.opengl.GL20.*;

//Error Codes Used: 0 - 4

public class ShaderProgram {

    //Data
    private int vertexShaderID;
    private int fragmentShaderID;
    private int programID;

    //Constructor
    public ShaderProgram() {
        this.programID = glCreateProgram();
        if (this.programID == 0) Utils.error("Unable to create shader program",
                "engine.display.ShaderProgram", 0, Utils.FATAL);
    }

    //Public Vertex Shader Creation Method
    public void createVertexShader(String resourcePath) {

        //load code into string and create shader
        String code = Utils.loadResourceIntoString(resourcePath);
        this.vertexShaderID = this.createShader(code, GL_VERTEX_SHADER);
    }

    //Public Fragment Shader Creation Method
    public void createFragmentShader(String resourcePath) {

        //load code into string and create shader
        String code = Utils.loadResourceIntoString(resourcePath);
        this.fragmentShaderID = this.createShader(code, GL_FRAGMENT_SHADER);
    }

    //Shader Linking Method
    public void link() {

        //link shader and check for errors
        glLinkProgram(this.programID);
        if (glGetProgrami(this.programID, GL_LINK_STATUS) == 0)
            Utils.error("Unable to link shaders: " + glGetProgramInfoLog(this.programID),
                    "engine.display.ShaderProgram", 3, Utils.FATAL);

        //detach shaders
        if (this.vertexShaderID != 0) glDetachShader(this.programID, this.vertexShaderID);
        if (this.fragmentShaderID != 0) glDetachShader(this.programID, this.fragmentShaderID);

        //validate program
        glValidateProgram(this.programID);
        if (glGetProgrami(this.programID, GL_VALIDATE_STATUS) == 0)
            Utils.error("Shader validation warning: " + glGetProgramInfoLog(this.programID),
                    "engine.display.ShaderProgram", 4, Utils.INFO);
    }

    //Binding/Unbinding Methods
    public void bind() { glUseProgram(this.programID); }
    public void unbind() { glUseProgram(0); }

    //Cleanup Method
    public void cleanup() {
        this.unbind(); //unbind shader
        if (this.programID != 0) glDeleteProgram(this.programID); //delete program
    }

    //Private Shader Creation Method
    /**
     * @param code a string containing all of the shader code
     * @param type the type of shader, use one of OpenGL's constants for this
     * @return the id of the shader
     */
    private int createShader(String code, int type) {

        //create shader
        int shaderID = glCreateShader(type);
        if (shaderID == 0) Utils.error("Unable to create shader of type " + type,
                "engine.display.ShaderProgram", 1, Utils.FATAL);

        //set source and compile shader
        glShaderSource(shaderID, code);
        glCompileShader(shaderID);

        //check compilation result and attach shader
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0)
            Utils.error("Unable to compile shader code: " + glGetShaderInfoLog(shaderID),
                    "engine.display.ShaderProgram", 2, Utils.FATAL);
        glAttachShader(this.programID, shaderID);

        //return shader id
        return shaderID;
    }
}
