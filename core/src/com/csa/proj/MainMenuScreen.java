package com.csa.proj;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen implements Screen {
    static final int SCREEN_WIDTH = 480;
    static final int SCREEN_HEIGHT = 720;
    final Catstacker game;
    OrthographicCamera camera;
    Texture cloudsBg;
    Texture catIconImage;
    GlyphLayout gl;
    Rectangle catIcon;


    public MainMenuScreen(final Catstacker game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480,720);

        gl = new GlyphLayout();
        cloudsBg = new Texture(Gdx.files.internal("clouds.png"));
        catIconImage = new Texture(Gdx.files.internal("cats/cat1.png"));

        catIcon = new Rectangle();
        catIcon.width = catIconImage.getWidth();
        catIcon.height = catIconImage.getHeight();
    }
    @Override
    public void show() {
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
        game.font.getData().setScale(2);
        gl.setText(game.font, "Cat Stacker!");
        game.font.draw(game.batch, "Cat Stacker!", SCREEN_WIDTH / 2 - gl.width / 2, (SCREEN_HEIGHT / 3) * 2);
        game.font.getData().setScale(1);
        gl.setText(game.font, "Click on the cat for credits");
        game.font.draw(game.batch, "Click on the cat for credits", SCREEN_WIDTH / 2 - gl.width / 2, SCREEN_HEIGHT / 2);
        game.font.getData().setScale(1);
        gl.setText(game.font, "PRESS SPACE TO START");
        game.font.draw(game.batch, "PRESS SPACE TO START", SCREEN_WIDTH / 2 - gl.width / 2, SCREEN_HEIGHT / 3 + gl.height);
        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            //game starts
            System.out.println("game starting");
            game.setScreen(game.gs);
            dispose();
        }
        if (Gdx.input.isTouched()) {
            Vector2 pos = new Vector2();
            pos.set(Gdx.input.getX(), Gdx.input.getY());
            if (pos.x > catIcon.x && pos.x < catIcon.x + catIcon.getWidth() && pos.y > catIcon.y && pos.y < catIcon.y + catIcon.height) {
                //credits and info screen
                System.out.println("Credits rolling");
                game.setScreen(new CreditsScreen(game));
                dispose();
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
    }
}
