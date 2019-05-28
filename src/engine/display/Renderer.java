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
    private int vao, vbo;

    //Init Method
    public void init() {

        //create shader program and link it
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader("/shaders/vertex.glsl");
        this.shaderProgram.createFragmentShader("/shaders/fragment.glsl");
        this.shaderProgram.link();

        //define triangle vertices
        float vertices[] = {
             0.0f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
             0.5f, -0.5f, 0.0f
        };

        //create triangle
        FloatBuffer verticesBuffer = null;
        try {

            //try to put vertices into a buffer
            verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();

            //create vao
            this.vao = glGenVertexArrays();
            glBindVertexArray(this.vao);

            //create vbo for vertices
            this.vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

            //put vbo data in vao
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            //unbind vbo, vao
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

        } catch (Exception e) {
            Utils.error("Unable to create triangle: " + e.getMessage(), "engine.display.Renderer", 0, Utils.FATAL);
        } finally {

            //free memory
            if (verticesBuffer != null) MemoryUtil.memFree(verticesBuffer);
        }

    }

    //Render Method
    /**
     * @param window the window to render to
     */
    public void render(Window window) {

        //clear and check for window resize
        clear(); //clear screen
        if (window.hasBeenResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight()); //change viewport size
            window.resizeAccountedFor(); //account for resize
        }

        //bind shader program and vao
        this.shaderProgram.bind();
        glBindVertexArray(this.vao);
        glEnableVertexAttribArray(0);

        //draw
        glDrawArrays(GL_TRIANGLES, 0, 3);

        //restore state
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        this.shaderProgram.unbind();

        //swap buffers
        glfwSwapBuffers(window.getId());
    }

    //Cleanup Method
    public void cleanup() {

        //cleanup shaders
        if (this.shaderProgram != null) this.shaderProgram.cleanup();

        //disable vao, vbo, attrib array 0
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        //delete vao, vbo
        glDeleteBuffers(this.vbo);
        glDeleteVertexArrays(this.vao);
    }

    //Clear Method
    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
