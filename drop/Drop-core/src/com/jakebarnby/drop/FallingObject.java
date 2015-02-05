package com.jakebarnby.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * A raindrop that falls down the screen in Drop game
 * @author Jake Barnby
 *
 */
public class FallingObject extends Rectangle {
	
	private Texture dropletImage;		//The image of the raindrop to draw
	private Type type;

	public enum Type {
		RAINDROP,
		BOMB,
		HAIL,
	}

	/**
	 * Creates a new raindrop with the given image and determines whether its flaming or not
	 * @param imageName The name of the image to load for the raindrop
	 * @param isFlaming Whether this raindrop is flaming or not
	 */
	public FallingObject(String imageName, Type type) {
		dropletImage = new Texture(Gdx.files.internal("img/" + imageName));
		this.type = type;
	}
	
	/**
	 * Draw raindrop with the given SpriteBatch
	 * @param batch The batch used to draw this raindrop
	 */
	public void draw(SpriteBatch batch) {
		batch.draw(dropletImage, x, y);
	}
	
	/**
	 * Get the raindrops image
	 * @return The image for this raindrop
	 */
	public Texture getImage() {
		return dropletImage;
	}
	
	/**
	 * Get the type of this falling object
	 * @return The type of this falling object
	 */
	public Type getType() {
		return type;
	}
}
