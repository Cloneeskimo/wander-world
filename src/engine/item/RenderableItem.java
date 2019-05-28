package engine.item;

import engine.display.Mesh;
import org.joml.Vector3f;

public class RenderableItem {

    //Data
    private final Mesh mesh;
    private final Vector3f position;
    private final Vector3f rotation;
    private float scale;

    //Constructor
    public RenderableItem(Mesh mesh) {
        this.mesh = mesh;
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = 1.0f;
    }

    //Render Method
    public void render() { this.mesh.render(); }

    //Accessors
    public Vector3f getPosition() { return this.position; }
    public Vector3f getRotation() { return rotation; }
    public float getScale() { return this.scale; }

    //Mutators
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }
    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    //Cleanup Method
    public void cleanup() {
        if (this.mesh != null) this.mesh.cleanup();
    }
}
