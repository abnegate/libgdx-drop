package com.jakebarnby.drop;

import com.badlogic.gdx.Game;

/**
 * Main entry point for Drop game
 * 
 * @author Jake Barnby
 *
 * 5 February 2015
 */
public class DropGame extends Game {
	
	private ActionResolver actionResolver;			//Action resolver for resolving google play game service events
	
	public static final String TITLE = "Drop";		//Title of the game
	public static final int WIDTH = 480;			//Virtual width of game screens
	public static final int HEIGHT = 800;			//Virtual height of game screens
	
	public static boolean SOUND_ON = true;			//Whether sound is enabled or not during gameplay

	/**
	 * Create a new Drop game instance
	 * @param actionResolver The ActionResolver used to resolve google play game service events
	 */
	public DropGame(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	}

	@Override
	public void create() {
		setScreen(new SplashScreen(actionResolver));
	}
}
