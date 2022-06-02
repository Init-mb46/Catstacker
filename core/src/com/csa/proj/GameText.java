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
                    SCREEN_HEIGHT / 5 * 4,
                    50,
                    true,
                    true
            ),
            new Textbox(
                    new String[]{
                            "Just stack the cats!",
                            "Its that easy",
                            "Click on the cat icon for credits"
                    },
                    font1,
                    SCREEN_HEIGHT / 2 + 100,
                    100,
                    true,
                    true
            ),
            new Textbox(
                    new String[] {
                            "PRESS SPACE TO START"
                    },
                    font1,
                    SCREEN_HEIGHT / 3 - 50,
                    30,
                    true,
                    true
            )
    };

    static Textbox[] CREDITSTEXT = new Textbox[]{
            new Textbox(
                    new String[] {
                            "CREDITS"
                    },
                    font1,
                    SCREEN_HEIGHT / 6 * 5 + 50,
                    40,
                    true,
                    true
            ),
            new Textbox(
                    new String[] {
                            "Cat Sprites by:         Victoria Yim",
                            "Meowing Sounds by:   Calvin Wu",
                    },
                    font1,
                    SCREEN_HEIGHT / 4 * 3,
                    50,
                    true,
                    true
            ),
            new Textbox(
                    new String[] {
                            "Menu Music by:       VABsounds",
                            "Game Music by:    FoolBoyMedia"
                    },
                    font1,
                    SCREEN_HEIGHT / 4 * 3 - 80,
                    50,
                    true,
                    true
            ),
            new Textbox(
                    new String[] {
                            "All other sprite assets",
                            "are copyright-free"
                    },
                    font1,
                    SCREEN_HEIGHT / 2,
                    60,
                    true,
                    true
            )
    };

    static Textbox[] GAMEOVERTEXT = new Textbox[]{
            new Textbox(
                    new String[]{
                            "GAME OVER :(",
                    },
                    font1,
                    SCREEN_HEIGHT / 4 * 3 + 50,
                    50,
                    true,
                    true
            ),
            new Textbox(
                    new String[]{
                            "Better luck next time"
                    },
                    font1,
                    SCREEN_HEIGHT / 3 + 40,
                    20,
                    true,
                    true
            ),
            new Textbox(
                    new String[]{
                            "Press space to go back to the menu"
                    },
                    font1,
                    SCREEN_HEIGHT/ 3,
                    20,
                    true,
                    true
            )
    };

    static Textbox NEWHIGHSCORE = new Textbox(
            new String[]{
                    "NEW HIGHSCORE!",
            },
            font1,
            SCREEN_HEIGHT / 3 * 2,
            30,
            true,
            true
    );
    static Textbox scoreText(int score, int x, int y, int width, int height) {
        return new Textbox(
                new String[]{
                        "Cats Stacked: " + score
                },
                font1,
                x,
                y,
                width,
                height,
                true
        );
    }

    /**
     * @param time (in seconds)
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    static Textbox timerText(int time, int x, int y, int width, int height) {
        return new Textbox(
                new String[]{
                        String.format("%02d:%02d:%02d", time / 3600, time % 3600 / 60, time % 60)
                },
                font1,
                x,
                y,
                width,
                height,
                true
        );
    }
}
