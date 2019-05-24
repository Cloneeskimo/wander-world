package engine.logic;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class WorldLogic implements Logic {

    //Init Method
    @Override
    public void init() {

    }

    //Input Method
    @Override
    public void input() {
        glfwPollEvents();
    }

    //Update Method
    @Override
    public void update() {

    }

    //Render Method
    @Override
    public void render() {

    }

    //Cleanup Method
    @Override
    public void cleanup() {

    }
}
