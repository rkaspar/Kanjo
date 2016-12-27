package com.kanjo.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MainGame implements ApplicationListener {

	// World config
	public World world;
	private int mVelocityIter = 8;
	private int mPositionIter = 3;
	private float mSecondsPerStep = 1 / 60f;
	public static float WORLD_SCALE = 32f;
	private float WORLD_WIDTH = 100f;
	private float WORLD_HEIGHT = 100f;
	private float mAccumulator; // time accumulator to fix the physics step.

	public SpriteBatch batch;
	Texture img;

	private Array<Entity> Entities;
	public OrthographicCamera camera, hudCamera;
	Box2DDebugRenderer debugRenderer;
	public ContactHandler contactHandler = new ContactHandler();
	ParticleEffect snowEffect = new ParticleEffect();
	private boolean debug = true;
	BitmapFont font12;
	FreeTypeFontGenerator fontgen;
	FreeTypeFontParameter fontparams;

	private Hud hud;

	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("circle.png");
		Entities = new Array<Entity>();
		world = new World(new Vector2(0, -9.8f), true);
		world.setContactListener(contactHandler);
		debugRenderer = new Box2DDebugRenderer();

		//aspect ratio
		float w = (float) Gdx.graphics.getWidth();
		float h = (float) Gdx.graphics.getHeight();
		float aspectRatio = (float) w / (float) h;
		//end aspect ratio
		fontgen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font_main.ttf"));
		fontparams = new FreeTypeFontParameter();
		fontparams.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:";
		fontparams.genMipMaps = true;
		fontparams.minFilter = Texture.TextureFilter.MipMapLinearLinear;
		fontparams.magFilter = Texture.TextureFilter.MipMapNearestNearest;
		fontparams.size = (int) (12 / WORLD_SCALE);

		font12 = fontgen.generateFont(fontparams);
		font12.setUseIntegerPositions(false);
		fontgen.dispose();

		camera = new OrthographicCamera(WORLD_HEIGHT * aspectRatio / WORLD_SCALE, WORLD_HEIGHT / WORLD_SCALE);
		camera.position.set(WORLD_WIDTH / 2 / WORLD_SCALE, WORLD_HEIGHT / 2 / WORLD_SCALE, 0);
		hud = new Hud(batch, camera);


		//DEBUG load test entities
		for (int i = 0; i < 5; i++) {
			Entity e = new Entity(scale(MathUtils.random(10, 300)), scale(MathUtils.random(10, 300)), img,
					world, true);
			Entities.add(e);
		}
		// add floor
		Entities.add(new Entity(0, 0, new Texture("floor.png"), world, false));

		// making a change.

		snowEffect.load(Gdx.files.local("effects/snow_effect.p"), Gdx.files.local("effects"));
		snowEffect.setPosition(0, camera.viewportHeight);
		snowEffect.scaleEffect(1 / WORLD_SCALE);
		snowEffect.start();

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float delta = Gdx.graphics.getDeltaTime();
		input();

		update();

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		debugRenderer.render(world, camera.combined);

		doPhysicsStep(delta);

		// start batch
		batch.begin();

		if (debug) {
			renderDebug();
		}

		for (Entity e : Entities) {
			e.draw(batch);
		}

		snowEffect.draw(batch, delta);

		batch.end();

	}

	private void update() {

		hud.update(Gdx.graphics.getDeltaTime());
		batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	private void renderDebug() {
		font12.setColor(Color.WHITE);

		font12.draw(batch, "Camera Viewport: \n" + "X:" + camera.position.x + "\n Y:" + camera.position.y + "\n H:"
				+ camera.viewportHeight + "\n W:" + camera.viewportWidth, 15, 0);

	}

	private void input() {

		// pan camera

		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			if (debug) {
				Gdx.app.exit();
			}
		}

		if (Gdx.input.isKeyPressed(Keys.D)) {
			camera.translate(2, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			camera.translate(-2, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			camera.translate(0, -2, 0);
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			camera.translate(0, 2, 0);
		}

		if (Gdx.input.isKeyPressed(Keys.Q)) {

			if (!(camera.viewportHeight < 10)) {

				camera.viewportHeight -= scale(90);
				camera.viewportWidth -= scale(160);
			}
		}
		if (Gdx.input.isKeyPressed(Keys.E)) {
			camera.viewportHeight += scale(90);
			camera.viewportWidth += scale(160);
		}

	}


	@Override
	public void resize(int width, int height) {
		float aspectRatio = (float) width / (float) height;

	}

	@Override
	public void dispose() {
		batch.dispose();

	}

	private void doPhysicsStep(float delta) {

		mAccumulator += delta;

		while (mAccumulator >= mSecondsPerStep) {
			world.step(mSecondsPerStep, mVelocityIter, mPositionIter);
			mAccumulator -= mSecondsPerStep;
		}

	}

	public float scale(float num) {
		return num / WORLD_SCALE;
	}
}
