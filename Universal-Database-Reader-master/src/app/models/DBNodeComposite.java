package app.models;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

public abstract class DBNodeComposite extends DBNode {
	
	private List<DBNode> children;

	public DBNodeComposite(String name, DBNode parent) {
		super(name, parent);

		children = new ArrayList<>();
	}
	
	public DBNodeComposite(List<DBNode> children) {
		this.children = children;
	}
	
	public abstract void addChild(DBNode child);

	public List<DBNode> getChildren() {
		return children;
	}

	public void setChildren(List<DBNode> children) {
		this.children = children;
	}

	public DBNode getChildByName(String name) {
		List<DBNode> children = getChildren();

		for (DBNode child : children)
			if (child.toString().equals(name))
				return child;

		return null;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public Enumeration<? extends TreeNode> children() {
		return null;
	}
}
