package engine.logic;

import engine.Window;
import engine.display.Renderer;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class WorldLogic implements Logic {

    //Data
    private Renderer renderer;
    private Window window;

    //Init Method
    @Override
    public void init(Window window) {
        this.renderer = new Renderer(); //create renderer
        this.renderer.init(); //initialize renderer
        this.window = window; //set window reference
    }

    //Input Method
    @Override
    public void input() {
        glfwPollEvents(); //poll gl events
    }

    //Update Method
    @Override
    public void update() {

    }

    //Render Method
    @Override
    public void render() {
        this.renderer.render(this.window);
    }

    //Cleanup Method
    @Override
    public void cleanup() {
        this.renderer.cleanup();
    }
}
