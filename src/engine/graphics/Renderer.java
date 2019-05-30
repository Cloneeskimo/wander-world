package engine.graphics;

import engine.graphics.lighting.DirectionalLight;
import engine.graphics.lighting.PointLight;
import engine.graphics.lighting.SceneLighting;
import engine.graphics.lighting.SpotLight;
import engine.graphics.renderable.Mesh;
import engine.graphics.renderable.RenderableItem;
import engine.graphics.renderable.Scene;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    //Static Data
    private static final float FOV = (float)Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    private static final int MAX_POINT_LIGHTS = 5; //must match shader constant
    private static final int MAX_SPOT_LIGHTS = 5; //must match shader constant

    //Data
    private ShaderProgram shaderProgram;
    private Transformer transformer;
    private float specularPower = 10f; //used for light rendering

    //Init Method
    public void init() {

        //create shader program, link it
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader("/shaders/vertex.glsl");
        this.shaderProgram.createFragmentShader("/shaders/fragment.glsl");
        this.shaderProgram.link();

        //create shader uniforms
        this.createUniforms();

        //create transformer
        this.transformer = new Transformer();
    }

    /**
     * Creates all the uniforms needed for the shader program to operate
     */
    private void createUniforms() {

        //create matrix and texture sampler uniforms
        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("modelViewMatrix");
        this.shaderProgram.createUniform("textureSampler");

        //create material and lighting uniforms
        this.shaderProgram.createUniform("ambientLight");
        this.shaderProgram.createUniform("specularPower");
        this.shaderProgram.createMaterialUniform("material");
        this.shaderProgram.createPointLightUniforms("pointLights", Renderer.MAX_POINT_LIGHTS);
        this.shaderProgram.createSpotLightUniforms("spotLights", Renderer.MAX_SPOT_LIGHTS);
        this.shaderProgram.createDirectionalLightUniform("directionalLight");
    }

    /**
     * Renders a scene
     * @param window the Window to render to
     * @param camera the Camera whose view to take into account
     * @param scene the scene whose lights and items to render
     */
    public void render(Window window, Camera camera, Scene scene) {

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

        //build view matrix and render lights
        renderLights(this.transformer.buildViewMatrix(camera), scene.getLighting());

        //render items
        Map<Mesh, List<RenderableItem>> meshes = scene.getMeshes();
        for (Mesh mesh : meshes.keySet()) {

            //set material
            this.shaderProgram.setUniform("material", mesh.getMaterial());

            //render all items with this mesh
            mesh.renderList(meshes.get(mesh), (RenderableItem item) -> {
                this.shaderProgram.setUniform("modelViewMatrix", this.transformer.buildModelViewMatrix(item));
            });

        }

        //unbind shader program
        this.shaderProgram.unbind();
    }

    /**
     * Renders the lights of a scene
     * @param viewMatrix the view matrix to use for rendering
     * @param lighting the SceneLighting object whose light members to render
     */
    private void renderLights(Matrix4f viewMatrix, SceneLighting lighting) {

        //set ambient light and specular power
        this.shaderProgram.setUniform("ambientLight", lighting.getAmbientLight());
        this.shaderProgram.setUniform("specularPower", this.specularPower);

        //render PointLights
        PointLight[] pointLights = lighting.getPointLights();
        int count = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < count; i++) {

            //create copy and multiply by view matrix
            PointLight plCopy = new PointLight(pointLights[i]);
            Vector3f pos = plCopy.getPosition();
            Vector4f posT = new Vector4f(pos, 1);
            posT.mul(viewMatrix);
            pos.x = posT.x;
            pos.y = posT.y;
            pos.z = posT.z;
            shaderProgram.setUniform("pointLights[" + i + "]", plCopy);
        }

        //render SpotLights
        SpotLight[] spotLights = lighting.getSpotLights();
        count = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < count; i++) {

            //create copy and multiply by view matrix
            SpotLight slCopy = new SpotLight(spotLights[i]);
            Vector4f dirT = new Vector4f(slCopy.getDirection(), 0);
            dirT.mul(viewMatrix);
            slCopy.setDirection(new Vector3f(dirT.x, dirT.y, dirT.z));
            Vector3f pos = slCopy.getPointLight().getPosition();
            Vector4f posT = new Vector4f(pos, 1);
            posT.mul(viewMatrix);
            pos.x = posT.x;
            pos.y = posT.y;
            pos.z = posT.z;
            shaderProgram.setUniform("spotLights[" + i + "]", slCopy);
        }

        //render DirectionalLight
        if (lighting.hasDirectionalLighting()) {
            DirectionalLight dlCopy = new DirectionalLight(lighting.getDirectionalLight());
            Vector4f dirT = new Vector4f(dlCopy.getDirection(), 0);
            dirT.mul(viewMatrix);
            dlCopy.setDirection(new Vector3f(dirT.x, dirT.y, dirT.z));
            shaderProgram.setUniform("directionalLight", dlCopy);
        }
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
