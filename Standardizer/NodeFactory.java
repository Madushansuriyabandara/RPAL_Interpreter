package Standardizer;

import java.util.ArrayList;

// class NodeFactory to create a node
public class NodeFactory {
    
    public NodeFactory() {}
    
    // method to create a node using the data and depth
    public static Node getNode(String data, int depth) {
        Node node = new Node();
        node.setData(data);
        node.setDepth(depth);
        node.children = new ArrayList<Node>();
        return node;
    }
    
    //  method to create a node using the data, depth, parent, children, and standardization status
    public static Node getNode(String data, int depth, Node parent, ArrayList<Node> children, boolean isStandardize) {
        Node node = new Node();
        node.setData(data);
        node.setDepth(depth);
        node.setParent(parent);
        node.children = children;
        node.isStandardized = isStandardize;
        return node;
    }
}