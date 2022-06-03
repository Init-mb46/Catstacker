package com.csa.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
    static ObjectMap<String, Texture> GARBAGEITEMS;
    static ObjectMap<String, Sound> MEOWSOUNDS;
    Music bgMusic; // BACKGROUND MUSIC TRACK BY FoolBoyMedia // https://freesound.org/people/FoolBoyMedia/sounds/257997/
    Array<Cat> catstack;
    Array<FallingObject> renderItems;

    final static float ACCELERATION = -40f;
    final static int STACKOVERLAP = 20; //px
    final static int RENDERBOUNDS = -250; //do not render anythng under this many pixels
    int bg1start;
    int bg2start;
    final float bgscrollspeed = 70f;
    final float foregroundscrollspeed = 200f;
    final float globalhorizontalspeed = 240f;
    static final float COLLAPSE_POWER_MULTIPLIER = 4;
    final float timeScoreMultiplier = 5;
    final float catScoreMultiplier = 100;

    int targetYOffset = 0;
    int currentYOffset = 0;
    Rectangle camerasCOF;
    boolean shifting = false;
    static int RENDERCOUNT = 0;

    long lastSpawnTime;
    long lastGarbageSpawnTime;
    long gameStartTime;
    long gameEndTime;
    int score;
    int catsStacked;
    boolean newHighScore = false;

    boolean stopRenderFunctions = false;
    boolean renderGameOverText = false;
    Cat previousCat;

    float chance;

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
        renderItems = new Array<>();
        readyCatTextures();
        readyMeowSounds();
        readyGarbageTextures();

        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("bgMusic2.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(0f);

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
        bgMusic.play();
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
        for (FallingObject Obj : renderItems) {
            Obj.render(delta);
        }
        if (renderGameOverText) {
            for (Textbox t : GameText.GAMEOVERTEXT)
                t.draw(game.batch);

            if (newHighScore) GameText.NEWHIGHSCORE.draw(game.batch);
            GameText.scoreText(score, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 3 * 2 - 50, 0, 25, true).draw(game.batch);
            GameText.catStackText(catsStacked, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 3 * 2 - 90, 0, 25, true).draw(game.batch);

        } else if (!stopRenderFunctions) {
            //render game time and score only
            int time = (int) ((TimeUtils.millis() - gameStartTime) / 1000);
            score = (int) (catsStacked * catScoreMultiplier * (1 + chance) + (((TimeUtils.millis() - gameStartTime) * timeScoreMultiplier * (1 + chance))/ 100));
            GameText.timerText(time, 15, SCREEN_HEIGHT - 10, 50, 20, false).draw(game.batch);
            GameText.catStackText(catsStacked, 15, SCREEN_HEIGHT - 80, 120, 20, false).draw(game.batch);
            GameText.scoreText(score, 15, SCREEN_HEIGHT - 45, 50, 20, false).draw(game.batch);
        }
        game.batch.end();
        if (stopRenderFunctions) {
            if (!renderGameOverText && TimeUtils.millis() - gameEndTime > 2000) renderGameOverText = true;
            if (renderGameOverText && Gdx.input.isKeyPressed(Input.Keys.SPACE)) game.reset();
            if (bgMusic.getVolume() > 0) {
                bgMusic.setVolume(MathUtils.clamp(bgMusic.getVolume() - 0.05f * delta, 0, 1));
            }
        } else {
            if (game.COLLAPSE) {
                //do collapse work
                stopRenderFunctions = true;
                updateHorizontalSpeeds(0);
                collapseStack();
                if (Catstacker.HIGHSCORE < score) {
                    Catstacker.HIGHSCORE = score;
                    newHighScore = true;
                }
                return;
            }
            spawn();
            if (bgMusic.getVolume() < 0.2f){
                bgMusic.setVolume(MathUtils.clamp(bgMusic.getVolume() + 0.05f * delta, 0, 0.2f));
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                //move everything to the right
                updateHorizontalSpeeds(globalhorizontalspeed);
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                updateHorizontalSpeeds(-globalhorizontalspeed);
            } else {
                updateHorizontalSpeeds(0);
            }

            if(Catstacker.sandbox) {
                //debug stuff
                if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) collapseXcats(catstack.size);
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) collapseXcats(1);
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) collapseXcats(2);
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) collapseXcats(3);
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) collapseXcats(4);
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) collapseXcats(5);
                if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) sandboxAddCat(true);
                if (Gdx.input.isKeyJustPressed(Input.Keys.T)) sandboxAddCat(false);
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) game.collapse();
            }

            //for moving sprites down as stack grows
            if (camerasCOF.y > SCREEN_HEIGHT / 3 && !shifting) {
                shifting = true;
                targetYOffset -= camerasCOF.height - STACKOVERLAP;
            } else if (camerasCOF.y < camerasCOF.height && !shifting) {
                shifting = true;
                targetYOffset -= camerasCOF.y - ground.height + STACKOVERLAP;
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
        renderItems.clear();
        MEOWSOUNDS.clear();
        bgMusic.dispose();
    }

    private void sandboxAddCat(boolean clear) {
        if (clear) {
            for (FallingObject f : renderItems) {
                if (f instanceof Garbage || (f instanceof Cat && ((Cat)f).falling)) {
                    renderItems.removeValue(f,true);
                }
            }
        }

        String texture = "cat" + MathUtils.random(1, CATIMAGES.size);
        int randXPos = MathUtils.random(STACKOVERLAP, SCREEN_WIDTH - Cat.DEFWIDTH - STACKOVERLAP);
        Cat c = new Cat(game, texture, (previousCat != null ? previousCat : basket), basket, randXPos, SCREEN_HEIGHT + STACKOVERLAP);
        c.placed = true;
        c.falling = false;
        c.x = catstack.size > 0 ? catstack.get(catstack.size - 1).x : basket.x + basket.width / 2 - c.width / 2;
        c.y = catstack.size > 0 ? catstack.get(catstack.size - 1).y + catstack.get(catstack.size - 1).height - STACKOVERLAP : basket.y + basket.height - STACKOVERLAP;
        c.placedXoffset = (int) (c.x - c.base.x);
        c.placedYoffset = (int) (c.y - c.base.y);
        renderItems.add(c);
        updateCOF(c, true);
    }

    private void updateHorizontalSpeeds(float speed) {
        basket.xvelocity = speed;
        for (Cat c : catstack) {
            c.xvelocity = speed;
        }
        camerasCOF.x = MathUtils.clamp(camerasCOF.x, 0, SCREEN_WIDTH - camerasCOF.width);
        basket.x = basket.camcofOffset + camerasCOF.x;
    }

    private void spawn() {
        int spawnRate = (int) MathUtils.clamp(5000 / Math.abs(MathUtils.log(MathUtils.E, (TimeUtils.millis() - gameStartTime) / 1000 * 0.1f)), 1000, 5000);
        chance = (float) Math.sqrt(MathUtils.clamp(MathUtils.log(MathUtils.E, (TimeUtils.millis() - gameStartTime) / 1000f * 0.05f), 0, 50)) * 0.075f;
//        System.out.println(chance);
        if ((TimeUtils.millis() - gameStartTime) / 1000 >= 10 && TimeUtils.millis() - lastGarbageSpawnTime > spawnRate / 4) {
            //start garbage chance
            lastGarbageSpawnTime = (TimeUtils.millis() - spawnRate / 5);
            if (MathUtils.random() < chance) {
                spawnGarbage();
            }
        }
        if (TimeUtils.millis() - lastSpawnTime < spawnRate) return;

        lastSpawnTime = TimeUtils.millis();
        int catNum = MathUtils.random(1, CATIMAGES.size);
        String texture = "cat" + catNum;
        int randXPos = MathUtils.random(STACKOVERLAP, SCREEN_WIDTH - Cat.DEFWIDTH - STACKOVERLAP);
        Cat c = new Cat(game, texture, (previousCat != null ? previousCat : basket), basket, randXPos, SCREEN_HEIGHT + STACKOVERLAP);
        renderItems.add(c);
    }

    private void spawnGarbage() {
        String randomGarbage = "garbage" + MathUtils.random(1, GARBAGEITEMS.size);
        renderItems.add(new Garbage(game, randomGarbage, MathUtils.random(STACKOVERLAP, SCREEN_WIDTH - Garbage.DEFWIDTH - STACKOVERLAP), SCREEN_HEIGHT + Garbage.GARBAGESIZE));
        lastGarbageSpawnTime = TimeUtils.millis();
//        System.out.println("Garbage spawned");
    }

    private void readyCatTextures() {
        CATIMAGES = new ObjectMap<>();
        CATIMAGES.put("cat1", new Texture(Gdx.files.internal("Cats/cat1.png")));
        CATIMAGES.put("cat2", new Texture(Gdx.files.internal("Cats/cat2.png")));
        CATIMAGES.put("cat3", new Texture(Gdx.files.internal("Cats/cat3.png")));
    }

    public void readyGarbageTextures() {
        GARBAGEITEMS = new ObjectMap<>();
        GARBAGEITEMS.put("garbage1", new Texture(Gdx.files.internal("Garbage/trash.png")));
        GARBAGEITEMS.put("garbage2", new Texture(Gdx.files.internal("Garbage/bone.png")));
    }

    private void readyMeowSounds() {
        MEOWSOUNDS = new ObjectMap<>();
        MEOWSOUNDS.put("meow1", Gdx.audio.newSound(Gdx.files.internal("MeowSounds/meow1.wav")));
        MEOWSOUNDS.put("meow2", Gdx.audio.newSound(Gdx.files.internal("MeowSounds/meow2.wav")));
        MEOWSOUNDS.put("meow3", Gdx.audio.newSound(Gdx.files.internal("MeowSounds/meow3.wav")));
        MEOWSOUNDS.put("meow4", Gdx.audio.newSound(Gdx.files.internal("MeowSounds/meow4.wav")));
        MEOWSOUNDS.put("break", Gdx.audio.newSound(Gdx.files.internal("MeowSounds/break.wav")));
        MEOWSOUNDS.put("collapse", Gdx.audio.newSound(Gdx.files.internal("MeowSounds/collapse.wav")));
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
        float speed = (float) (foregroundscrollspeed  * (1 + Math.abs(currentYOffset - targetYOffset) * .01));
//        if (speed > 200) System.out.println(speed);
        if (currentYOffset > targetYOffset) {
            currentYOffset -= speed * delta;
            if (currentYOffset < targetYOffset) currentYOffset = targetYOffset;
        } else if (currentYOffset < targetYOffset) {
            currentYOffset += speed * delta;
            if (currentYOffset > targetYOffset) currentYOffset = targetYOffset;
        } else {
            shifting = false;
        }
    }

    private void collapseStack() {
        //make all cats fall over with random x velocities to the sides
        for (FallingObject obj : renderItems) {
            if (obj instanceof Cat) {
                ((Cat) obj).collapse(COLLAPSE_POWER_MULTIPLIER);
            } else if (obj instanceof Garbage) {
                ((Garbage) obj).collapse(COLLAPSE_POWER_MULTIPLIER);
            }
        }
        gameEndTime = TimeUtils.millis();
        long id = MEOWSOUNDS.get("collapse").play();
        MEOWSOUNDS.get("collapse").setPitch(id, 2f);
    }

    public void collapseXcats(int num) {
        if (catstack.size < 1) return;
        catsStacked -= num;
        catsStacked = MathUtils.clamp(catsStacked,0, Math.abs(catsStacked));
//        System.out.println(score);
        for(int i = catstack.size - 1; i >= 0 && num > 0; i-- ) {
                catstack.get(i).collapse(COLLAPSE_POWER_MULTIPLIER);
                catstack.removeIndex(i);
                num--;
        }
        previousCat = catstack.size > 0 ? catstack.get(catstack.size - 1) : null;
        updateCOF(previousCat, false);
        long id = MEOWSOUNDS.get("break").play();
        MEOWSOUNDS.get("break").setPitch(id, 2);
    }

    public void updateCOF(Rectangle rect, boolean check) {
        if (rect == null) rect = basket;
        camerasCOF = rect;
        basket.camcofOffset = (int) (basket.x - camerasCOF.x);
        shifting = false;
        if (!check) return;
        String songName = "meow" +MathUtils.random(1,4);
        long id = MEOWSOUNDS.get(songName).play();
        MEOWSOUNDS.get(songName).setVolume(id, 0.05f);
        MEOWSOUNDS.get(songName).setPitch(id, 2f);
        if (rect instanceof Cat) {
            catstack.add((Cat) rect);
            catsStacked++;
            previousCat = (Cat) rect;
        }
    }
}
