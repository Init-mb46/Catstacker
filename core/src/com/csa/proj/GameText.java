package com.csa.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class GameText {
    static int SCREEN_WIDTH = GameScreen.SCREEN_WIDTH;
    static int SCREEN_HEIGHT = GameScreen.SCREEN_HEIGHT;
    static BitmapFont font1 = new BitmapFont(Gdx.files.internal("cfont.fnt"));

    static Textbox[] MAINMENUTEXT = new Textbox[]{
            new Textbox(
                    new String[]{
                            "CATSTACKER!"
                    },
                    font1,
                    SCREEN_WIDTH / 2 - 100,
                    SCREEN_HEIGHT / 5 * 4,
                    200,
                    50,
                    true
            ),
            new Textbox(
                    new String[]{
                            "Just stack the cats!",
                            "Its that easy",
                            "Click on the cat icon for credits"
                    },
                    font1,
                    SCREEN_WIDTH / 2 - 100,
                    SCREEN_HEIGHT / 2 + 100,
                    200,
                    100,
                    true
            ),
            new Textbox(
                    new String[] {
                            "PRESS SPACE TO START"
                    },
                    font1,
                    SCREEN_WIDTH / 2 - 100,
                    SCREEN_HEIGHT / 3 - 50,
                    200,
                    30,
                    true
            )
    };

    static Textbox[] CREDITSTEXT = new Textbox[]{

    };

    static Textbox[] GAMEOVERTEXT = new Textbox[]{

    };
}
