package engine.graphics.renderable;

import engine.utils.Utils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture {

    //Data
    private int id;
    private int width;
    private int height;

    /**
     * Constructor which constructs the texture from a resource path
     * @param resourcePath resource path of the texture file
     */
    public Texture(String resourcePath) throws IOException {
        this(Utils.resourceToByteBuffer(resourcePath, 1024));
    }

    /**
     * Constructor which constructs the texture from a ByteBuffer holding the image data
     * @param imageData
     */
    public Texture(ByteBuffer imageData) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            //create buffers
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer avChannels = stack.mallocInt(1);

            //load image
            ByteBuffer decodedImage = stbi_load_from_memory(imageData, w, h, avChannels, 4);

            //set width and height
            this.width = w.get();
            this.height = h.get();

            //generate, bind, and configure texture
            this.id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, this.id);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1); //each component is one byte in size

            //set scaling/interpolation settings
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            //unpack texture from buffer
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, decodedImage);

            //generate mipmaps
            glGenerateMipmap(GL_TEXTURE_2D);

            //free image memory
            stbi_image_free(imageData);
        }
    }

    //Accessors
    public int getID() { return this.id; }

    //Cleanup Method
    public void cleanup() { glDeleteTextures(this.id); }
}
