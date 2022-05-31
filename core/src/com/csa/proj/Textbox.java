package com.csa.proj;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Textbox extends Rectangle {
    BitmapFont font;
    float fontSize; //px
    String[] text;
    GlyphLayout glyphLayout;
    float fontScale = 1;
    float padding = 10;

    public Textbox(String[] lines, BitmapFont font, int x, int y, int width, int height, boolean fill) {
        super(x,y,width,height);
        this.glyphLayout = new GlyphLayout();
        this.font = font;
        this.text = lines;
        glyphLayout.setText(font, "s");
        fontSize = (int) glyphLayout.height;
        fontScale = fill ? (height - (text.length - 1) * padding) / text.length / fontSize : 1;
    }

    public Textbox(String[] lines, BitmapFont font, int y, int height, boolean fill, boolean centered) {
        this(lines, font, GameScreen.SCREEN_WIDTH / 2, y, 0, height, fill);
    }

    /**
     * must be called between batch begin and batch end
     * @param batch
     */
    public void draw(SpriteBatch batch) {
        font.getData().setScale(fontScale);
        for (int i = 0; i < text.length; i++) {
            String s = text[i];
            glyphLayout.setText(font, s);
            font.draw(batch, s, x + width / 2 - glyphLayout.width / 2, y - i * glyphLayout.height - i * padding);
        }
        font.getData().setScale(1);
    }
}
