package engine.graphics.renderable;

import org.joml.Vector4f;

/**
 * A class to control what the surface of a mesh looks like. Can either be a solid color
 * or a texture
 */
public class Material {

    //Static Data
    private static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final float DEFAULT_REFLECTANCE = 0.5f;

    //Data
    private Vector4f ambientColor;
    private Vector4f diffuseColor;
    private Vector4f specularColor;
    private Texture texture;
    private float reflectance;

    //Default Constructor
    public Material() {
        this(Material.DEFAULT_COLOR, Material.DEFAULT_COLOR, Material.DEFAULT_COLOR, null, Material.DEFAULT_REFLECTANCE);
    }

    //Single Color Constructor
    public Material(Vector4f color) {
        this(color, color, color, null, Material.DEFAULT_REFLECTANCE);
    }

    //Single Color & Reflectance Constructor
    public Material(Vector4f color, float reflectance) {
        this(color, color, color, null, reflectance);
    }

    //Texture Constructor
    public Material(Texture texture) {
        this(Material.DEFAULT_COLOR, Material.DEFAULT_COLOR, Material.DEFAULT_COLOR, texture, Material.DEFAULT_REFLECTANCE);
    }

    //Texture & Reflectance Constructor
    public Material(Texture texture, float reflectance) {
        this(Material.DEFAULT_COLOR, Material.DEFAULT_COLOR, Material.DEFAULT_COLOR, texture, reflectance);
    }

    //Full Constructor
    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, Texture texture, float reflectance) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.texture = texture;
        this.reflectance = reflectance;
    }

    //Cleanup Method
    public void cleanup() {
        if (this.texture != null) this.texture.cleanup();
    }

    //Accessors
    public boolean isTextured() { return this.texture != null; }
    public float getReflectance() { return this.reflectance; }
    public Vector4f getAmbientColor() { return this.ambientColor; }
    public Vector4f getDiffuseColor() { return this.diffuseColor; }
    public Vector4f getSpecularColor() { return this.specularColor; }
    public Texture getTexture() { return this.texture; }
}
