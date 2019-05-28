package engine.display;

import engine.Window;
import engine.item.RenderableItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformer {

    //Data
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    //Constructor
    public Transformer() {
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
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
        this.projectionMatrix.identity().perspective(FOV, aspectRatio, zNear, zFar);

        //return the projection matrix
        return this.projectionMatrix;
    }

    //View Matrix Building Method
    /**
     * @param camera - the camera whose view is to be considered
     * @return the built view matrix
     */
    public Matrix4f buildViewMatrix(Camera camera) {

        //get camera position and rotation
        Vector3f cameraPosition = camera.getPosition();
        Vector3f cameraRotation = camera.getRotation();

        //rotate
        this.viewMatrix.identity().rotate((float)Math.toRadians(cameraRotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(cameraRotation.y), new Vector3f(0, 1, 0));

        //then translate
        viewMatrix.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        //return the view matrix
        return this.viewMatrix;
    }

    //ModelView Matrix Building Method
    /**
     * @param item - the item whose aspects are to be considered
     * @return the built model view matrix
     */
    public Matrix4f buildModelViewMatrix(RenderableItem item) {
        Vector3f rotation = item.getRotation();
        Matrix4f modelViewMatrix = new Matrix4f();
        modelViewMatrix.identity().translate(item.getPosition())
                .rotateX((float)Math.toRadians(-rotation.x))
                .rotateY((float)Math.toRadians(-rotation.y))
                .rotateZ((float)Math.toRadians(-rotation.z))
                .scale(item.getScale());
        Matrix4f viewMatrixCopy = new Matrix4f(this.viewMatrix);
        return viewMatrixCopy.mul(modelViewMatrix);
    }

    //Accessors
    public Matrix4f getProjectionMatrix() { return this.projectionMatrix; }
}
