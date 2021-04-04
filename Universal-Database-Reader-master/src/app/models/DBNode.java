package app.models;

import javax.swing.tree.TreeNode;

public abstract class DBNode implements TreeNode {

	private String name;

	private DBNode parent;

	public DBNode() {
		super();
	}

	public DBNode(String name, DBNode parent) {
		super();
		this.name = name;
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DBNode getParent() {
		return parent;
	}

	public void setParent(DBNode parent) {
		this.parent = parent;
	}


}
