package engine.utils;

import engine.graphics.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    //Final Data
    private final Vector2d previousPosition;
    private final Vector2d currentPosition;
    private final Vector2f deltaPosition;

    //Data
    private boolean inWindow = false;
    private boolean leftPressed = false, rightPressed = false;

    //Constructor
    public MouseInput() {
        this.previousPosition = new Vector2d(0, 0);
        this.currentPosition = new Vector2d(0, 0);
        this.deltaPosition = new Vector2f();
    }

    //Init Method
    public void init(Window window) {

        //get starting position
        double[] xi = new double[] { 0 };
        double[] yi = new double[] { 0 };
        glfwGetCursorPos(window.getID(), xi, yi);
        this.previousPosition.x = xi[0];
        this.previousPosition.y = yi[0];
        this.currentPosition.x = xi[0];
        this.currentPosition.y = yi[0];

        //set position callback
        glfwSetCursorPosCallback(window.getID(), (id, x, y) -> {
            this.currentPosition.x = x;
            this.currentPosition.y = y;
        });

        //set inWindow callbackk
        glfwSetCursorEnterCallback(window.getID(), (id, in) -> {
           this.inWindow = in;
        });

        //set click callback
        glfwSetMouseButtonCallback(window.getID(), (id, button, action, mode) -> {
           this.leftPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
           this.rightPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    /**
     * updates the mouse position and the delta mouse position of this MouseInput
     */
    public void inputUpdate() {
        this.deltaPosition.x = this.deltaPosition.y = 0;
        if (this.inWindow) {
            double dX = this.currentPosition.x - this.previousPosition.x;
            double dY = this.currentPosition.y - this.previousPosition.y;
            this.deltaPosition.x = (float)dX;
            this.deltaPosition.y = (float)dY;
        }
        this.previousPosition.x = this.currentPosition.x;
        this.previousPosition.y = this.currentPosition.y;
    }

    //Accessors
    public Vector2f getDeltaPosition() { return this.deltaPosition; }
    public boolean isLeftPressed() { return this.leftPressed; }
    public boolean isRightPressed() { return this.rightPressed; }
}
