package game;

import engine.graphics.OBJLoader;
import engine.graphics.renderable.Material;
import engine.graphics.renderable.Mesh;
import engine.graphics.renderable.RenderableItem;
import engine.graphics.renderable.Texture;
import engine.utils.Node;
import engine.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Area {

    //Data
    private List<RenderableItem> items;

    //Default Constructor
    public Area() {
        this.items = new ArrayList<>();
    }

    /**
     * Constructs the area by taking in a map name and loading the area from the corresponding file within the area
     * directory (/data/areas/)
     * @param areaName the name of the area to be loaded. Must be the same name as the file to load (w/o the extension)
     */
    public Area(String areaName) {
        this();
        this.loadMap("data/areas/" + areaName + ".wdr");
    }

    /**
     * Loads this area from the given file directory
     * @param areaDir the directory to load the area from
     */
    private void loadMap(String areaDir) {

        //load data from file
        Node areaData = Node.readNode(areaDir);
        Node keyData = areaData.getChild("key");

        //construct tile key map
        HashMap<Character, Mesh> tileKey = new HashMap<>();
        for (Node tile : keyData.getChildren()) {
            if (tile.getValue().equals("[EMPTY]")) {
                tileKey.put(tile.getName().charAt(0), null);
            } else {
                tileKey.put(tile.getName().charAt(0), loadTile("data/tiles/" + tile.getValue() + ".wdr"));
            }
        }

        //construct map
        Node layoutData = areaData.getChild("layout");
        int width = Integer.parseInt(layoutData.getChild("width").getValue());
        int height = Integer.parseInt(layoutData.getChild("height").getValue());
        for (int y = 0; y < height; y++) {
            Node row = layoutData.getChild("row " + (y + 1));
            for (int x = 0; x < width; x++) {
                Mesh mesh = tileKey.get(row.getValue().charAt(x));
                if (mesh != null) {
                    RenderableItem item = new RenderableItem(mesh);
                    item.setPosition(x + 5, 0, y);
                    this.items.add(item);
                }
            }
        }
    }

    /**
     * Loads a mesh by loading its details from the given directory
     * @param tileDir the directory of the tile mesh to load
     * @return the loaded mesh
     */
    private Mesh loadTile(String tileDir) {

        //load data from file
        Node data = Node.readNode(tileDir);

        //construct mesh
        try {
            Material material = new Material(new Texture("/textures/" + data.getChild("texture").getValue()));
            Mesh mesh = OBJLoader.loadOBJ("/models/" + data.getChild("model").getValue());
            mesh.setMaterial(material);
            return mesh;
        } catch (IOException e) {
            Utils.log(e, "game.logic.WorldLogic");
            e.printStackTrace();
        }

        //return null if couldn't load
        return null;
    }

    //Accessors
    public List<RenderableItem> getItems() { return this.items; }
}
