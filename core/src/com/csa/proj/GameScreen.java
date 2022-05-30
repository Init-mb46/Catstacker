package com.csa.proj;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
    static final int SCREEN_WIDTH = 480;
    static final int SCREEN_HEIGHT = 720;
    final Catstacker game;

    Texture basketImage;
    Texture groundImage;
    Texture cloudsBG1;
    Texture cloudsBG2;

    OrthographicCamera camera;
    Basket basket;
    Rectangle ground;
    GlyphLayout gl;

    static ObjectMap<String, Texture> CATIMAGES;
    Array<Cat> catstack;

    final static float ACCELERATION = -40f;
    final static int STACKOVERLAP = 20; //px
    final static int RENDERBOUNDS = -250; //do not render anythng under this many pixels
    int bg1start;
    int bg2start;
    final float bgscrollspeed = 60f;
    final float foregroundscrollspeed = 200f;
    final float globalhorizontalspeed = 240f;
    long lastSpawnTime;
    long gameStartTime;
    long gameEndTime;
    boolean stopRenderFunctions = false;

    int targetYOffset = 0;
    int currentYOffset = 0;
    Rectangle camerasCOF;
    boolean shifting = false;
    static int RENDERCOUNT = 0;

    public GameScreen(final Catstacker game) {
        this.game = game;
        cloudsBG1 = new Texture(Gdx.files.internal("clouds.png"));
        cloudsBG2 = new Texture(Gdx.files.internal("clouds.png"));
        basketImage = new Texture(Gdx.files.internal("basket.png"));
        groundImage = new Texture(Gdx.files.internal("ground.png"));

        gl = new GlyphLayout();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 720);

        catstack = new Array<>();
        readyCatTextures();

        bg1start = 0;
        bg2start = bg1start + SCREEN_HEIGHT;

        ground = new Rectangle();
        ground.x = 0;
        ground.y = 0;
        ground.height = groundImage.getHeight();
        ground.width = groundImage.getWidth();

        basket = new Basket(game, catstack, SCREEN_WIDTH / 2 - basketImage.getWidth() / 2, (int) (ground.height - STACKOVERLAP));
        camerasCOF = basket; //everytime a cat is added onto the stack, the center changes
    }

    @Override
    public void show() {
        gameStartTime = TimeUtils.millis();
    }

    @Override
    public void render(float delta) {
        camera.update();
        RENDERCOUNT = 0;

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        renderBG(game.batch, delta);
        game.batch.draw(groundImage, ground.x, ground.y + currentYOffset);
        RENDERCOUNT++;
        basket.render(delta, basketImage, currentYOffset);
        //render all cats
        for (Cat c : catstack) {
            c.render(delta);
        }
        game.batch.end();
        if (stopRenderFunctions) {
            if (TimeUtils.millis() - gameEndTime > 5000) {
                catstack.clear();
                game.reset();
            }
        } else {
            if (game.COLLAPSE) {
                //do collapse work
                stopRenderFunctions = true;
                updateHorizontalSpeeds(0);
                collapseStack();
                return;
            }
            spawn(delta);

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                //move everything to the right
                System.out.println("right");
                updateHorizontalSpeeds(globalhorizontalspeed);
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                System.out.println("left");
                updateHorizontalSpeeds(-globalhorizontalspeed);
            } else {
                updateHorizontalSpeeds(0);
            }

            //for moving sprites down as stack grows
            if (camerasCOF.y > SCREEN_HEIGHT / 3 && !shifting) {
                shifting = true;
                targetYOffset -= camerasCOF.height - STACKOVERLAP;
            }
        }
        incrementOffset(delta);
//        System.out.println(RENDERCOUNT);
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
        cloudsBG2.dispose();
        cloudsBG1.dispose();
        basketImage.dispose();
        groundImage.dispose();
        catstack.clear();
        CATIMAGES.clear();
    }

    private void updateHorizontalSpeeds(float speed) {
        basket.xvelocity = speed;
        for (Cat c : catstack) {
            if (c.placed) c.xvelocity = speed;
        }
        camerasCOF.x = MathUtils.clamp(camerasCOF.x, 0, SCREEN_WIDTH - camerasCOF.width);
        basket.x = basket.camcofOffset + camerasCOF.x;
    }

    private void spawn(float delta) {
        int spawnRate = (int) MathUtils.clamp(5000 / Math.abs(MathUtils.log(MathUtils.E, (TimeUtils.millis() - gameStartTime) / 1000 * 0.2f)), 2000, 5000);
        if (TimeUtils.millis() - lastSpawnTime < spawnRate) return;
        System.out.println("spawning");
        int catNum = MathUtils.random(1, CATIMAGES.size);
        String texture = "cat" + catNum;
        int randXPos = MathUtils.random(STACKOVERLAP, SCREEN_WIDTH - Cat.DEFWIDTH - STACKOVERLAP);
        Cat c = new Cat(game, texture, (catstack.size > 0 ? catstack.get(catstack.size - 1) : basket), basket, randXPos, SCREEN_HEIGHT + STACKOVERLAP);
        catstack.add(c);
        lastSpawnTime = TimeUtils.millis();
    }

    private void readyCatTextures() {
        CATIMAGES = new ObjectMap<>();
        CATIMAGES.put("cat1", new Texture(Gdx.files.internal("Cats/cat1.png")));
        CATIMAGES.put("cat2", new Texture(Gdx.files.internal("Cats/cat2.png")));
        CATIMAGES.put("cat3", new Texture(Gdx.files.internal("Cats/cat3.png")));
    }

    /**
     * call between batch begin and batch end
     * @param batch
     * @param delta
     */
    private void renderBG(SpriteBatch batch, float delta) {
        batch.draw(cloudsBG1, 0, bg1start);
        batch.draw(cloudsBG2, 0, bg2start);

        bg1start -= bgscrollspeed * delta;
        if (bg1start < -720) bg1start = 0;
        bg2start = bg1start + SCREEN_HEIGHT;
        RENDERCOUNT+= 2;
    }

    private void incrementOffset(float delta) {
        if (currentYOffset > targetYOffset) {
            currentYOffset -= foregroundscrollspeed * delta;
            if (currentYOffset < targetYOffset) currentYOffset = targetYOffset;
        } else if (currentYOffset < targetYOffset) {
            currentYOffset += foregroundscrollspeed * delta;
            if (currentYOffset < targetYOffset) currentYOffset = targetYOffset;
        } else {
            shifting = false;
        }
    }

    private void collapseStack() {
        //make all cats fall over with random x velocities to the sides
        for (Cat c : catstack) {
            //precaution
            c.placed = true;
            c.falling = true;
            c.yvelocity = MathUtils.random() * 100;
            c.xvelocity = (MathUtils.random() - 0.5f) * 400;
        }
        gameEndTime = TimeUtils.millis();
    }

    public void updateCOF(Rectangle rect) {
        camerasCOF = rect;
        basket.camcofOffset = (int) (basket.x - camerasCOF.x);
    }
}
