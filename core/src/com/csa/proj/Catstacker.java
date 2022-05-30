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
	MainMenuScreen mms;
	CreditsScreen cs;
	static boolean COLLAPSE;

	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("cfont.fnt"));
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		font.getData().setScale(1);
		COLLAPSE = false;
		gs = new GameScreen(this);
		cs = new CreditsScreen(this);
		mms = new MainMenuScreen(this);
		this.setScreen(mms);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		gs.dispose();
		mms.dispose();
		cs.dispose();
	}

	public void reset() {
		gs.dispose();
		mms.dispose();
		cs.dispose();
		COLLAPSE = false;
		batch.dispose();
		font.dispose();
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("cfont.fnt"));
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		font.getData().setScale(1);
		cs = new CreditsScreen(this);
		gs = new GameScreen(this);
		mms = new MainMenuScreen(this);
		this.setScreen(mms);
	}
	public void collapse() {
		System.out.println("Stack Collapsed");
		COLLAPSE = true;
	}
}
