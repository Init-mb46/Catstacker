package com.csa.proj;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Basket extends Rectangle {
    Catstacker game;
    float xvelocity = 0f;
    float yvelocity = 0f;

    final int set_y;

    Array<Cat> catstack;
    int camcofOffset = 0;

    public Basket(Catstacker game, Array<Cat> catstack, int x, int y) {
        this.game = game;
        this.catstack = catstack;
        this.x = x;
        this.y = y;
        width = 140;
        height = 60;
        set_y = y;
    }

    public void move(float delta) {
        x += xvelocity * delta;
        y += yvelocity * delta;
    }

    /**
     * must be called between batch begin and batch end
     * @param delta
     * @param t
     */
    public void render(float delta, Texture t, int offset) {
        y = set_y + offset;
        move(delta);
        if (y >= GameScreen.RENDERBOUNDS) {
            game.batch.draw(t, x, y, width, height);
            GameScreen.RENDERCOUNT++;
        }
    }
}
