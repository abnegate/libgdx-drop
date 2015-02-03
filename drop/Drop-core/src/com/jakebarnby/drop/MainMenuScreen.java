package com.jakebarnby.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {

	// private Texture texture = new
	// Texture(Gdx.files.internal("img/title.png"));
	// private Image titleImage = new Image(texture);

	private Stage stage = new Stage(new FitViewport(DropGame.WIDTH, DropGame.HEIGHT)); // Create stage with viewport of given virtual size

	private Skin skin = new Skin( // Create skin for menu
			Gdx.files.internal("skins/menuSkin.json"), new TextureAtlas(
					Gdx.files.internal("skins/menuSkin.pack")));

	private TextButton buttonPlay = new TextButton("Play", skin); // Create text buttons for menu options
	private TextButton buttonLeader = new TextButton("Leaderboard", skin);
	private TextButton buttonAchieve = new TextButton("Achievements", skin);
	private TextButton buttonSound = new TextButton("Sound: On", skin);
	private TextButton buttonExit = new TextButton("Quit", skin);

	private ActionResolver actionResolver;

	public MainMenuScreen(ActionResolver ar) {
		actionResolver = ar;
	}

	@Override
	public void show() {

		MenuListener menuListener = new MenuListener();
		buttonPlay.addListener(menuListener);
		buttonLeader.addListener(menuListener);
		buttonAchieve.addListener(menuListener);
		buttonSound.addListener(menuListener);
		buttonExit.addListener(menuListener);

		buttonPlay.setName("Play");
		buttonLeader.setName("Leaderboard");
		buttonAchieve.setName("Achievements");
		buttonSound.setName("Sound");
		buttonExit.setName("Exit");

		Table table = new Table();
		table.add(buttonPlay).size(DropGame.WIDTH, DropGame.HEIGHT / 15).row();
		table.add(buttonLeader).size(DropGame.WIDTH, DropGame.HEIGHT / 15).row();
		table.add(buttonAchieve).size(DropGame.WIDTH, DropGame.HEIGHT / 15).row();
		table.add(buttonSound).size(DropGame.WIDTH, DropGame.HEIGHT / 15).row();
		table.add(buttonExit).size(DropGame.WIDTH, DropGame.HEIGHT / 15).row();
		table.setFillParent(true);
		table.bottom();

		stage.addActor(table);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
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
		skin.dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	/**
	 * Used for listening and responding to button presses in the main menu of
	 * Drop
	 * 
	 * @author Jake Barnby
	 *
	 */
	private class MenuListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			if (actor.getName().equals("Play")) {
				if (!actionResolver.getSignedInGPGS()) actionResolver.loginGPGS();
				((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(actionResolver));
			} 
			else if (actor.getName().equals("Leaderboard")) {
				if (actionResolver.getSignedInGPGS()) actionResolver.getLeaderboardGPGS();
				else actionResolver.loginGPGS();
			} 
			else if (actor.getName().equals("Achievements")) {
				if (actionResolver.getSignedInGPGS()) actionResolver.getAchievementGPGS();
				else actionResolver.loginGPGS();

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
			}
		}
	}
}
