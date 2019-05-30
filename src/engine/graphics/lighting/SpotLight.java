package engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * Models a light at a single point that points in a specific conal direction (like a flashlight)
 */
public class SpotLight {

    //Data
    private PointLight pointLight;
    private Vector3f direction;
    private float cutOff;

    //Full Constructor
    public SpotLight(PointLight pointLight, Vector3f direction, float cutOffAngle) {
        this.pointLight = pointLight;
        this.direction = direction;
        this.setCutOffAngle(cutOffAngle);
    }

    //Copy Constructor
    public SpotLight(SpotLight other) {
        this(new PointLight(other.getPointLight()), new Vector3f(other.getDirection()), 0);
        this.setCutOff(other.getCutOff());
    }

    //Accessors
    public PointLight getPointLight() { return this.pointLight; }
    public Vector3f getDirection() { return this.direction; }
    public float getCutOff() { return this.cutOff; }

    //Mutators
    public void setCutOffAngle(float cutOffAngle) { this.setCutOff((float)Math.cos(Math.toRadians(cutOffAngle))); }
    public void setCutOff(float cutOff) { this.cutOff = cutOff; }
    public void setDirection(Vector3f direction) { this.direction = direction; }
    public void setPointLight(PointLight pointLight) { this.pointLight = pointLight; }
}
