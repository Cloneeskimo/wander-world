package engine.graphics;

import org.joml.Vector3f;

public class Camera {

    //Data
    private Vector3f position, rotation, velocity;

    //Default Constructor
    public Camera() {
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.velocity = new Vector3f();
    }

    //Full Constructor
    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Moves the camera by the provided offsets
     */
    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            this.position.x += (float)Math.sin(Math.toRadians(this.rotation.y)) * -1.0f * offsetZ;
            this.position.z += (float)Math.cos(Math.toRadians(this.rotation.y)) * offsetZ;
        }
        if (offsetX != 0) {
            this.position.x += (float)Math.sin(Math.toRadians(this.rotation.y - 90)) * -1.0f * offsetX;
            this.position.z += (float)Math.cos(Math.toRadians(this.rotation.y - 90)) * offsetX;
        }
        this.position.y += offsetY;
    }

    /**
     * Rotates the camera by the provided offsets
     */
    public void rotate(float offsetX, float offsetY, float offsetZ) {
        this.rotation.x += offsetX;
        this.rotation.y += offsetY;
        this.rotation.z += offsetZ;
    }

    //Update Method
    public void update() {
        this.movePosition(this.velocity.x, this.velocity.y, this.velocity.z);
    }

    //Accessors
    public Vector3f getPosition() { return this.position; }
    public Vector3f getRotation() { return this.rotation; }

    //Mutators
    public void setPosition(Vector3f position) { this.position = position; }
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }
    public void setRotation(Vector3f rotation) { this.rotation = rotation; }
    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    public void accelerateX(float dx) { this.velocity.x += dx; }
    public void accelerateY(float dy) { this.velocity.y += dy; }
    public void accelerateZ(float dz) { this.velocity.z += dz; }
    public void setVelocity(float x, float y, float z) {
        this.velocity.x = x;
        this.velocity.y = y;
        this.velocity.z = z;
    }
}
