package com.jakebarnby.drop;

public class GoldDrop extends FallingObject {
	
	private static final long serialVersionUID = 5010440322240743061L;
	private boolean active = false;
	private float timeRemaining = 20;

	/**
	 * Creates a new gold drop
	 * @param imageName The name of the image of this falling object
	 * @param type The type of this falling object
	 */
	public GoldDrop(String imageName, Type type) {
		super(imageName, type);
	}
	
	/**
	 * 
	 * @param delta
	 */
	public void update(float delta) {
		if (active) {
			timeRemaining -= delta;
			if (timeRemaining < 0) active = false;
		}
	}

	/**
	 * Get whether this gold drop is active or not
	 * @return Whether this gold drop is active
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Set whether this gold drop is active or not
	 * @param active Whether this gold drop is active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
}
