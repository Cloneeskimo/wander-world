package engine.logic;

import engine.Window;
import engine.display.Camera;
import engine.display.Mesh;
import engine.display.Renderer;
import engine.item.RenderableItem;
import engine.utils.Controls;
import engine.utils.MouseInput;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class WorldLogic implements Logic {

    //Data
    private Renderer renderer;
    private Camera camera;
    private Window window;
    private RenderableItem[] items;

    //Init Method
    @Override
    public void init(Window window) {

        //assign data
        this.renderer = new Renderer(); //create renderer
        this.renderer.init(); //initialize renderer
        this.camera = new Camera();
        this.window = window; //set window reference

        //create mesh
        float[] positions = new float[]{
                -0.5f, 0.5f, -1.1f,
                -0.5f, -0.5f, -1.1f,
                0.5f, -0.5f, -1.1f,
                0.5f, 0.5f, -1.1f,};
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,};
        Mesh mesh = new Mesh(positions, indices);
        RenderableItem item = new RenderableItem(mesh);
        this.items = new RenderableItem[] { item };
    }

    //Input Method
    @Override
    public void input() {

        //camera movement
        this.camera.setVelocity(0, 0, 0);
        if (this.window.isKeyPressed(Controls.MOVE_FORWARD)) this.camera.accelerateZ(-Controls.CAMERA_SPEED);
        if (this.window.isKeyPressed(Controls.MOVE_BACKWARD)) this.camera.accelerateZ(Controls.CAMERA_SPEED);
        if (this.window.isKeyPressed(Controls.MOVE_RIGHT)) this.camera.accelerateX(Controls.CAMERA_SPEED);
        if (this.window.isKeyPressed(Controls.MOVE_LEFT)) this.camera.accelerateX(-Controls.CAMERA_SPEED);
        if (this.window.isKeyPressed(Controls.MOVE_UP)) this.camera.accelerateY(Controls.CAMERA_SPEED);
        if (this.window.isKeyPressed(Controls.MOVE_DOWN)) this.camera.accelerateY(-Controls.CAMERA_SPEED);
    }

    //Update Method
    @Override
    public void update(float dT, MouseInput mouseInput) {

        //update camera rotation
        if (Controls.mouseGrabbed) {
            Vector2f deltaMousePosition = mouseInput.getDeltaPosition();
            this.camera.rotate(deltaMousePosition.y * Controls.MOUSE_SENSITIVITY,
                    deltaMousePosition.x * Controls.MOUSE_SENSITIVITY, 0);
        }

        //update camera position
        this.camera.update();
    }

    //Render Method
    @Override
    public void render() {
        this.renderer.render(this.window, this.camera, this.items);
    }

    //Cleanup Method
    @Override
    public void cleanup() {
        this.renderer.cleanup();
        for (RenderableItem item : this.items) item.cleanup();
    }
}
