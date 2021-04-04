package app.models;

import app.resource.AttributeType;

public class Attribute extends DBNodeComposite {

	private AttributeType attributeType;
	private Attribute inRelationWith;
	
	public Attribute(String name, DBNode parent, AttributeType attributeType) {
		super(name, parent);
		this.attributeType = attributeType;
	}

	@Override
	public void addChild(DBNode child) {
		if(child instanceof AttributeConstraint) {
				AttributeConstraint constraint =(AttributeConstraint) child;
				this.getChildren().add(constraint);
		}
	}

	public AttributeType getAttributeType() {
		return attributeType;
	}

	public Attribute getInRelationWith() {
		return inRelationWith;
	}

	public void setInRelationWith(Attribute inRelationWith) {
		this.inRelationWith = inRelationWith;
	}
}
