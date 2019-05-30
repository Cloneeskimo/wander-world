package engine.graphics.renderable;

import org.joml.Vector4f;

/**
 * A class to control what the surface of a mesh looks like. Can either be a solid color
 * or a texture
 */
public class Material {

    //Static Data
    private static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    //Data
    private Vector4f color;
    private Texture texture;

    //Default Constructor
    public Material() {
        this.color = Material.DEFAULT_COLOR;
    }

    //Texture Constructor
    public Material(Texture texture) {
        this.texture = texture;
        this.color = Material.DEFAULT_COLOR;
    }

    //Color Constructor
    public Material(Vector4f color) {
        this.color = color;
    }

    //Cleanup Method
    public void cleanup() {
        if (this.texture != null) this.texture.cleanup();
    }

    //Accessors
    public boolean isTextured() { return this.texture != null; }
    public Texture getTexture() { return this.texture; }
    public Vector4f getColor() { return this.color; }
}
