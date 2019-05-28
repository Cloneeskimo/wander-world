package engine.logic;

import engine.Window;
import engine.display.Camera;
import engine.display.Mesh;
import engine.display.Renderer;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class WorldLogic implements Logic {

    //Data
    private Renderer renderer;
    private Camera camera;
    private Window window;
    private Mesh mesh;

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
        this.mesh = new Mesh(positions, indices);
    }

    //Input Method
    @Override
    public void input() {
        glfwPollEvents(); //poll gl events
    }

    //Update Method
    @Override
    public void update(float dT) {
    }

    //Render Method
    @Override
    public void render() {
        this.renderer.render(this.window, this.mesh);
    }

    //Cleanup Method
    @Override
    public void cleanup() {
        this.renderer.cleanup();
        this.mesh.cleanup();
    }
}
