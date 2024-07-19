package Standardizer;

import java.util.ArrayList;

// class ASTFactory to create an abstract syntax tree from the data
public class ASTFactory {
    
    public ASTFactory() {}
    
    // method to create an abstract syntax tree from the data
    public AST getAST(ArrayList<String> data) {
        Node root = NodeFactory.getNode(data.get(0), 0);
        Node previous_node = root;
        int current_depth = 0;
        // iterate through the data to create the abstract syntax tree
        for (String s: data.subList(1, data.size())) {
            int i = 0;
            int d = 0;                                                          
            while (s.charAt(i) == '.') { 
                d++; 
                i++; 
            }            
            Node current_node = NodeFactory.getNode(s.substring(i), d); 
            
            if (current_depth < d) {
                previous_node.children.add(current_node);
                current_node.setParent(previous_node);               
            } else {
                while (previous_node.getDepth() != d) {
                    previous_node = previous_node.getParent();
                }
                previous_node.getParent().children.add(current_node);
                current_node.setParent(previous_node.getParent());
            }
            previous_node = current_node;
            current_depth = d;
        }
        // return the abstract syntax tree
        return new AST(root);
    }
}