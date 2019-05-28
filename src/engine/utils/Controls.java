package engine.utils;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.opengl.GL11.*;

public class Controls {

    //Controls
    public static final long CLOSE_PROGRAM = GLFW_KEY_ESCAPE;
    public static final long TOGGLE_GL_POLYGON_MODE = GLFW_KEY_1;

    //Settings
    public static int currentPolygonMode = GL_FILL;

    //Polygon Mode Switching Method
    public static void togglePolygonMode() {
        Controls.currentPolygonMode = (Controls.currentPolygonMode == GL_FILL) ? GL_LINE : GL_FILL;
        System.out.println("toggled");
        glPolygonMode(GL_FRONT_AND_BACK, Controls.currentPolygonMode);
    }
}
