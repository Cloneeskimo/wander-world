package game;

import engine.Window;

import static org.lwjgl.glfw.GLFW.*;

public class Game {

    //main method
    public static void main(String[] args) {

        //create window
        Window window = new Window();
        window.init();

        //game loop
        while (!glfwWindowShouldClose(window.getId())) {
            glfwPollEvents();
        }
    }
}
