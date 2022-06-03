package com.csa.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class CreditsScreen implements Screen {
    static final int SCREEN_WIDTH = 480;
    static final int SCREEN_HEIGHT = 720;
    final Catstacker game;

    OrthographicCamera camera;
    Texture cloudsBg;

    //EXTRAS
    int catSpam = 1000;
    long lastTimeUsed = 0;
    long cooldown = 5000;
    float EXTRAS_COLLAPSE_POWER_MULTIPLIER = 100;
    boolean bonusActive = false;
    Array<Cat> extrasStack;

    public CreditsScreen(final Catstacker game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480,720);

        cloudsBg = new Texture(Gdx.files.internal("clouds.png"));
        extrasStack = new Array<>();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(cloudsBg, 0,0);
        for (Textbox t : GameText.CREDITSTEXT) {
            t.draw(game.batch);
        }
        for (Cat t : extrasStack) {
            t.render(delta);
        }
        game.batch.end();

        if (bonusActive) {
            if (TimeUtils.millis() - lastTimeUsed > cooldown) {
                bonusActive = false;
                extrasStack.clear();
            }
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.C)) {
                bonus();
                lastTimeUsed = TimeUtils.millis();
                bonusActive = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                game.setScreen(game.mms);
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

    }

    private void bonus() {
        for (int i = 0 ; i < catSpam ; i ++ ) {
            int catNum = MathUtils.random(1, GameScreen.CATIMAGES.size);
            String texture = "cat" + catNum;
            Cat c = new Cat(game, texture, null, null, SCREEN_WIDTH / 2 - Cat.DEFWIDTH / 2, 0);
            c.placed = true;
            c.falling = true;
            float sizeScale = MathUtils.random()  + 0.5f;
            c.height *= sizeScale;
            c.width *= sizeScale;
            extrasStack.add(c);
            c.collapse(EXTRAS_COLLAPSE_POWER_MULTIPLIER);
        }
        String track = "meow"+MathUtils.random(1,4);
        long id = GameScreen.MEOWSOUNDS.get(track).play();
        GameScreen.MEOWSOUNDS.get(track).setPitch(id, 1.5f);
        GameScreen.MEOWSOUNDS.get(track).setVolume(id, 0.5f);
    }
}
