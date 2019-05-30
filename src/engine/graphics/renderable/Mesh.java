package engine.graphics.renderable;

import engine.utils.Utils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    //Data
    private int vao;
    private int[] vbos; // [0] positions, [1] texture coordinates, [2] normal vectors, [3] indices
    private int vertexCount;
    private Material material;

    /**
     * Constructor
     * @param positions a completely filled array of vertex positions
     * @param texCoords a completely filled array of texture coordinates
     * @param normals a completely filled array of normal vectors
     * @param indices a completely filled array of indices
     * @param material the material to be used for the surface of the mesh
     */
    public Mesh(float[] positions, float[] texCoords, float[] normals, int[] indices, Material material) {

        //create default material
        this.material = material;

        //create buffers
        FloatBuffer positionsBuffer = null;
        FloatBuffer texCoordsBuffer = null;
        FloatBuffer normalsBuffer = null;
        IntBuffer indicesBuffer = null;

        //create vbos and vaos
        try {

            //count vertices
            vertexCount = indices.length;

            //create and bind vao, create vbos array
            this.vao = glGenVertexArrays();
            glBindVertexArray(this.vao);
            this.vbos = new int[4];

            //positions vbo
            positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
            positionsBuffer.put(positions).flip();
            this.vbos[0] = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, this.vbos[0]);
            glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false,0, 0);

            //texture coordinates vbo
            texCoordsBuffer = MemoryUtil.memAllocFloat(texCoords.length);
            texCoordsBuffer.put(texCoords).flip();
            this.vbos[1] = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, this.vbos[1]);
            glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            //normal vectors vbo
            normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            normalsBuffer.put(normals).flip();
            this.vbos[2] = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, this.vbos[2]);
            glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            //indices vbo
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            this.vbos[3] = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.vbos[3]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            //unbind vbo and vao
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            //catch exceptions
        } catch (Exception e) {

            Utils.log(e, "engine.graphics.renderable.Mesh");
            e.printStackTrace();

        } finally {

            //free memory
            if (positionsBuffer != null) MemoryUtil.memFree(positionsBuffer);
            if (texCoordsBuffer != null) MemoryUtil.memFree(texCoordsBuffer);
            if (normalsBuffer != null) MemoryUtil.memFree(normalsBuffer);
            if (indicesBuffer != null) MemoryUtil.memFree(indicesBuffer);
        }
    }

    /**
     * Constructor which has the same parameters as the above constructor, but sets the material to the
     * default material
     */
    public Mesh(float[] position, float[] texCoords, float[] normals, int[] indices) {
        this(position, texCoords, normals, indices, new Material());
    }

    //Render Method
    public void render() {

        //render this mesh
        this.preRender();
        glDrawElements(GL_TRIANGLES, this.vertexCount, GL_UNSIGNED_INT, 0);
        this.postRender();
    }

    /**
     * Sets up rendering process
     */
    private void preRender() {

        //bind texture
        if (this.material.isTextured()) {
            glActiveTexture(GL_TEXTURE0); //activate first texture bank
            glBindTexture(GL_TEXTURE_2D, this.material.getTexture().getID());
        }

        //bind vao and attribute arrays
        glBindVertexArray(this.vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }

    /**
     * Cleans up after rendering has been done
     */
    private void postRender() {

        //restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    //Cleanup Method
    public void cleanup() {

        //cleanup material
        this.material.cleanup();

        //disable vertex attribute arrays
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        //delete buffers
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vbo : this.vbos) glDeleteBuffers(vbo);

        //delete vao
        glBindVertexArray(0);
        glDeleteVertexArrays(this.vao);
    }

    //Accessors
    public Material getMaterial() { return this.material; }

    //Mutators
    public void setMaterial(Material material) { this.material = material; }
}
