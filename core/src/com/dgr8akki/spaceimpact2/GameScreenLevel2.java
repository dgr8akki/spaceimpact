package com.dgr8akki.spaceimpact2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;

public class GameScreenLevel2 implements Screen {
        final SpaceImpact2 game;
        Texture selfTankImage;
        public static Texture backgroundTexture;
        Texture enemyTankImage;
        Texture bulletImage;
        Sound destroySound;
        int perLevelScore = 0;
        Music spaceMusic;
        SpriteBatch batch;
        OrthographicCamera camera;
        Rectangle selfTank,bullet;
        Array<Rectangle> enemyTanks;
        private Array<Rectangle> bullets;
        private long lastEnemyTankTime;
        private long lastBulletTime;
        public static Sprite backgroundSprite;
        int enemiesLeft = GlobalVariables.maxLevelScore;
        

	public GameScreenLevel2(final SpaceImpact2 game) {
            this.game = game;
            // load the images for the selfTank and the enemy tanks, 64x64 pixels each
          enemyTankImage = new Texture(Gdx.files.internal("enemyTank2.png"));
          bulletImage = new Texture(Gdx.files.internal("selfTank.png"));
          selfTankImage = new Texture(Gdx.files.internal("selfTank.png"));
          backgroundTexture = new Texture(Gdx.files.internal("background2.jpg"));
          backgroundSprite =new Sprite(backgroundTexture);

          // load the drop sound effect and the rain background "music"
          destroySound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
          spaceMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

          // start the playback of the background music immediately
          spaceMusic.setLooping(true);
          spaceMusic.play();

          // create the camera and the SpriteBatch
          camera = new OrthographicCamera();
          camera.setToOrtho(false, GlobalVariables.windowsWidth, GlobalVariables.windowsHeight);
          batch = new SpriteBatch();

          // create a Rectangle to logically represent the bucket
          selfTank = new Rectangle();
          selfTank.x = GlobalVariables.windowsWidth / 2 - 64 / 2; // center the bucket horizontally
          selfTank.y = 0; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
          selfTank.width = 64;
          selfTank.height = 64;

          // create the raindrops array and spawn the first raindrop
          enemyTanks = new Array<Rectangle>();
          //bullets  = new Array<Rectangle>();
          spawnEnemyTanks();
          spawnBullet();

	}

        private void spawnEnemyTanks() {
            Rectangle enemyTank = new Rectangle();
            enemyTank.x = MathUtils.random(0, GlobalVariables.windowsWidth-64);
            enemyTank.y = GlobalVariables.windowsHeight;
            enemyTank.width = 64;
            enemyTank.height = 64;
            enemyTanks.add(enemyTank);
            lastEnemyTankTime = TimeUtils.nanoTime();
        }
        
        @Override
	public void render (float delta) {  
           //Gdx.gl.glClearColor(1f, 1f, 1f, 0);
           //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
           

           // tell the camera to update its matrices.
           camera.update();

           // tell the SpriteBatch to render in the
           // coordinate system specified by the camera.
           batch.setProjectionMatrix(camera.combined);

           // begin a new batch and draw the bucket and
           // all drops
           batch.begin();
           backgroundSprite.draw(batch);
           batch.draw(selfTankImage, selfTank.x, selfTank.y);
           game.font.draw(batch, "Enemies Left: " + enemiesLeft, 0, GlobalVariables.windowsHeight);
           game.font.draw(batch, "Scores: " + GlobalVariables.score, GlobalVariables.windowsWidth - 200, GlobalVariables.windowsHeight);
           batch.draw(bulletImage, bullet.x, bullet.y);
           for(Rectangle enemyTank: enemyTanks) {
              batch.draw(enemyTankImage, enemyTank.x, enemyTank.y);
           }
           batch.end();

           // process user input
           if(Gdx.input.isTouched()) {
              Vector3 touchPos = new Vector3();
              touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
              camera.unproject(touchPos);
              selfTank.x = touchPos.x - 64 / 2;
              selfTank.y = touchPos.y - 64 / 2;
           }
           if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) selfTank.x -= 300 * Gdx.graphics.getDeltaTime();
           if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) selfTank.x += 300 * Gdx.graphics.getDeltaTime();
           if(Gdx.input.isKeyPressed(Input.Keys.UP)) selfTank.y += 300 * Gdx.graphics.getDeltaTime();
           if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) selfTank.y -= 300 * Gdx.graphics.getDeltaTime();
           if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) spawnBullet();

           // make sure the bucket stays within the screen bounds
           if(selfTank.x < 0) selfTank.x = 0;
           if(selfTank.x > GlobalVariables.windowsWidth - 64) selfTank.x = GlobalVariables.windowsWidth - 64;
           if(selfTank.y < 0) selfTank.y = 0;
           if(selfTank.y > GlobalVariables.windowsHeight - 64) selfTank.y = GlobalVariables.windowsHeight - 64;

           // check if we need to create a new raindrop
           if(TimeUtils.nanoTime() - lastEnemyTankTime > 1000000000) 
           {
               spawnEnemyTanks();
           }
           
           // move the raindrops, remove any that are beneath the bottom edge of
           // the screen or that hit the bucket. In the later case we play back
           // a sound effect as well.
           Iterator<Rectangle> iter = enemyTanks.iterator();
           while(iter.hasNext() ) {
              Rectangle enemyTank = iter.next();         
              enemyTank.y -= 400 * Gdx.graphics.getDeltaTime();

              if(enemyTank.y + 64 < 0) iter.remove();
              if(enemyTank.overlaps(selfTank)) {
                 destroySound.play();
                 game.setScreen(new GameOverScreen(game));
                 dispose();
              }
               bullet.y += 200 * Gdx.graphics.getDeltaTime(); 

              if (bullet.overlaps(enemyTank))
              {
                 iter.remove();
                 bullet.y = -200;
                 GlobalVariables.score++;
                 perLevelScore++;
                 enemiesLeft--;
                 if (perLevelScore == GlobalVariables.maxLevelScore) {
                    game.setScreen(new GameOverScreen(game));
                 dispose(); 
                 }
              }
           }
	}
	
	@Override
	public void dispose () {
        // dispose of all the native resources
            enemyTankImage.dispose();
            selfTankImage.dispose();
            destroySound.dispose();
            spaceMusic.dispose();
            batch.dispose();
	}

    @Override
    public void show() {
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

    private void spawnBullet() {
        bullet = new Rectangle();
        bullet.x = selfTank.x;
        bullet.y = selfTank.y;
        bullet.width = 64;
        bullet.height = 64;
    }    
}
