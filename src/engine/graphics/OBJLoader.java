package engine.graphics;

import engine.graphics.renderable.Mesh;
import engine.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

//Error Codes Used: 0

public class OBJLoader {

    /**
     * Loads an .obj file into a Mesh
     * @param resourcePath resource path of the .obj file to load
     * @return the Mesh created from the .obj file
     */
    public static Mesh loadOBJ(String resourcePath) {

        //read in entire file
        List<String> file = Utils.loadResourceIntoStringList(resourcePath); //file

        //create arraylists for the things to read from the obj file
        List<Vector3f> positions = new ArrayList<>(); //positions
        List<Vector3f> normals = new ArrayList<>(); //normal vectors
        List<Vector2f> texCoords = new ArrayList<>(); //texture coordinates
        List<Face> faces = new ArrayList<>(); //faces

        //parse lines of file
        for (String line : file) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v": //position
                    Vector3f vPosition = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    positions.add(vPosition);
                    break;
                case "vn": //normal vector
                    Vector3f vNormalVector = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    normals.add(vNormalVector);
                    break;
                case "vt": //texture coordinate
                    Vector2f vTextureCoordinate = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]));
                    texCoords.add(vTextureCoordinate);
                    break;
                case "f": //face
                    faces.add(new Face(tokens[1], tokens[2], tokens[3]));
                    break;
                default: //other
                    Utils.error("Ignoring unexpected line of .obj file: " + line,
                            "engine.graphics.OBJLoader", 0, Utils.INFO);
                    break;
            }
        }

        //reorder lists then return them
        return reorderLists(positions, normals, texCoords, faces);
    }

    /**
     * Reoorders given ArrayLists of mesh data into the correct OpenGL order to create the mesh object
     * @param positions the positions list
     * @param normals the normal vectors list
     * @param texCoords the texture coordinates list
     * @param faces the faces list
     * @return the created Mesh object after reordering and using the given lists
     */
    private static Mesh reorderLists(List<Vector3f> positions, List<Vector3f> normals, List<Vector2f> texCoords,
                                     List<Face> faces) {
        //create indices list
        List<Integer> indices = new ArrayList<>();

        //create arrays
        float[] positionsArr = new float[positions.size() * 3]; //positions
        float[] texCoordsArr = new float[positions.size() * 2]; //texture coordinates
        float[] normalsArr = new float[positions.size() * 3]; //normal vectors

        //fill positions array in the order it has been received
        int i = 0;
        for (Vector3f position : positions) {
            positionsArr[i * 3] = position.x;
            positionsArr[i * 3 + 1] = position.y;
            positionsArr[i * 3 + 2] = position.z;
            i++;
        }

        //process faces
        for (Face face : faces) {
            IndexGroup[] indexGroups = face.getIndexGroups();
            for (IndexGroup indexGroup : indexGroups) processIndexGroup(indexGroup, texCoords, normals, indices,
                    texCoordsArr, normalsArr);
        }

        //convert indices list into an array
        int[] indicesArr = new int[indices.size()];
        indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();

        //create and return mesh
        return new Mesh(positionsArr, texCoordsArr, normalsArr, indicesArr);
    }

    /**
     * Processes a single IndexGroup and puts its data into the correct arrays
     * @param idxGroup the IndexGroup to process
     * @param texCoords the list of texture coordinates to pull from
     * @param normals the list of normal vectors to pull from
     * @param indices the array of indices to add to
     * @param texCoordsArr the array of texture coordinates to add to
     * @param normalsArr the array of normal vectors to add to
     */
    private static void processIndexGroup(IndexGroup idxGroup, List<Vector2f> texCoords, List<Vector3f> normals,
                                          List<Integer> indices, float[] texCoordsArr, float[] normalsArr) {
        //get index of position
        int posIndex = idxGroup.posIndex;
        indices.add(posIndex);

        //reorder texture coordinate
        if (idxGroup.texCoordIndex != IndexGroup.NO_VALUE) {
            Vector2f textureCoordinate = texCoords.get(idxGroup.texCoordIndex);
            texCoordsArr[posIndex * 2] = textureCoordinate.x;
            texCoordsArr[posIndex * 2 + 1] = 1 - textureCoordinate.y;
        }

        //reorder normal vectors
        if (idxGroup.normalIndex != IndexGroup.NO_VALUE) {
            Vector3f normalVector = normals.get(idxGroup.normalIndex);
            normalsArr[posIndex * 3] = normalVector.x;
            normalsArr[posIndex * 3 + 1] = normalVector.y;
            normalsArr[posIndex * 3 + 2] = normalVector.z;
        }
    }

    /**
     *  Holds and represents data for a single face of an OBJ file/mesh
     */
    private static class Face {

        //Index Group Data
        private IndexGroup[] idxGroups = new IndexGroup[3];

        /**
         * @param group1 - the first index group as a token string
         * @param group2 - the second index group as a token string
         * @param group3 - the third index group as a token string
         */
        public Face(String group1, String group2, String group3) {
            idxGroups[0] = parseGroup(group1);
            idxGroups[1] = parseGroup(group2);
            idxGroups[2] = parseGroup(group3);
        }

        /**
         * Parses a single index group string (i.e. 5//5//5)
         * @param groupString the group to parse as a string
         * @return the parsed group
         */
        private IndexGroup parseGroup(String groupString) {

            //create index group and split group string into tokens
            IndexGroup idxGroup = new IndexGroup(); //index group
            String[] ts = groupString.split("/"); //tokens
            int l = ts.length; //length

            //get position index
            idxGroup.posIndex = Integer.parseInt((ts[0])) - 1;

            //get texture coordinate index
            if (l > 1) {
                String textureCoordinate = ts[1];
                idxGroup.texCoordIndex = textureCoordinate.length() > 0 ? Integer.parseInt(textureCoordinate) - 1
                        : IndexGroup.NO_VALUE;
            }

            //get normal vector index
            if (l > 2) idxGroup.normalIndex = Integer.parseInt(ts[2]) - 1;

            //return created group
            return idxGroup;
        }

        //Accessor
        public IndexGroup[] getIndexGroups() { return this.idxGroups; }
    }

    /**
     * Holds and represents data for one index group of a face
     * there are 3 index groups in one face
     */
    private static class IndexGroup {

        //Static Data
        public static final int NO_VALUE = -1;

        //Data
        public int posIndex;
        public int texCoordIndex;
        public int normalIndex;

        //Constructor
        public IndexGroup() {
            this.posIndex = this.texCoordIndex = this.normalIndex = IndexGroup.NO_VALUE;
        }
    }
}
