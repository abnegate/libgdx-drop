package com.jakebarnby.drop;

import java.util.Iterator;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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
import com.jakebarnby.drop.FallingObject.Type;

/**
 * Main game screen for Drop game.
 * 
 * @author Jake Barnby
 * 
 * 8 December 2014
 */
public class GameScreen implements Screen {

	private Array<FallingObject> fallingObjects = new Array<FallingObject>(); 	// List of the raindrops that fall
	private long dropTimeGap = 800000000; 							// Total time in game, used for controlling speed
	private float dropSpeed = 1f;									// Speed at which the raindrops fall
	private long lastDropTime; 										// Last time a raindrop was spawned

	private Texture titleImage = new Texture(Gdx.files.internal("img/drop.png"));	  // Cached title image
	private Texture bucketImage = new Texture(Gdx.files.internal("img/bucket.png")); 
	private Texture bigBucket = new Texture(Gdx.files.internal("img/bucket_gold.png"));
	private Texture grassImage = new Texture(Gdx.files.internal("img/grass.png"));
	
	private ParticleEffect water = new ParticleEffect();
	private ParticleEffect fire = new ParticleEffect();
	private ParticleEffect ice = new ParticleEffect();
	private ParticleEffect gold = new ParticleEffect();
	
	
	private Sound dropSound; 	// Cached raindrop sound
	private Sound bombSound;
	private Sound freezeSound;
	private Music rainMusic; 	// Cached background rain music

	private Rectangle bucket = new Rectangle();		// Contains the item to draw
	private Vector3 touchPos = new Vector3(); 		// Cached last touched position

	private SpriteBatch batch = new SpriteBatch();
	private OrthographicCamera camera = new OrthographicCamera();
	private BitmapFont mBitmapFont;
	private String score = "0";
	
	private Stage stage = new Stage(new FitViewport(DropGame.WIDTH, DropGame.HEIGHT), batch);
	private ActionResolver actionResolver;
	private GoldDrop goldDrop;
	
	private boolean gameOver = false;
	
	/**
	 * 
	 * @param actionResolver
	 */
	public GameScreen(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	}

	@Override
	public void show() {

		if (DropGame.SOUND_ON) {
			// Sound is turned on, load sounds and music
			bombSound = Gdx.audio.newSound(Gdx.files.internal("audio/explosion.wav"));
			dropSound = Gdx.audio.newSound(Gdx.files.internal("audio/drop.wav"));
			freezeSound = Gdx.audio.newSound(Gdx.files.internal("audio/freeze.wav"));
			rainMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/rain.mp3"));
			rainMusic.setLooping(true);
			rainMusic.play();
		}

		camera.setToOrtho(false, DropGame.WIDTH, DropGame.HEIGHT);

		// Sets the position of the bucket image
		bucket.x = DropGame.WIDTH / 2 - bucketImage.getWidth() / 2;
		bucket.y = grassImage.getHeight()+10;
		bucket.width = bucketImage.getWidth();
		bucket.height = bucketImage.getHeight();

		// Instantiate rain drops
		spawnFallingOjbect();

		// Get font asset for rendering text
		FileHandle fontFile = Gdx.files.internal("fonts/RhumBanane.ttf");
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
		FreeTypeFontParameter parameters = new FreeTypeFontParameter();
		parameters.size = 70;

		mBitmapFont = generator.generateFont(parameters);
		mBitmapFont.setColor(1f, 1f, 1f, 1);
		generator.dispose();
		
		water.load(Gdx.files.internal("effects/water.p"), Gdx.files.internal("img"));
		fire.load(Gdx.files.internal("effects/fire.p"), Gdx.files.internal("img"));
		ice.load(Gdx.files.internal("effects/ice.p"), Gdx.files.internal("img"));
	}

	@Override
	public void render(float delta) {
		// Clear screen and set color to blue
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.input.setInputProcessor(stage);
		
		draw(delta);
		
		stage.act();
		stage.draw();
		
		// If screen is touched
		if (Gdx.input.isTouched() && !gameOver) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - bucket.width / 2;
		}

		// If need new raindrop
		if (TimeUtils.nanoTime() - lastDropTime > dropTimeGap) {
			spawnFallingOjbect();
		}

