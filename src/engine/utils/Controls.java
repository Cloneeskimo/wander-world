package engine.utils;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Controls {

    //Controls
    public static final int CLOSE_PROGRAM = GLFW_KEY_ESCAPE;
    public static final int TOGGLE_GL_POLYGON_MODE = GLFW_KEY_1;
    public static final int TOGGLE_MOUSE_GRAB = GLFW_KEY_2;
    public static final int MOVE_LEFT = GLFW_KEY_A;
    public static final int MOVE_BACKWARD = GLFW_KEY_S;
    public static final int MOVE_RIGHT = GLFW_KEY_D;
    public static final int MOVE_FORWARD = GLFW_KEY_W;
    public static final int MOVE_UP = GLFW_KEY_SPACE;
    public static final int MOVE_DOWN = GLFW_KEY_LEFT_SHIFT;

    //Settings
    public static final float MOUSE_SENSITIVITY = 0.45f;
    public static final float CAMERA_SPEED = 0.15f;
    public static int currentPolygonMode = GL_FILL;
    public static boolean mouseGrabbed = true;

    /**
     * Toggles OpenGL's polygon mode
     */
    public static void togglePolygonMode() {
        Controls.currentPolygonMode = (Controls.currentPolygonMode == GL_FILL) ? GL_LINE : GL_FILL;
        glPolygonMode(GL_FRONT_AND_BACK, Controls.currentPolygonMode);
    }

    /**
     * Toggles whether the mouse is grabbed or not
     * @param windowID the id of the Window for which to toggle the mouse grab
     */
    public static void toggleMouseGrab(long windowID) {
        Controls.mouseGrabbed = !Controls.mouseGrabbed;
        glfwSetInputMode(windowID, GLFW_CURSOR, Controls.mouseGrabbed ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }
}
