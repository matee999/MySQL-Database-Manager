package app.models;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import app.resource.ConstraintType;

public class AttributeConstraint extends DBNode {

	private ConstraintType constraintType;

	public AttributeConstraint(String name, DBNode parent, ConstraintType constraintType) {
		super(name,parent);
		this.constraintType = constraintType;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}

	public ConstraintType getConstraintType() {
		return constraintType;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public int getIndex(TreeNode node) {
		return 0;
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public Enumeration<? extends TreeNode> children() {
		return null;
	}
}
