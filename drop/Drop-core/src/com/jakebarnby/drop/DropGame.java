package com.jakebarnby.drop;

import com.badlogic.gdx.Game;

/**
 * Main entry point for Drop game
 * @author Jake Barnby
 *
 */
public class DropGame extends Game {

	public static final String TITLE = "Drop";		//Cached title
	public static final int WIDTH = 480;			//Cached virtual width of game screens
	public static final int HEIGHT = 800;			//Cached virtual height of game screens
	
	public static boolean SOUND_ON = true;			//Global boolean determining whether sound is enabled or not during gameplay

	@Override
	public void create() {
		setScreen(new SplashScreen());
	}
}
