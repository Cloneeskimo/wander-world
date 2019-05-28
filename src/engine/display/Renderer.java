package engine.display;

import engine.Window;
import engine.utils.Utils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

//Error Codes Used: 0

public class Renderer {

    //Static Data
    private static final float FOV = (float)Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;

    //Data
    ShaderProgram shaderProgram;
    Transformer transformer;

    //Init Method
    public void init() {

        //create shader program, link it, and create unfiorms
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader("/shaders/vertex.glsl");
        this.shaderProgram.createFragmentShader("/shaders/fragment.glsl");
        this.shaderProgram.link();
        this.shaderProgram.createUniform("projectionMatrix");

        //create transformer
        this.transformer = new Transformer();
    }

    //Render Method
    /**
     * @param window the window to render to
     */
    public void render(Window window, Mesh mesh) {

        //clear and check for window resize
        clear(); //clear screen
        if (window.hasBeenResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight()); //change viewport size
            window.resizeAccountedFor(); //account for resize
        }

        //bind shader program
        this.shaderProgram.bind();

        //set projection matrix
        this.shaderProgram.setUniform("projectionMatrix", this.transformer.buildProjectionMatrix(
                Renderer.FOV, Renderer.Z_NEAR, Renderer.Z_FAR, window));

        //render mesh
        mesh.render();

        //unbind shader program
        this.shaderProgram.unbind();
    }

    //Cleanup Method
    public void cleanup() {

        //cleanup shaders
        if (this.shaderProgram != null) this.shaderProgram.cleanup();

        //disable vao, vbo, attrib array 0
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    //Clear Method
    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
