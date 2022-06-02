package com.csa.proj;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

public class Garbage extends Rectangle implements FallingObject{
    Catstacker game;
    String texture;
    Rectangle target;

    static final int GARBAGESIZE = 100;

    float xvelocity = 0f;
    float yvelocity = 0f;
    static final int DEFWIDTH = GARBAGESIZE / 3 * 2;
    static final int DEFHEIGHT = GARBAGESIZE / 3 * 2;
    final int overlap = GameScreen.STACKOVERLAP;

    boolean collapsed = false;

    public Garbage(Catstacker game, String texture, int x, int y) {
        this.game = game;
        this.texture = texture;
        this.x = x;
        this.y = y;
        width = DEFWIDTH;
        height = DEFHEIGHT;

        target = game.gs.previousCat;

    }

    public void collapse(float COLLAPSE_POWER_MULTIPLIER) {
        if (collapsed) return;
        if (!collapsed) collapsed = true;
        xvelocity = (MathUtils.random() - 0.5f) * 200 * COLLAPSE_POWER_MULTIPLIER;
        yvelocity = MathUtils.random() * 50 * COLLAPSE_POWER_MULTIPLIER;
    }

    @Override
    public void render(float delta) {
        target = game.gs.previousCat;
        move(delta);
        if (y > GameScreen.RENDERBOUNDS) {
            GameScreen.RENDERCOUNT++;
            game.batch.draw(GameScreen.GARBAGEITEMS.get(texture), x + width / 2 - GARBAGESIZE / 2 , y + height / 2 - GARBAGESIZE / 2 , GARBAGESIZE,GARBAGESIZE);
        }
    }

    @Override
    public void move(float delta) {
        yvelocity += ACCELERATION * delta;
        if (!collapsed && target != null) {
            if (y < target.y + target.height && y > target.y + target.height - overlap) {
                if (x + width / 3  < target.x + target.width && x + width / 3 * 2 > target.x){
                    //hit the target
                    xvelocity = 0;
                    yvelocity = 0;

                    game.gs.collapseXcats(MathUtils.random(1, (int) MathUtils.clamp(1 + Math.sqrt((TimeUtils.millis() - game.gs.gameStartTime) / 1000) * 0.2, 1, 6)));
                    collapse(GameScreen.COLLAPSE_POWER_MULTIPLIER);
                }
            }
        }

        x += xvelocity * delta;
        y += yvelocity * delta;
    }
}
