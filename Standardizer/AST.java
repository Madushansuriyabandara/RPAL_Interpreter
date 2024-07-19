package Standardizer;

// class AST representing the abstract syntax tree
public class AST {
    private Node root;
    
    public AST(Node root) {
        this.setRoot(root);
    }
    
    public void setRoot(Node root) {
        this.root = root;
    }
    
    public Node getRoot() {
        return this.root;
    }
    
    public void standardize() {  
        if (!this.root.isStandardized) {
            this.root.standardize();
        }
    }
    
    // helper method to print the abstract syntax tree
    private void preOrderTraverse(Node node,int i) {
        for (int n = 0; n < i; n++) {System.out.print(".");}
        System.out.println(node.getData());
        node.children.forEach((child) -> preOrderTraverse(child, i+1));
    }
    
    // method to print the abstract syntax tree
    public void printAst() {
        this.preOrderTraverse(this.getRoot(), 0);
    }
}