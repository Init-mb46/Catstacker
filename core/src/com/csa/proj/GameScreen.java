package com.csa.proj;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
    static final int SCREEN_WIDTH = 480;
    static final int SCREEN_HEIGHT = 720;
    final Catstacker game;

    Texture basketImage;
    Texture groundImage;
    Texture cloudsBG1;
    Texture cloudsBG2;

    OrthographicCamera camera;
    Rectangle basket;
    Rectangle ground;
    GlyphLayout gl;

    Array<Texture> catImages;
    Array<Cat> catstack;
    int gameOverHeight;
    int targetYOffset = 0;
    int currentYOffset = 0;
    int bg1start;
    int bg2start;
    final float bgscrollspeed = 60f;
    final float foregroundspeed = 200f;
    long laststacktime;
    int stackSize;
    long gameStartTime;

    public GameScreen(final Catstacker game) {
        this.game = game;
        cloudsBG1 = new Texture(Gdx.files.internal("clouds.png"));
        cloudsBG2 = new Texture(Gdx.files.internal("clouds.png"));
        basketImage = new Texture(Gdx.files.internal("basket.png"));
        groundImage = new Texture(Gdx.files.internal("ground.png"));

        gl = new GlyphLayout();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 720);

        catstack = new Array<>(); //need to populate with all the cat images
        catImages = new Array<>();

        gameOverHeight = 150; //should be around the top of the boat
        bg1start = 0;
        bg2start = bg1start + SCREEN_HEIGHT;

        ground = new Rectangle();
        ground.x = 0;
        ground.y = 0;
        ground.height = groundImage.getHeight();
        ground.width = groundImage.getWidth();
    }

    @Override
    public void show() {
        gameStartTime = TimeUtils.millis();
    }

    @Override
    public void render(float delta) {
        camera.update();

        game.batch.begin();
        renderBG(game.batch, delta);
        game.batch.draw(groundImage, ground.x, ground.y - currentYOffset);
        game.batch.end();

        incrementOffset(delta);
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

    /**
     * call between batch begin and batch end
     * @param batch
     */
    private void renderBG(SpriteBatch batch, float delta) {
        batch.draw(cloudsBG1, 0, bg1start);
        batch.draw(cloudsBG2, 0, bg2start);

        bg1start -= bgscrollspeed * delta;
        if (bg1start < -720) bg1start = 0;
        bg2start = bg1start + SCREEN_HEIGHT;
    }

    private void incrementOffset(float delta) {
        if (currentYOffset < targetYOffset) {
            currentYOffset += foregroundspeed * delta;
            if (currentYOffset > targetYOffset) currentYOffset = targetYOffset;
        }
    }
}
