package com.jakebarnby.drop;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Main game screen for Drop game.
 * 
 * @author Jake Barnby
 * 
 *         8 December 2014
 */
public class GameScreen implements Screen {

	private Array<Rectangle> raindrops = new Array<Rectangle>(); 	// List of the raindrops that fall
	private long dropTimeGap = 800000000; 							// Total time in game, used for controlling speed
	private float dropSpeed = 1f;									// Speed at which the raindrops fall
	private long lastDropTime; 										// Last time a raindrop was spawned

	private Texture titleImage = new Texture(Gdx.files.internal("img/drop.png"));	  // Cached title image
	private Texture dropImage = new Texture(Gdx.files.internal("img/droplet.png"));   // Cached raindrop image
	private Texture bucketImage = new Texture(Gdx.files.internal("img/bucket.png"));  // Cached bucket image
	
	private Sound dropSound; 	// Cached raindrop sound
	private Music rainMusic; 	// Cached background rain music

	private Rectangle bucket = new Rectangle();		// Contains the item to draw
	private Vector3 touchPos = new Vector3(); 		// Cached last touched position

	private SpriteBatch batch = new SpriteBatch();
	private OrthographicCamera camera = new OrthographicCamera();
	private BitmapFont mBitmapFont;
	private String score = "0";
	
	
	private Stage stage = new Stage(new FitViewport(DropGame.WIDTH, DropGame.HEIGHT), batch);
	

	@Override
	public void show() {

		if (DropGame.SOUND_ON) {
			// Load the drop sound effect and rain background music
			dropSound = Gdx.audio.newSound(Gdx.files.internal("audio/drop.wav"));
			rainMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/rain.mp3"));

			rainMusic.setLooping(true);
			rainMusic.play();
		}

		camera.setToOrtho(false, DropGame.WIDTH, DropGame.HEIGHT);

		// Sets the position of the bucket image
		bucket.x = DropGame.HEIGHT / 2 - bucketImage.getWidth() / 2;
		bucket.y = 20;
		bucket.width = bucketImage.getWidth();
		bucket.height = bucketImage.getHeight();

		// Instantiate rain drops
		spawnRaindrop();

		// Get font asset for rendering text
		FileHandle fontFile = Gdx.files.internal("fonts/RhumBanane.ttf");
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
		FreeTypeFontParameter parameters = new FreeTypeFontParameter();
		parameters.size = 70;

		mBitmapFont = generator.generateFont(parameters);
		mBitmapFont.setColor(1f, 1f, 1f, 1);
		generator.dispose();
	}

	@Override
	public void render(float delta) {
		// Clear screen and set color to blue
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		draw();
		
		stage.act(delta);
		stage.draw();
		
		// If screen is touched
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}

		// If need new raindrop
		if (TimeUtils.nanoTime() - lastDropTime > dropTimeGap) {
			spawnRaindrop();
		}

		dropSpeed += 0.00015f;
		dropTimeGap -= 30000;
	}
	
	/**
	 * Create a new raindrop and record the time it was spawned.
	 */
	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, DropGame.WIDTH - dropImage.getWidth());
		raindrop.y = DropGame.HEIGHT - titleImage.getHeight();
		raindrop.width = dropImage.getWidth();
		raindrop.height = dropImage.getHeight();
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
	
	/**
	 * 
	 */
	private void draw() {
		camera.update();
		
		// Draw bucket and raindrops
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(titleImage, 0, DropGame.HEIGHT - titleImage.getHeight());
		batch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle raindrop : raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		
		// Draw players current score
		mBitmapFont.draw(batch, score, 40, DropGame.HEIGHT - 20);
		batch.end();
		
		// Draw raindrops moving
		Iterator<Rectangle> iter = raindrops.iterator();
		while (iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= (250 * dropSpeed) * Gdx.graphics.getDeltaTime();
			// Raindrop is off screen
			if (raindrop.y + 64 < 0) {
				iter.remove();
				gameOver();
			}
			// Raindrop is caught
			if (raindrop.overlaps(bucket)) {
				if (DropGame.SOUND_ON) {
					dropSound.play();
				}
				iter.remove();
				int newScore = Integer.valueOf(score) + 1;
				score = "" + newScore;
			}
		}
	}
	
	/**
	 * 
	 */
	private void gameOver() {
		Dialog d = new DropDialog("Game Over", new Skin(						
				Gdx.files.internal("skins/menuSkin.json"),
				new TextureAtlas(Gdx.files.internal("skins/menuSkin.pack")))
		);
		
		d.show(stage);
		d.setModal(false);
		
	}


	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
}