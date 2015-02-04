package com.jakebarnby.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Raindrop extends Rectangle {
	
	private Texture dropletImage;
	private boolean isFlaming;

	public Raindrop(String imageName, boolean isFlaming) {
		dropletImage = new Texture(Gdx.files.internal("img/" + imageName));
		this.isFlaming = isFlaming;
	}
	
	public void draw(SpriteBatch batch) {
		batch.draw(dropletImage, x, y);
	}
	
	public Texture getDropletImage() {
		return dropletImage;
	}
	
	public boolean isFlaming() {
		return isFlaming;
	}
}
