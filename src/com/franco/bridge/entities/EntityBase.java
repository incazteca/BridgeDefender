package com.franco.bridge.entities;

public abstract class EntityBase {
	
	// Create attributes for the entity dimension and positions
	
	private int entityHeight;
	private int entityLength;
	
	private int entityPositionX;
	private int entityPositionY;
	
	public EntityBase(int height, int length, int posX, int posY){
		entityHeight = height;
		entityLength = length;
		entityPositionX = posX;
		entityPositionY = posY;
	}

	
	/*
	 * Getters and Setters follow for the attributes
	 */
	
	public int getEntityHeight() {
		return entityHeight;
	}

	public void setEntityHeight(int entityHeight) {
		this.entityHeight = entityHeight;
	}

	public int getEntityLength() {
		return entityLength;
	}

	public void setEntityLength(int entityLength) {
		this.entityLength = entityLength;
	}

	public int getEntityPositionX() {
		return entityPositionX;
	}

	public void setEntityPositionX(int entityPositionX) {
		this.entityPositionX = entityPositionX;
	}

	public int getEntityPositionY() {
		return entityPositionY;
	}

	public void setEntityPositionY(int entityPositionY) {
		this.entityPositionY = entityPositionY;
	}
	
	
	
}
