package engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * Models a constant light pointing in certain direction (like the sun)
 */
public class DirectionalLight {

    //Data
    private Vector3f color;
    private Vector3f direction;
    private float intensity;

    //Full Constructor
    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    //Copy Constructor
    public DirectionalLight(DirectionalLight other) {
        this(new Vector3f(other.getColor()), new Vector3f(other.getDirection()), other.getIntensity());
    }

    //Accessors
    public Vector3f getColor() { return this.color; }
    public Vector3f getDirection() { return this.direction; }
    public float getIntensity() { return this.intensity; }

    //Mutators
    public void setColor(Vector3f color) { this.color = color; }
    public void setDirection(Vector3f direction) { this.direction = direction; }
    public void setIntensity(float intensity) { this.intensity = intensity; }
}
