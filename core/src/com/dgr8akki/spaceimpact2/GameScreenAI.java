package com.dgr8akki.spaceimpact2;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.exit;

public class GameScreenAI implements Screen {
        final SpaceImpact2 game;
        private Texture selfTankImage;
        public static Texture backgroundTexture;
        private Texture enemyTankImage;
        private Texture bulletImage;
        private Sound destroySound;
        private Music spaceMusic;
        private SpriteBatch batch;
        private OrthographicCamera camera;
        private Rectangle selfTank,bullet;
        private Array<Rectangle> enemyTanks;
        private Array<Rectangle> bullets;
        private long lastEnemyTankTime;
        private long lastBulletTime;
        public final static int windowsWidth = 1366;
        public final static int windowsHeight = 768;
        public static Sprite backgroundSprite;
        
        private Rectangle currentTank;

	public GameScreenAI(final SpaceImpact2 game) {
            this.game = game;
            // load the images for the selfTank and the enemy tanks, 64x64 pixels each
          enemyTankImage = new Texture(Gdx.files.internal("enemyTank.png"));
          bulletImage = new Texture(Gdx.files.internal("selfTank.png"));
          selfTankImage = new Texture(Gdx.files.internal("selfTank.png"));
          backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
          backgroundSprite =new Sprite(backgroundTexture);

          // load the drop sound effect and the rain background "music"
          destroySound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
          spaceMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

          // start the playback of the background music immediately
          spaceMusic.setLooping(true);
          spaceMusic.play();

          // create the camera and the SpriteBatch
          camera = new OrthographicCamera();
          camera.setToOrtho(false, windowsWidth, windowsHeight);
          batch = new SpriteBatch();

          // create a Rectangle to logically represent the bucket
          selfTank = new Rectangle();
          selfTank.x = windowsWidth / 2 - 64 / 2; // center the bucket horizontally
          selfTank.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
          selfTank.width = 64;
          selfTank.height = 64;

          // create the raindrops array and spawn the first raindrop
          enemyTanks = new Array<Rectangle>();
          bullets  = new Array<Rectangle>();
          spawnEnemyTanks();
          spawnBullet();
	}

        private Rectangle spawnEnemyTanks() {
            Rectangle enemyTank = new Rectangle();
            enemyTank.x = MathUtils.random(0, windowsWidth-64);
            enemyTank.y = windowsHeight;
            enemyTank.width = 64;
            enemyTank.height = 64;
            enemyTanks.add(enemyTank);
            lastEnemyTankTime = TimeUtils.nanoTime();
            
            return enemyTank;
        }
        private void spawnBullet() {
            Rectangle bullett = new Rectangle();
            bullett.x = selfTank.x;
            bullett.y = selfTank.y;
            bullett.width = 64;
            bullett.height = 64;
            lastBulletTime = TimeUtils.nanoTime();
            bullets.add(bullett);
    }
    
        
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
//           batch.draw(bulletImage, bullet.x, bullet.y);

           for(Rectangle bullet : bullets) {
                batch.draw(bulletImage, bullet.x, bullet.y);
            }
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
           if(selfTank.x > windowsWidth - 64) selfTank.x = windowsWidth - 64;
           if(selfTank.y < 0) selfTank.y = 0;
           if(selfTank.y > windowsHeight - 64) selfTank.y = windowsHeight - 64;
           

           // check if we need to create a new raindrop
           if(TimeUtils.nanoTime() - lastEnemyTankTime > 1000000000) 
           {
               currentTank = spawnEnemyTanks();
           }
           
           if( currentTank != null) {
               selfTank.x = currentTank.x;
           }
           
           if(TimeUtils.nanoTime() -  lastBulletTime > 1000000000) 
           {
               spawnBullet();
           }
           
          
           
           // move the raindrops, remove any that are beneath the bottom edge of
           // the screen or that hit the bucket. In the later case we play back
           // a sound effect as well.
           Iterator<Rectangle> iter = enemyTanks.iterator();
           Iterator<Rectangle> bulletIter = bullets.iterator();
           while(iter.hasNext() && bulletIter.hasNext()) {
              Rectangle enemyTank = iter.next();
              Rectangle friendlyBullet = bulletIter.next();
              
              friendlyBullet.y += 200 * Gdx.graphics.getDeltaTime(); 
              enemyTank.y -= 200 * Gdx.graphics.getDeltaTime();

              if(enemyTank.y + 64 < 0) iter.remove();
              if(enemyTank.overlaps(selfTank)) {
                 destroySound.play();
                 //game.setScreen(new GameOverScreen(game));
                 //dispose();
              }
              if(friendlyBullet.y + 64 > windowsHeight) bulletIter.remove();
              if (friendlyBullet.overlaps(enemyTank))
              {
                  System.out.println("Colliding");
                 iter.remove();
                 bulletIter.remove();
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

 
}
