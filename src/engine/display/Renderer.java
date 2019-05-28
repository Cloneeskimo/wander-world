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

    //Data
    ShaderProgram shaderProgram;

    //Init Method
    public void init() {

        //create shader program and link it
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader("/shaders/vertex.glsl");
        this.shaderProgram.createFragmentShader("/shaders/fragment.glsl");
        this.shaderProgram.link();
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
