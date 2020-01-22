/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dgr8akki.spaceimpact2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import static com.dgr8akki.spaceimpact2.GameScreenAI.windowsHeight;
import static com.dgr8akki.spaceimpact2.GameScreenAI.windowsWidth;


/**
 *
 * @author KH9267
 */
public class GameOverScreen implements Screen{
    
    final SpaceImpact2 game;
    private float elapsed = 0;
    OrthographicCamera camera;

    public GameOverScreen(final SpaceImpact2 game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, windowsWidth, windowsHeight);
        GlobalVariables.backgroundTexture = new Texture(Gdx.files.internal("GameOverBackground.png"));
        GlobalVariables.backgroundSprite =new Sprite(GlobalVariables.backgroundTexture);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
            Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        GlobalVariables.backgroundSprite.draw(game.batch);
        game.font.draw(game.batch, "Your Final Score :" + GlobalVariables.score, GlobalVariables.windowsWidth - 250, GlobalVariables.windowsHeight - 100);
        game.batch.end();

        if (Gdx.input.isTouched() || elapsed > 2.0) {
            Gdx.app.exit();
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
