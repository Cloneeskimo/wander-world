package engine.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//Error Codes Used: 0 - 6

public class Node {

    //Static Data
    private static char INDENT_CHAR = '\t';
    private static char DIVIDER_CHAR = ':';

    //Data
    private String name;
    private String value;
    private List<Node> children;

    //Constructors
    public Node(String name, String value, List<Node> children) { //full
        this.name = name;
        this.value = value;
        this.children = children;
    }

    public Node(String name, String data, Node child) { //constructor with single child
        this(name, data, new ArrayList<>());
        this.children.add(child);
    }

    public Node(String name, String value) { //constructor with no children
        this.name = name;
        this.value = value;
    }

    public Node(String name) { //constructor with just name
        this.name = name;
    }

    public Node() {} //default constructor

    //Children Manipulation
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
            Utils.error("Unable to access index " + index + " in array of size " + this.children.size() +
                    ", returning null", "engine.utils.Node", 0, Utils.WARNING);
            return null;
        }
        return this.children.get(index);
    }

    public Node getChild(String name) {
        for (Node child : this.children) if (child.getName().equals(name)) return child;
        Utils.error("Unable to access child with name '" + name + "', returning null",
                "engine.utils.Node", 1, Utils.WARNING);
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

    //Static Writing Method
    public static void writeNode(Node node, String path) {

        //try to open file to print
        try {
            PrintWriter out = new PrintWriter(new File(path));

            //recursively save node then close file
            Node.writeNodeR(out, node, new StringBuilder());
            out.close();

        //catch errors
        } catch (Exception ex) {
            Utils.error("Unable to open file for node saving: " + ex.getMessage(),
                    "engine.utils.Node", 2, Utils.FATAL);
        }
    }

    //Recursive Node Writing Method
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

    //Static Reading Method
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
            Utils.error("Unable to open file '" + path + "' for node saving: " + e.getMessage(),
                    "engine.utils.Node", 3, Utils.FATAL);
        }

        //return node
        return node;
    }

    //Recursive Node Reading Method
    private static int readNodeR(Node node, List<String> fileContents, int i, int indent) {

        //format next line and find dividing point
        String nextLine = fileContents.get(i); //get line
        nextLine = nextLine.substring(indent, nextLine.length()); //remove indent
        int dividerLocation = -1; //location of the divider in line
        for (int j = 0; j < nextLine.length() && dividerLocation == -1; j++)
            if (nextLine.charAt(j) == Node.DIVIDER_CHAR) dividerLocation = j; //find divider

        //throw error if no divider found
        if (dividerLocation == -1) Utils.error("Unable to find divider in line: '" + nextLine + "'",
                "engine.utils.Node", 4, Utils.FATAL);

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
                    if ((i + 1) > fileContents.size()) Utils.error("unexpected stop in file at line " + i,
                            "engine.utils.Node", 5, Utils.FATAL);

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
