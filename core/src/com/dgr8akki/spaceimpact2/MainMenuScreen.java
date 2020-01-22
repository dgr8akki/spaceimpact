package com.dgr8akki.spaceimpact2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import static com.dgr8akki.spaceimpact2.GameScreenAI.windowsHeight;
import static com.dgr8akki.spaceimpact2.GameScreenAI.windowsWidth;
public class MainMenuScreen implements Screen {

    final SpaceImpact2 game;
    public static Sprite welcomeBackgroundSprite;
    public static Texture welcomeBackgroundTexture;
    private float elapsed = 0;
    OrthographicCamera camera;

    public MainMenuScreen(final SpaceImpact2 game) {
        this.game = game;
        welcomeBackgroundTexture = new Texture(Gdx.files.internal("startBackground2.png"));
          welcomeBackgroundSprite =new Sprite(welcomeBackgroundTexture);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, windowsWidth, windowsHeight);

    }

    @Override
    public void show() {
       
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        welcomeBackgroundSprite.draw(game.batch);
        game.batch.end();

        if (Gdx.input.isTouched() || elapsed > 2.0) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}