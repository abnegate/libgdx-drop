package com.jakebarnby.drop;

import com.badlogic.gdx.Game;

public class DropGame extends Game {

	public static final String TITLE = "Drop";
	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;
	
	public static boolean SOUND_ON = true;

	@Override
	public void create() {
		setScreen(new SplashScreen());
	}
}
