package engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * Models a light as a single point (like a torch or lamp)
 */
public class PointLight {

    //Data
    private Vector3f color;
    private Vector3f position;
    private float intensity;
    private Attenuation attenuation;

    //Default Attenuation Constructor
    public PointLight(Vector3f color, Vector3f position, float intensity) {
        this(color, position, intensity, new Attenuation(1, 0, 0));
    }

    //Custom Attenuation Constructor
    public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.attenuation = attenuation;
    }

    //Copy Constructor
    public PointLight(PointLight other) {
        this(new Vector3f(other.getColor()), new Vector3f(other.getPosition()), other.getIntensity(),
                other.getAttenuation());
    }

    //Accessors
    public Vector3f getColor() { return this.color; }
    public Vector3f getPosition() { return position; }
    public float getIntensity() { return this.intensity; }
    public Attenuation getAttenuation() { return this.attenuation; }

    //Mutators
    public void setColor(Vector3f color) { this.color = color; }
    public void setPosition(Vector3f position) { this.position = position; }
    public void setIntensity(float intensity) { this.intensity = intensity; }
    public void setAttenuation(Attenuation attenuation) { this.attenuation = attenuation; }

    /**
     * Models the attenuation of a PointLight
     */
    public static class Attenuation {

        //Data
        private float constant;
        private float linear;
        private float exponent;

        //Full Constructor
        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        //Accessors
        public float getConstant() { return this.constant; }
        public float getLinear() { return this.linear; }
        public float getExponent() { return this.exponent; }

        //Mutators
        public void setConstant(float constant) { this.constant = constant; }
        public void setLinear(float linear) { this.linear = linear; }
        public void setExponent(float exponent) { this.exponent = exponent; }
    }
}
