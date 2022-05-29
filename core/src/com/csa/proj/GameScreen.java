package com.csa.proj;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    static final int SCREEN_WIDTH = 480;
    static final int SCREEN_HEIGHT = 720;

    final Catstacker game;
    Texture boatImage;
    Texture waterImage;
    Texture cloudsBG1;
    Texture cloudsBG2;

    OrthographicCamera camera;
    Rectangle boat;
    GlyphLayout gl;


    Array<Texture> catImages;
    Array<Cat> catstack;
    int gameOverHeight;
    int bg1start;
    int bg2start;
    final float bgscrollspeed = 10f;

    public GameScreen(final Catstacker game) {
        this.game = game;
        cloudsBG1 = new Texture(Gdx.files.internal("clouds.png"));
        cloudsBG2 = new Texture(Gdx.files.internal("clouds.png"));

        gl = new GlyphLayout();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 720);

        catstack = new Array<>();
        catImages = new Array<>();

        gameOverHeight = 150; //should be around the top of the boat
        bg1start = 0;
        bg2start = bg1start + SCREEN_HEIGHT;

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,0);
        camera.update();
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
        bg2start = bg1start + 720;
    }
}