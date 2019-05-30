package game.logic;

import engine.Logic;
import engine.graphics.Camera;
import engine.graphics.OBJLoader;
import engine.graphics.Renderer;
import engine.graphics.Window;
import engine.graphics.lighting.DirectionalLight;
import engine.graphics.lighting.PointLight;
import engine.graphics.lighting.SceneLighting;
import engine.graphics.renderable.*;
import engine.utils.Controls;
import engine.utils.MouseInput;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;

public class WorldLogic implements Logic {

    //Data
    private Renderer renderer;
    private Camera camera;
    private Window window;
    private Scene scene;
    private float directionalLightAngle;

    //Init Method
    @Override
    public void init(Window window) throws Exception {

        //assign data
        this.renderer = new Renderer(); //create renderer
        this.renderer.init(); //initialize renderer
        this.camera = new Camera();
        this.window = window; //set window reference
        this.scene = new Scene();

        //create pillar item
        Texture pillarTexture = new Texture("/textures/templepillar.png");
        Material pillarMaterial = new Material(pillarTexture);
        Mesh pillarMesh = OBJLoader.loadOBJ("/models/pillar.obj");
        pillarMesh.setMaterial(pillarMaterial);
        RenderableItem pillar = new RenderableItem(pillarMesh);
        pillar.setPosition(1, 0, -5);

        //create second pillar item
        Material pillarMaterial2 = new Material(new Vector4f(0.6f, 0.6f, 0.0f, 1.0f));
        Mesh pillarMesh2 = OBJLoader.loadOBJ("/models/pillar.obj");
        pillarMesh2.setMaterial(pillarMaterial2);
        RenderableItem pillar2 = new RenderableItem(pillarMesh2);
        pillar2.setPosition(-1, 0, -5);

        //add items to scene
        this.scene.addItems(new RenderableItem[] { pillar, pillar2 });

        //create lighting
        SceneLighting lighting = new SceneLighting();
        lighting.setAmbientLight(new Vector3f(1.0f, 1.0f, 1.0f));
        lighting.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(-1, 0, 0), 1.0f));
        PointLight pl = new PointLight(new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(0, 0, -5), 1.0f);
        lighting.setPointLights(new PointLight[] { pl });
        this.scene.setLighting(lighting);
    }

    //Input Method
    @Override
    public void input() {

        //camera movement
        this.camera.setVelocity(0, 0, 0);
        if (this.window.isKeyPressed(Controls.MOVE_FORWARD)) this.camera.accelerateZ(-Controls.CAMERA_SPEED);
        if (this.window.isKeyPressed(Controls.MOVE_BACKWARD)) this.camera.accelerateZ(Controls.CAMERA_SPEED);
        if (this.window.isKeyPressed(Controls.MOVE_RIGHT)) this.camera.accelerateX(Controls.CAMERA_SPEED);
        if (this.window.isKeyPressed(Controls.MOVE_LEFT)) this.camera.accelerateX(-Controls.CAMERA_SPEED);
        if (this.window.isKeyPressed(Controls.MOVE_UP)) this.camera.accelerateY(Controls.CAMERA_SPEED);
        if (this.window.isKeyPressed(Controls.MOVE_DOWN)) this.camera.accelerateY(-Controls.CAMERA_SPEED);
    }

    //Update Method
    @Override
    public void update(float dT, MouseInput mouseInput) {

        //update camera rotation
        if (Controls.mouseGrabbed) {
            Vector2f deltaMousePosition = mouseInput.getDeltaPosition();
            this.camera.rotate(deltaMousePosition.y * Controls.MOUSE_SENSITIVITY,
                    deltaMousePosition.x * Controls.MOUSE_SENSITIVITY, 0);
        }

        //update camera position
        this.camera.update();

        //update DirectionalLight
        DirectionalLight dl = this.scene.getLighting().getDirectionalLight();
        this.directionalLightAngle += 0.2f;
        float intensity = 1.0f;


        //if night time
        if (this.directionalLightAngle > 90 || this.directionalLightAngle < -90) {
            intensity = 0;
            if (this.directionalLightAngle > 180) this.directionalLightAngle -= 360;

        //if sunrise or sundown
        } else if (this.directionalLightAngle > 60 || this.directionalLightAngle < -60) {

            float factor = 1 - (Math.abs(this.directionalLightAngle) - 60) / 30; //0.0f - 1.0f
            intensity = factor;
            float ambient = Math.max(0.3f, intensity);
            this.scene.getLighting().setAmbientLight(new Vector3f(ambient, ambient, ambient));

        }


        double angle = Math.toRadians(this.directionalLightAngle);
        dl.getDirection().x = (float) Math.sin(angle);
        dl.getDirection().y = (float) Math.cos(angle);
        dl.setIntensity(intensity);
    }

    //Render Method
    @Override
    public void render() {
        this.renderer.render(this.window, this.camera, this.scene);
    }

    //Cleanup Method
    @Override
    public void cleanup() {
        this.renderer.cleanup();
        if (this.scene != null) this.scene.cleanup();
    }
}
