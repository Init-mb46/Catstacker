package com.csa.proj;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Cat extends Rectangle implements FallingObject{
    final Catstacker game;

    String texture;
    Rectangle previousTarget;
    Basket base;
    boolean placed = false;
    boolean falling = false;

    //physics
    float yvelocity = 200f;
    float xvelocity = 0f;
    final int set_xpos;
    final static float DRIFTSPEED = 70;

    final static int DEFWIDTH = 120; //px
    final static int DEFHEIGHT = 84; //px

    final static int STACKOVERLAP = GameScreen.STACKOVERLAP;
    final static float ACCELERATION = GameScreen.ACCELERATION;

    int placedXoffset;
    int placedYoffset;
    boolean collapsed = false;

    public Cat(final Catstacker game, String texture, Rectangle previous, Basket base, int x, int y) {
        this.texture = texture;
        this.previousTarget = previous;
        this.game = game;
        this.base = base;
        width = DEFWIDTH;
        height = DEFHEIGHT;
        this.x = x;
        this.y = y;
        set_xpos = x;
        xvelocity = DRIFTSPEED; //slowly drift side to side;
        falling = true;
    }

    public void collapse(float COLLAPSE_POWER_MULTIPLIER) {
        if (collapsed) return;
        collapsed = true;
        placed = true;
        falling = true;
        yvelocity = MathUtils.random() * 25 * COLLAPSE_POWER_MULTIPLIER;
        xvelocity = (MathUtils.random() - 0.5f) * 100 * COLLAPSE_POWER_MULTIPLIER;
    }

    public void move(float delta) {
        if (falling) yvelocity += ACCELERATION * delta;
        if (placed && falling) yvelocity += ACCELERATION * delta;
        //initiate falling into place
        if (falling && !placed && !collapsed) {
            previousTarget = game.gs.previousCat != null ? game.gs.previousCat : game.gs.basket;
            if (previousTarget != null && y < previousTarget.y + previousTarget.height - STACKOVERLAP) {
                if (x + width / 1.5 > previousTarget.x + STACKOVERLAP && x + width / 3 < previousTarget.x + previousTarget.width - STACKOVERLAP) {
                    //cat is in place to be stacked
                    falling = false;
                    placed = true;

                    //lock in place
                    xvelocity = 0;
                    yvelocity = 0;
                    placedXoffset = (int) (x - base.x);
                    y = previousTarget.y + previousTarget.height - STACKOVERLAP;
                    placedYoffset = (int) (y - base.y);
                    game.gs.updateCOF(this, true);
                } else {
                    //cat is not in place. collapse stack
                    game.collapse();
                    placed = true;
                    falling = false;
                }
            } else {
                //still falling into place
                if (x < set_xpos - STACKOVERLAP || x > set_xpos + STACKOVERLAP) xvelocity = -xvelocity;
            }
        }

        if (placed && !falling && !game.COLLAPSE && base != null) {
            x = base.x + placedXoffset;
            y = base.y + placedYoffset;
        } else {
            x += xvelocity * delta;
            y += yvelocity * delta;
        }
    }

    /**
     * must be called between batch begin and batch end
     * @param delta
     */
    public void render(float delta) {
        move(delta);
        if (y >= GameScreen.RENDERBOUNDS) {
            //render
            game.batch.draw(GameScreen.CATIMAGES.get(texture), x, y, width, height);
            GameScreen.RENDERCOUNT++;
        }
    }
}
