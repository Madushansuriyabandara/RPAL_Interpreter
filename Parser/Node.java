package Parser;

// class node to store the type, value and number of children of a node
public class Node {
	public NodeType type;
	public String value;
	public int noOfChildren;
	
	public Node(NodeType type, String value, int children) {
		this.type = type;
		this.value = value;
		this.noOfChildren = children;
	}

	public NodeType getType() {
		return this.type;
	}

	public String getvalue() {
		return this.value;
	}

	public int getchildren() {
		return this.noOfChildren;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setChildren(int children) {
		this.noOfChildren = children;
	}
}