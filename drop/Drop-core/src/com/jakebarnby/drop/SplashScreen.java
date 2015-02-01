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

public class SplashScreen implements Screen {
    private Texture texture = new Texture(Gdx.files.internal("img/splashlogo.png"));
    private Image splashImage = new Image(texture);
    private Stage stage = new Stage(new FitViewport(DropGame.WIDTH, DropGame.HEIGHT));

	@Override
	public void show() {
		stage.addActor(splashImage);
		
		splashImage.setColor(1, 1, 1, 0);
		splashImage.setPosition(DropGame.WIDTH/2 - splashImage.getWidth()/2, DropGame.HEIGHT/2 - splashImage.getHeight()/2);
		splashImage.addAction(Actions.sequence(delay(1.0f), fadeIn(2.0f), delay(3.0f), fadeOut(2.0f), run(new Runnable() {
            @Override
            public void run() {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
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
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
}