		dropSpeed += 0.00015f;
		dropTimeGap -= 30000;
	}
	
	/**
	 * Create a new raindrop and record the time it was spawned.
	 */
	private void spawnFallingOjbect() {
		FallingObject object;
		
		Random rand = new Random();
		if (rand.nextInt(15) == 0) object = new FallingObject("droplet_red.png", Type.BOMB);
		else if (rand.nextInt(15) == 0) object = new FallingObject("droplet_white.png", Type.HAIL);
		else if (rand.nextInt(10) == 0) object = new GoldDrop("droplet_gold.png", Type.GOLD);
		else object = new FallingObject("droplet_blue.png", Type.RAINDROP);

		object.x = MathUtils.random(0, DropGame.WIDTH - object.getImage().getWidth());
		object.y = DropGame.HEIGHT - titleImage.getHeight();
		object.width = object.getImage().getWidth();
		object.height = object.getImage().getHeight();
		
		fallingObjects.add(object);
		lastDropTime = TimeUtils.nanoTime();
	}
	
	/**
	 * 
	 */
	private void draw(float delta) {
		camera.update();
		
		// Draw bucket and raindrops
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(titleImage, 0, DropGame.HEIGHT - titleImage.getHeight());

		for (int i = 0; i < fallingObjects.size; i++) {
			fallingObjects.get(i).draw(batch);
		}
		
		if (goldDrop != null) {
			if (goldDrop.isActive()) {
				goldDrop.update(delta);
				bucket.width = bigBucket.getWidth();
				bucket.height = bigBucket.getHeight();
				batch.draw(bigBucket, bucket.x, bucket.y);
				mBitmapFont.draw(batch, String.valueOf((int)goldDrop.getTimeRemaining()), DropGame.WIDTH - 95, DropGame.HEIGHT - 20);
			} else {
				goldDrop = null;
				bucket.width = bucketImage.getWidth();
				bucket.height = bucketImage.getHeight();
			}
		} else {
			batch.draw(bucketImage, bucket.x, bucket.y);
		}
		
		batch.draw(grassImage, 0, 0);
		
		water.draw(batch);
		fire.draw(batch);
		ice.draw(batch);
		//gold.draw(batch);
		water.update(delta);
		fire.update(delta);
		ice.update(delta);
		//gold.update(delta);
		
		// Draw players current score
		mBitmapFont.draw(batch, score, 40, DropGame.HEIGHT - 20);
		batch.end();
		
		// Draw falling objects moving moving
		Iterator<FallingObject> iter = fallingObjects.iterator();
		while (iter.hasNext()) {
			FallingObject object = iter.next();
			object.y -= (250 * dropSpeed) * Gdx.graphics.getDeltaTime();
			// Raindrop is off screen
			if (object.y + object.getImage().getHeight() < 0) {
				iter.remove();
				if (object.getType() == Type.RAINDROP && !gameOver) {
					gameOver();
				}
			}
			// Raindrop is caught
			if (object.overlaps(bucket) && !gameOver) {
				catchObject(object);
				iter.remove();
			}
		}
	}
	
	/**
	 * 
	 * @param object
	 */
	private void catchObject(FallingObject object) {
		if (object.getType() == Type.RAINDROP) {
			if (DropGame.SOUND_ON) {
				dropSound.play();
			}
			int newScore = Integer.valueOf(score) + 1;
			score = "" + newScore;
		
			water.setPosition(bucket.x + bucket.width/2, bucket.y + bucket.height + 10);
			water.start();
		} 	
		else if (object.getType() == Type.BOMB) {
			if (DropGame.SOUND_ON) {
				bombSound.play();
			}
			fire.setPosition(bucket.x + bucket.width/2, bucket.y + bucket.height- 5);
			fire.start();
			gameOver();
		}
		else if (object.getType() == Type.HAIL) {
			if (DropGame.SOUND_ON) {
				freezeSound.play();
			}
			ice.setPosition(bucket.x + bucket.width/2, bucket.y + bucket.height/2);
			ice.start();
			gameOver();
		}
		else if (object.getType() == Type.GOLD) {
			goldDrop = (GoldDrop) object;
			if (DropGame.SOUND_ON) {
				// sound.play();
			}
			//gold.setPosition(bucket.x + bucket.width/2, bucket.y + bucket.height/2);
			//gold.start();
			
			((GoldDrop) object).setActive(true);
			
			
		}
	}

	/**
	 * 
	 */
	private void gameOver() {
		GameOverDialog d = new GameOverDialog("Game Over", new Skin(						
				Gdx.files.internal("skins/menuSkin.json"),
				new TextureAtlas(Gdx.files.internal("skins/menuSkin.pack"))));
		d.pack();
		d.setPosition(DropGame.WIDTH/2 - d.getWidth()/2, DropGame.HEIGHT/2 - d.getHeight()/2);
		stage.addActor(d);

		gameOver = true;
		
		if (actionResolver.getSignedInGPGS()) {
			actionResolver.submitScoreGPGS(Integer.valueOf(score));
			if (Integer.valueOf(score) > 100) actionResolver.unlockAchievementGPGS("CgkIi_fxh5MEEAIQAQ");
			if (Integer.valueOf(score) > 200) actionResolver.unlockAchievementGPGS("CgkIi_fxh5MEEAIQAg");
			if (Integer.valueOf(score) > 300) actionResolver.unlockAchievementGPGS("CgkIi_fxh5MEEAIQAw");
			if (Integer.valueOf(score) > 400) actionResolver.unlockAchievementGPGS("CgkIi_fxh5MEEAIQBA");
			if (Integer.valueOf(score) > 500) actionResolver.unlockAchievementGPGS("CgkIi_fxh5MEEAIQBQ");
			if (Integer.valueOf(score) > 750) actionResolver.unlockAchievementGPGS("CgkIi_fxh5MEEAIQBg");
			if (Integer.valueOf(score) > 1000) actionResolver.unlockAchievementGPGS("CgkIi_fxh5MEEAIQCA");
		}
	}


	@Override
	public void dispose() {
		if (DropGame.SOUND_ON) {
			dropSound.dispose();
			bombSound.dispose();
			freezeSound.dispose();
			rainMusic.dispose();
		}
		bucketImage.dispose();
		grassImage.dispose();
		water.dispose();
		fire.dispose();
		ice.dispose();
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}
	
	/**
	 * Game Over dialog for Drop game
	 * @author Jake Barnby
	 *
	 */
	public class GameOverDialog extends Dialog {

		/**
		 * Create's a new game over dialog
		 * @param title The title for this dialog
		 * @param skin The skin for this dialog
		 */
		public GameOverDialog(String title, Skin skin) {
			super(title, skin);
			
			button("Back", "Back");
			button("Try again", "Try again");
			text("\n      You lose!\nYour score: " + Integer.valueOf(score) + "\n");
		}
		
		@Override 
		protected void result(Object object) {
			if (((String)object).equals("Back")) {
				dispose();
				((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(actionResolver));
			}
			else if (((String)object).equals("Try again")) {
				dispose();
				((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(actionResolver));
			}
		}

	}
}