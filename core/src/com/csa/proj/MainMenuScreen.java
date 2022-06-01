package com.csa.proj;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

import java.sql.Time;

public class MainMenuScreen implements Screen {
    static final int SCREEN_WIDTH = 480;
    static final int SCREEN_HEIGHT = 720;
    final Catstacker game;
    OrthographicCamera camera;
    Texture cloudsBg;
    Texture catIconImage;
    GlyphLayout gl;
    Rectangle catIcon;
    Music bgMusic; // BACKGROUND MUSIC TRACK BY VABsounds //https://freesound.org/people/VABsounds/sounds/505391/

    boolean ready = false;
    boolean startGame = false;
    long st;

    public MainMenuScreen(final Catstacker game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480,720);

        gl = new GlyphLayout();
        cloudsBg = new Texture(Gdx.files.internal("clouds.png"));
        catIconImage = new Texture(Gdx.files.internal("cats/cat1.png"));
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("bgMusic.mp3"));
        bgMusic.setVolume(0f);
        bgMusic.setLooping(true);

        catIcon = new Rectangle();
        catIcon.width = catIconImage.getWidth();
        catIcon.height = catIconImage.getHeight();
    }
    @Override
    public void show() {
        st = TimeUtils.millis();
        bgMusic.play();
        ready = false;
    }
    @Override
    public void render(float delta) {
        camera.update();

        catIcon.x = 10;
        catIcon.y = SCREEN_HEIGHT - catIconImage.getHeight() - 10;

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(cloudsBg,0,0);
        game.batch.draw(catIconImage, catIcon.x, catIcon.y, catIcon.getWidth(), catIcon.getHeight());
        for (Textbox t : GameText.MAINMENUTEXT) {
            t.draw(game.batch);
        }
        game.batch.end();

        if (bgMusic.getVolume() < 0.05f){
            bgMusic.setVolume(MathUtils.clamp(bgMusic.getVolume() + 0.01f * delta, 0, 0.05f));
        }

        if (!ready && TimeUtils.millis() - st > 1000) ready = true;

        if (!ready) {
            return;
        }
        if (startGame) {
            if (bgMusic.getVolume() > 0.001) {
                bgMusic.setVolume(MathUtils.clamp(bgMusic.getVolume() - 0.05f * delta, 0, 0.05f));
                return;
            }
            bgMusic.stop();
            System.out.println("game starting");
            game.setScreen(game.gs);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !startGame) {
            //game starts
            startGame = true;
        }
        if (Gdx.input.isTouched() && !startGame) {
            Vector3 pos = new Vector3();
            pos.set(Gdx.input.getX(), Gdx.input.getY(),0);
            camera.unproject(pos);
            if (pos.x > catIcon.x && pos.x < catIcon.x + catIcon.getWidth() && pos.y > catIcon.y && pos.y < catIcon.y + catIcon.height) {
                //credits and info screen
                System.out.println("Credits rolling");
                game.setScreen(game.cs);
            }
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
        cloudsBg.dispose();
        catIconImage.dispose();
        bgMusic.dispose();
    }
}
