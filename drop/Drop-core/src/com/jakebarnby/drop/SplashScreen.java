package com.jakebarnby.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Splash screen for drop game, displays a fading logo then redirects suer to main menu screen
 * @author Jake Barnby
 *
 */
public class SplashScreen implements Screen {
	private ActionResolver ar;
    private Texture texture = new Texture(Gdx.files.internal("img/splashlogo.png"));    //Load splash logo image
    private Image splashImage = new Image(texture);										//Create image from texture of splash logo
    private Stage stage = new Stage(new FitViewport(DropGame.WIDTH, DropGame.HEIGHT));	//Create stage to display the logo

    
    /**
     * Creates a new splash screen
	 * @param actionResolver The ActionResolver used to resolve google play game service events
	 */
	public SplashScreen(ActionResolver actionResolver) {
		ar = actionResolver;
	}

	@Override
	public void show() {
		stage.addActor(splashImage);
		
		/*Set the alpha of the image to 0 for completely transparent so it can be faded in and out,
		  set position of image directly in the center of the screen then change to main menu.      */
		
		splashImage.setColor(1, 1, 1, 0);
		splashImage.setPosition(DropGame.WIDTH/2 - splashImage.getWidth()/2, DropGame.HEIGHT/2 - splashImage.getHeight()/2);
		splashImage.addAction(Actions.sequence(delay(0.5f), fadeIn(1.5f), delay(2.0f), fadeOut(1.5f), run(new Runnable() {
            @Override
            public void run() {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(ar));
            }
        })));
	}

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1); //sets clear color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //clear the batch
        
        stage.act(delta);
        stage.draw();
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		texture.dispose();
		stage.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
