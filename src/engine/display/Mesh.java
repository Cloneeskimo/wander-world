package engine.display;

import engine.utils.Utils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

//Error Codes Used: 0

public class Mesh {

    //Data
    private int vao;
    private int[] vbos; // [0] - positions, [1] - indices
    private int vertexCount;

    //Constructor
    /**
     * @param positions - a completely filled array of vertex positions
     * @param indices - a completely filled array of indices
     */
    public Mesh(float[] positions, int[] indices) {

        //create buffers
        FloatBuffer positionsBuffer = null;
        IntBuffer indicesBuffer = null;

        //create vbos and vaos
        try {

            //count vertices
            vertexCount = indices.length;

            //create and bind vao, create vbos array
            this.vao = glGenVertexArrays();
            glBindVertexArray(this.vao);
            this.vbos = new int[2];

            //positions vbo
            positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
            positionsBuffer.put(positions).flip();
            this.vbos[0] = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, this.vbos[0]);
            glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false,0, 0);

            //indices vbo
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            this.vbos[1] = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.vbos[1]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            //unbind vbo and vao
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            //catch errors
        } catch (Exception e) {
            Utils.error("Unable to create mesh: " + e.getMessage(), "engine.display.Mesh", 0, Utils.FATAL);
        } finally {

            //free memory
            if (positionsBuffer != null) MemoryUtil.memFree(positionsBuffer);
            if (indicesBuffer != null) MemoryUtil.memFree(indicesBuffer);
        }
    }

    //Render Method
    public void render() {

        //bind vao and enable first attrib array
        glBindVertexArray(this.vao);
        glEnableVertexAttribArray(0);

        //draw
        glDrawElements(GL_TRIANGLES, this.vertexCount, GL_UNSIGNED_INT, 0);

        //restore state
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);


    }

    //Cleanup Method
    public void cleanup() {

        //disable vertex attrib array 0
        glDisableVertexAttribArray(0);

        //delete buffers
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        for (int vbo : this.vbos) glDeleteBuffers(vbo);

        //delete vao
        glBindVertexArray(0);
        glDeleteVertexArrays(this.vao);
    }

}
