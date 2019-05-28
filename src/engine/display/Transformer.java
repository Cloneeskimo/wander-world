package engine.display;

import engine.Window;
import org.joml.Matrix4f;

public class Transformer {

    //Data
    private Matrix4f projectionMatrix;

    //Constructor
    public Transformer() {
        this.projectionMatrix = new Matrix4f();
    }

    //Projection Matrix Building Method
    /**
     * @param FOV - the desired field of view (in radians)
     * @param zNear - the nearest distance from the camera to be visible
     * @param zFar - the farthest distance from the camera to be visible
     * @param window - a reference to the window
     * @return the built projection matrix
     */
    public Matrix4f buildProjectionMatrix(float FOV, float zNear, float zFar, Window window) {

        //calculate projection matrix
        float aspectRatio = (float)window.getWidth() / (float)window.getHeight();
        this.projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio, zNear, zFar);

        //return the projection matrix
        return this.projectionMatrix;
    }

    //Accessors
    public Matrix4f getProjectionMatrix() { return this.projectionMatrix; }
}
