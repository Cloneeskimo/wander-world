package game.logic;

import engine.Logic;
import engine.graphics.Camera;
import engine.graphics.Renderer;
import engine.graphics.Window;
import engine.graphics.lighting.DirectionalLight;
import engine.graphics.lighting.SceneLighting;
import engine.graphics.renderable.Scene;
import engine.utils.Controls;
import engine.utils.MouseInput;
import game.Area;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class WorldLogic implements Logic {

    //Data
    private Renderer renderer;
    private Camera camera;
    private Window window;
    private Scene scene;
    private float directionalLightAngle;
    private Area area;

    //Init Method
    @Override
    public void init(Window window) throws Exception {

        //assign data
        this.renderer = new Renderer(); //create renderer
        this.renderer.init(); //initialize renderer
        this.camera = new Camera();
        this.window = window; //set window reference
        this.scene = new Scene();
        this.area = new Area("pillarmaze"); //load pillarmaze map

        //create lighting
        SceneLighting lighting = new SceneLighting();
        lighting.setAmbientLight(new Vector3f(1.0f, 1.0f, 1.0f));
        lighting.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(-1, 0, 0), 1.0f));
        this.scene.setLighting(lighting);

        //add items to scene
        this.scene.addItems(this.area.getItems());
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
            Vector3f rot = this.camera.getRotation();
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

        //update angle
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
