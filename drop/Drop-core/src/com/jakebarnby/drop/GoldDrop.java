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
	 * Countdown time until gold runs out
	 * @param delta The time passed in seconds since this method was last called
	 */
	public void update(float delta) {
		if (active) {
			timeRemaining -= delta;
			if (timeRemaining < 0) active = false;
			System.out.println(timeRemaining);
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
	
	/**
	 * Get the time remaining for this gold drop
	 * @return The time remaining for this gold drop in seconds
	 */
	public float getTimeRemaining() {
		return timeRemaining;
	}
}
