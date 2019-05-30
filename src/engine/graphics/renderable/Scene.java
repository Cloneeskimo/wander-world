package engine.graphics.renderable;

import engine.graphics.lighting.SceneLighting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    //Data
    private Map<Mesh, List<RenderableItem>> meshes;
    private SceneLighting lighting;

    //Default Constructor
    public Scene() { this.meshes = new HashMap<>(); }

    //Accessors
    public Map<Mesh, List<RenderableItem>> getMeshes() { return this.meshes; }
    public SceneLighting getLighting() { return this.lighting; }

    //Mutators
    public void setLighting(SceneLighting lighting) { this.lighting = lighting; }

    /**
     * Will add items to the scene. If the items share meshes with previously added items, the previously
     * added items will be removed. To avoid this, add all items that share the same mesh at once
     * @param items the items to be added
     */
    public void addItems(RenderableItem[] items) {

        //sort game items by mesh for optimal rendering
        int n = items != null ? items.length : 0;
        for (int i = 0; i < n; i ++) {
            RenderableItem item = items[i];
            Mesh m = item.getMesh();
            List<RenderableItem> l = meshes.get(m);
            if (l == null) {
                l = new ArrayList<>();
                meshes.put(m, l);
            }
            l.add(item);
        }
    }

    //Cleanup Method
    public void cleanup() {
        for (Mesh mesh : this.meshes.keySet()) mesh.cleanup();
    }
}
