package engine.graphics;

import engine.RenderableItem;
import engine.Window;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    //Static Data
    private static final float FOV = (float)Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;

    //Data
    ShaderProgram shaderProgram;
    Transformer transformer;

    //Init Method
    public void init() {

        //create shader program, link it
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader("/shaders/vertex.glsl");
        this.shaderProgram.createFragmentShader("/shaders/fragment.glsl");
        this.shaderProgram.link();

        //create matrix uniforms
        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("modelViewMatrix");

        //create other uniforms
        this.shaderProgram.createUniform("textureSampler");
        this.shaderProgram.createMaterialUniform("material");

        //create transformer
        this.transformer = new Transformer();
    }

    /**
     * Renders a scene
     * @param window the Window to render to
     * @param camera the Camera whose view to take into account
     * @param items the array of RenderableItems to render
     */
    public void render(Window window, Camera camera, RenderableItem[] items) {

        //clear and check for window resize
        clear(); //clear screen
        if (window.hasBeenResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight()); //change viewport size
            window.resizeAccountedFor(); //account for resize
        }

        //bind shader program
        this.shaderProgram.bind();

        //set texture sampler
        this.shaderProgram.setUniform("textureSampler", 0);

        //set projection matrix
        this.shaderProgram.setUniform("projectionMatrix", this.transformer.buildProjectionMatrix(
                Renderer.FOV, Renderer.Z_NEAR, Renderer.Z_FAR, window));

        //build view matrix
        this.transformer.buildViewMatrix(camera);

        //render items
        for (RenderableItem item : items) {

            //set model view matrix
            this.shaderProgram.setUniform("modelViewMatrix", this.transformer.buildModelViewMatrix(item));

            //set material
            this.shaderProgram.setUniform("material", item.getMesh().getMaterial());

            //render
            item.render();
        }

        //unbind shader program
        this.shaderProgram.unbind();
    }

    //Cleanup Method
    public void cleanup() {

        //cleanup shaders
        if (this.shaderProgram != null) this.shaderProgram.cleanup();
    }

    //Clear Method
    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
