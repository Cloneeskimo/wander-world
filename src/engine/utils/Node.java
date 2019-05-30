package engine.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//Info Codes Used: 0 - 1

/**
 * Holds a singular piece of data, and can hold children nodes with their own data. Has the capability to be
 * written and loaded from files, allowing for easy and safe data saving/loading
 */
public class Node {

    //Static Data
    private static char INDENT_CHAR = '\t';
    private static char DIVIDER_CHAR = ':';

    //Data
    private String name;
    private String value;
    private List<Node> children;

    //Full Constructor
    public Node(String name, String value, List<Node> children) {
        this.name = name;
        this.value = value;
        this.children = children;
    }

    //Single Child Constructor
    public Node(String name, String data, Node child) {
        this(name, data, new ArrayList<>());
        this.children.add(child);
    }

    //No Child Constructor
    public Node(String name, String value) {
        this.name = name;
        this.value = value;
    }

    //Name Constructor
    public Node(String name) {
        this.name = name;
    }

    //Default Constructor
    public Node() {}

    //Children Manipulation Methods
    public List<Node> getChildren() { return this.children; }
    public int getChildCount() { return this.children.size(); }

    public void addChild(Node child) {
        if (this.children == null) this.children = new ArrayList<>();
        this.children.add(child);
    }

    public void addChildren(List<Node> children) {
        if (this.children == null) this.children = new ArrayList<>();
        if (children == null) return;
        for (Node child : children) this.children.add(child);
    }

    public Node getChild(int index) {
        if (index > this.children.size()) {
            Utils.log("Unable to access index " + index + " in child array of size " + this.children.size() +
                    ", returning null", "engine.utils.Node", 0, true);
            return null;
        }
        return this.children.get(index);
    }

    public Node getChild(String name) {
        for (Node child : this.children) if (child.getName().equals(name)) return child;
        Utils.log("Unable to access child with name '" + name + "', returning null", "engine.utils.Node",
                1, true);
        return null;
    }

    //Accessors
    public String getName() { return this.name; }
    public String getValue() { return this.value; }
    public boolean hasName() { return this.name != null; }
    public boolean hasValue() { return this.value != null; }
    public boolean hasChildren() {
        if (this.children == null) return false;
        if (this.children.size() < 1) return false;
        return true;
    }

    //Mutators
    public void setValue(String value) { this.value = value; }
    public void setName(String name) { this.name = name; }

    /**
     * Writes a node to a file
     * @param node the Node to write
     * @param path the path to write the Node to. Will throw an error if doesn't exit or cannot open
     */
    public static void writeNode(Node node, String path) {

        //ensure directories
        int lastSlash = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') lastSlash = i;
        }
        Utils.ensureDirectory(path.substring(0, lastSlash));

        //try to open file to print
        try {
            PrintWriter out = new PrintWriter(new File(path));

            //recursively save node then close file
            Node.writeNodeR(out, node, new StringBuilder());
            out.close();

        //catch errors
        } catch (FileNotFoundException e) {
            Utils.log(e, "engine.utils.Node");
            e.printStackTrace();
        }
    }

    /**
     * Recursively write a node to a file
     * @param out the PrintWriter to use for writing
     * @param node the current node in focus
     * @param indent the current indent to use
     */
    private static void writeNodeR(PrintWriter out, Node node, StringBuilder indent) {

        //print name and date
        String indentString = indent.toString();
        out.print(indentString + (node.hasName() ? node.getName() : "") + Node.DIVIDER_CHAR + " ");
        out.println(node.hasValue() ? node.getValue() : "");

        //print children
        if (node.hasChildren()) {
            out.println(indentString + "{");
            indent.append(Node.INDENT_CHAR);
            for (Node child : node.getChildren()) writeNodeR(out, child, indent);
            indent.deleteCharAt(indent.length() - 1);
            out.println(indentString + "}");
        }
    }

    /**
     * Reads a node from a file
     * @param path the path to read the Node from. Will throw an error if does not exist or cannot be opened
     * @return the read Node
     */
    public static Node readNode(String path) {

        //create node
        Node node = new Node();

        //open file
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            List<String> fileContents = new ArrayList<>();

            //put file into an arraylist and then recursively parse it
            while (in.ready()) fileContents.add(in.readLine());
            readNodeR(node, fileContents, 0, 0);

            //catch any errors
        } catch (Exception e) {
            Utils.log(e, "engine.utils.Node");
            e.printStackTrace();
        }

        //return node
        return node;
    }

    /**
     * Recursively reads a Node from a file
     * @param node the current Node in focus
     * @param fileContents the recursively static file contents
     * @param i the current line of fileContents in focus
     * @param indent the current indent in terms of number of characters
     * @return the node in focus and its recursively read children
     */
    private static int readNodeR(Node node, List<String> fileContents, int i, int indent) {

        //format next line and find dividing point
        String nextLine = fileContents.get(i); //get line
        nextLine = nextLine.substring(indent, nextLine.length()); //remove indent
        int dividerLocation = -1; //location of the divider in line
        for (int j = 0; j < nextLine.length() && dividerLocation == -1; j++)
            if (nextLine.charAt(j) == Node.DIVIDER_CHAR) dividerLocation = j; //find divider

        //throw error if no divider found
        if (dividerLocation == -1) {
            IllegalStateException e = new IllegalStateException("Could not find divider in line: '" + nextLine + "'");
            Utils.log(e, "engine.utils.Node");
            throw e;
        }

        //create node and set name
        Node curr = new Node();
        String possibleName = nextLine.substring(0, dividerLocation);
        if (!possibleName.equals("")) curr.setName(nextLine.substring(0, dividerLocation)); //create node with name

        //set node value if there is one
        String possibleValue = nextLine.substring(dividerLocation + 1, nextLine.length()); //grab possible value
        if (!possibleValue.equals(" ") && !possibleValue.equals("")) { //if possible value has substance
            curr.setValue(possibleValue.substring(1, possibleValue.length())); //set value (remove first space space)
        }

        //check for more file
        if (i + 1 <= fileContents.size()) { //if not eof

            //check for child nodes
            if (fileContents.get(i + 1).contains("{")) { //if the node has children
                i += 2; //iterate twice
                indent++; //iterate indent
                while (!fileContents.get(i).contains("}")) { //while there are more children

                    //add child
                    Node child = new Node(); //create child node
                    i = readNodeR(child, fileContents, i, indent); //recursively read child, keep track of file position
                    curr.addChild(child); //add child

                    //throw error if file suddenly stops
                    if ((i + 1) > fileContents.size()) {
                        IllegalStateException e = new IllegalStateException("Unexpected stop in file at line " + i);
                        Utils.log(e, "engine.utils.Node");
                        throw e;
                    }

                    //iterate i
                    i += 1;
                }
            }
        }

        //set node, return current position in file
        node.setName(curr.getName());
        node.setValue(curr.getValue());
        node.addChildren(curr.getChildren());
        return i;
    }
}
