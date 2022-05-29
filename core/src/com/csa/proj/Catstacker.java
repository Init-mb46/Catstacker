package com.csa.proj;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Catstacker extends Game {
	SpriteBatch batch;
	BitmapFont font;
	GameScreen gs;

	@Override
	public void create () {
		batch = new SpriteBatch();
		gs = new GameScreen(this);
		font = new BitmapFont(Gdx.files.internal("cfont.fnt"));
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		font.getData().setScale(1);
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		gs.dispose();
		font.dispose();
	}
}
