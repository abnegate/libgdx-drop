package com.jakebarnby.drop.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.jakebarnby.drop.ActionResolver;
import com.jakebarnby.drop.DropGame;

/**
 * Main menu screen of Drop game
 * 
 * @author Jake Barnby
 * 
 * 5 February 2015
 *
 */
public class MainMenuScreen implements Screen {

	// Create stage to display title image and menu buttons
	private Stage stage = new Stage(new StretchViewport(DropGame.WIDTH, DropGame.HEIGHT));
	
	//Create title image
	private Texture texture = new Texture(Gdx.files.internal("img/menu_title.png"));
	private Image menuImage = new Image(texture);

	//Create skin for menu
	private Skin skin = new Skin( 
			Gdx.files.internal("skins/menuSkin.json"), new TextureAtlas(
					Gdx.files.internal("skins/menuSkin.pack")));

	// Create text buttons for menu options
	private TextButton buttonPlay = new TextButton("Play", skin); 
	private TextButton buttonLeader = new TextButton("Leaderboard", skin);
	private TextButton buttonAchieve = new TextButton("Achievements", skin);
	private TextButton buttonSound = new TextButton("Sound: On", skin);
	private TextButton buttonExit = new TextButton("Quit", skin);

	//Action Resolver for resolving google play game service events
	private ActionResolver actionResolver;

	/**
	 * Create a new main menu screen
	 * @param actionResolver The ActionResolver used to resolve google play game service events
	 */
	public MainMenuScreen(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	}

	@Override
	public void show() {
		Gdx.input.setCatchBackKey(true);
		
		//If sound is off make sure button reflects this
		if (!DropGame.SOUND_ON) {
			buttonSound.setText("Sound: Off");
		}
		
		//Add a button listener to all the menu buttons
		MenuListener menuListener = new MenuListener();
		buttonPlay.addListener(menuListener);
		buttonLeader.addListener(menuListener);
		buttonAchieve.addListener(menuListener);
		buttonSound.addListener(menuListener);
		buttonExit.addListener(menuListener);
		
		//Set names of all buttons for dealing with presses
		buttonPlay.setName("Play");
		buttonLeader.setName("Leaderboard");
		buttonAchieve.setName("Achievements");
		buttonSound.setName("Sound");
		buttonExit.setName("Exit");

		//Add all buttons to a table and add table to the stage
		Table table = new Table();
		table.add(buttonPlay).size(DropGame.WIDTH, DropGame.HEIGHT / 15).row();
		table.add(buttonLeader).size(DropGame.WIDTH, DropGame.HEIGHT / 15).row();
		table.add(buttonAchieve).size(DropGame.WIDTH, DropGame.HEIGHT / 15).row();
		table.add(buttonSound).size(DropGame.WIDTH, DropGame.HEIGHT / 15).row();
		table.add(buttonExit).size(DropGame.WIDTH, DropGame.HEIGHT / 15).row();
		table.setFillParent(true);
		table.bottom();
		
		menuImage.setPosition(0, DropGame.HEIGHT - menuImage.getHeight());

		stage.addActor(table);
		stage.addActor(menuImage);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
		
		if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.HOME)) {
			Gdx.app.exit();
			actionResolver.showInterstitial();
		}
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		stage.dispose();
		texture.dispose();
		skin.dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	/**
	 * Used for listening and responding to button presses in the main menu of Drop
	 * 
	 * @author Jake Barnby
	 *
	 */
	private class MenuListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			if (actor.getName().equals("Play")) {
				if (!actionResolver.getSignedInGPGS()) actionResolver.loginGPGS();
				
				//If user hasn't played before, show a how to play screen
				Preferences prefs = Gdx.app.getPreferences("HOWTOPLAY");
				if (!prefs.getBoolean("Shown")) {
					((Game) Gdx.app.getApplicationListener()).setScreen(new HowToPlayScreen(actionResolver));
					prefs.putBoolean("Shown",true);
					prefs.flush();
				} else {
					((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(actionResolver));
				}
			} 
			else if (actor.getName().equals("Leaderboard")) {
				if (actionResolver.getSignedInGPGS()) {
					actionResolver.getLeaderboardGPGS();
				}
			} 
			else if (actor.getName().equals("Achievements")) {
				actionResolver.getAchievementGPGS();
			}
			else if (actor.getName().equals("Sound")) {
				if (DropGame.SOUND_ON) {
					DropGame.SOUND_ON = false;
					((TextButton) actor).setText("Sound: Off");
				} else {
					DropGame.SOUND_ON = true;
					((TextButton) actor).setText("Sound: On");
				}
			} else if ((actor.getName().equals("Exit"))) {
				Gdx.app.exit();
				actionResolver.showInterstitial();
			}
		}
	}
}
