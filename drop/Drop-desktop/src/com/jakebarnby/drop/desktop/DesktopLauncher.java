package com.jakebarnby.drop.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jakebarnby.drop.DropGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = DropGame.WIDTH;
		config.height = DropGame.HEIGHT;
		new LwjglApplication(new DropGame(new ActionResolverDesktop()), config);
	}
}
