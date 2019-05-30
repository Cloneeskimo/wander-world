package engine;

import engine.utils.Controls;
import engine.utils.Utils;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

//Error Codes Used: 0-2

public class Window {

    //Default Window Settings
    public static final boolean DEFAULT_VYSNC = true;
    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;
    public static final String DEFAULT_TITLE = "Wander World";

    //Window Data
    private boolean vSync;
    private boolean resized = false;
    private int width, height;
    private long id;
    private String title;
    private Vector4f clearColor  = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    //Full Constructor
    public Window(int width, int height, String title, boolean vSync) {
        this.vSync = vSync;
        this.width = width;
        this.height = height;
        this.title = title;
    }

    //Default Constructor
    public Window() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE, DEFAULT_VYSNC);
    }

    //Init Method
    public void init() {

        //init GLFW
        if (!glfwInit()) Utils.error("unable to initialize GLFW", "engine.Window", 0, Utils.FATAL);

        //set window hints
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); //window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); //window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        //create window
        this.id = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (this.id == NULL) Utils.error("unable to create GLFW window", "engine.Window", 1, Utils.FATAL);

        //set key callback
        glfwSetKeyCallback(this.id, (window, key, scancode, action, mods) -> {

            //escape key closes window
            if (key == Controls.CLOSE_PROGRAM && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(this.id, true);
            } else if (key == Controls.TOGGLE_GL_POLYGON_MODE && action == GLFW_RELEASE) {
                Controls.togglePolygonMode();
            } else if (key == Controls.TOGGLE_MOUSE_GRAB && action == GLFW_RELEASE) {
                Controls.toggleMouseGrab(this.id);
            }

        });

        //set resize callback
        glfwSetFramebufferSizeCallback(this.id, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resized = true;
        });

        //double-check current frame size
        try (MemoryStack stack = MemoryStack.stackPush()) {

            //allocate space for width, height
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            //load image from memory
            glfwGetFramebufferSize(this.id, w, h);

            //set width and height
            this.width = w.get();
            this.height = h.get();

        } catch (Exception e) {
            Utils.error("unable to acquire frame size: " + e.getMessage(), "engine.Window", 2, Utils.FATAL);
        }

        //get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        //center the window
        glfwSetWindowPos(this.id, (vidmode.width() - this.width) / 2, (vidmode.height() - this.height) / 2);

        //make window current context
        glfwMakeContextCurrent(this.id);

        //enable v-sync
        if (this.vSync) glfwSwapInterval(1);

        //create capabilites and show window
        GL.createCapabilities();
        glfwShowWindow(this.id);

        //set clear color and enable depth testing
        glClearColor(this.clearColor.x, this.clearColor.y, this.clearColor.z, this.clearColor.w);
        glEnable(GL_DEPTH_TEST);

        //enable support for transparencies
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //enable face culling
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        //grab mouse if enabled
        if (Controls.mouseGrabbed) glfwSetInputMode(this.id, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    /**
     * Swaps this window's buffers and polls for events - should be called after renderering
     */
    public void postRender() {
        glfwSwapBuffers(this.id);
        glfwPollEvents();
    }

    //Accessors
    public boolean hasBeenResized() { return this.resized; }
    public boolean shouldClose() { return glfwWindowShouldClose(this.id); }
    public boolean isVSync() { return this.vSync; }
    public boolean isKeyPressed(int keyCode) { return glfwGetKey(this.id, keyCode) == GLFW_PRESS; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public long getID() { return this.id; }

    //Mutators
    public void resizeAccountedFor() { this.resized = false; }
}
