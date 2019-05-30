package engine.graphics.renderable;

import engine.utils.Utils;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

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

        //create image and get rgb data from it
        BufferedImage image = ImageIO.read(Utils.class.getResource(resourcePath));
        this.width = image.getWidth();
        this.height = image.getHeight();
        int pixels[] = new int[image.getWidth() * image.getHeight()];
        System.out.println("w: " + image.getWidth() + ", h: " + image.getHeight());
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        //shove data into buffer
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 bytes per pixel
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); //r
                buffer.put((byte) ((pixel >> 8) & 0xFF)); //g
                buffer.put((byte) (pixel & 0xFF)); //b
                buffer.put((byte) ((pixel >> 24) & 0xFF)); //a
            }
        }

        //flip buffer
        buffer.flip();

        //create gl texture
        this.id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.id);

        //set wrapping settings
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        //set scaling/interpolation settings
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //unpack texture from buffer
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        //generate mipmaps
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    //Accessors
    public int getID() { return this.id; }

    //Cleanup Method
    public void cleanup() { glDeleteTextures(this.id); }
}
