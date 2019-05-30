package game.logic;

import engine.Logic;
import engine.RenderableItem;
import engine.Window;
import engine.graphics.Camera;
import engine.graphics.OBJLoader;
import engine.graphics.Renderer;
import engine.graphics.renderable.Material;
import engine.graphics.renderable.Mesh;
import engine.graphics.renderable.Texture;
import engine.utils.Controls;
import engine.utils.MouseInput;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class WorldLogic implements Logic {

    //Data
    private Renderer renderer;
    private Camera camera;
    private Window window;
    private RenderableItem[] items;

    //Init Method
    @Override
    public void init(Window window) throws Exception {

        //assign data
        this.renderer = new Renderer(); //create renderer
        this.renderer.init(); //initialize renderer
        this.camera = new Camera();
        this.window = window; //set window reference

        //create pillar item
        Texture pillarTexture = new Texture("/textures/templepillar.png");
        Material pillarMaterial = new Material(pillarTexture);
        Mesh pillarMesh = OBJLoader.loadOBJ("/models/pillar.obj");
        pillarMesh.setMaterial(pillarMaterial);
        RenderableItem pillar = new RenderableItem(pillarMesh);
        pillar.setPosition(1, 0, -5);

        //create second pillar item
        Material pillarMaterial2 = new Material(new Vector4f(0.5f, 0.5f, 0.5f, 0.9f));
        Mesh pillarMesh2 = OBJLoader.loadOBJ("/models/pillar.obj");
        pillarMesh2.setMaterial(pillarMaterial2);
        RenderableItem pillar2 = new RenderableItem(pillarMesh2);
        pillar2.setPosition(-1, 0, -5);
        this.items = new RenderableItem[] { pillar, pillar2 };
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
    }

    //Render Method
    @Override
    public void render() {
        this.renderer.render(this.window, this.camera, this.items);
    }

    //Cleanup Method
    @Override
    public void cleanup() {
        this.renderer.cleanup();
        for (RenderableItem item : this.items) item.cleanup();
    }
}
