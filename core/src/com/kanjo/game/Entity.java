package com.kanjo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Entity extends Actor {
	private Body body;
	private boolean isPhysics;
	public Sprite sprite;

	public Entity(float x, float y, Texture t, World world, boolean isPhysics) {

		sprite = new Sprite(t, t.getWidth(), t.getHeight());
		sprite.setSize(sprite.getWidth() / MainGame.WORLD_SCALE, sprite.getHeight() / MainGame.WORLD_SCALE);
		sprite.setX(x);
		sprite.setY(y);
		this.setPhysics(isPhysics);
		sprite.setOriginCenter();
		createBody(world);
	}

	public Entity(int x, int y, Sprite sprt, World world, boolean isPhysics) {

		this.sprite = sprt;
		sprite.setSize(sprite.getWidth() / MainGame.WORLD_SCALE, sprite.getHeight() / MainGame.WORLD_SCALE);
		sprite.setX(x);
		sprite.setY(y);
		this.setPhysics(isPhysics);
		sprite.setOriginCenter();
		createBody(world);

	}

	private void createBody(World world) {
		// Create a physics world, the heart of the simulation. The Vector
		// passed in is gravity

		// Now create a BodyDefinition. This defines the physics objects type
		// and position in the simulation
		BodyDef bodyDef = new BodyDef();
		if (isPhysics) {
			bodyDef.type = BodyDef.BodyType.DynamicBody;
		} else {
			bodyDef.type = BodyDef.BodyType.StaticBody;
		}
		// We are going to use 1 to 1 dimensions. Meaning 1 in physics engine
		// is 1 pixel
		// Set our body to the same position as our sprite
		bodyDef.position.set(sprite.getX(), sprite.getY());

		// Create a body in the world using our definition
		body = world.createBody(bodyDef);

		// Now define the dimensions of the physics shape
		PolygonShape shape = new PolygonShape();
		// We are a box, so this makes sense, no?
		// Basically set the physics polygon to a box with the same dimensions
		// as our sprite
		shape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() / 2);
		System.out.println((sprite.getWidth() + ":" + sprite.getHeight()));

		// FixtureDef is a confusing expression for physical properties
		// Basically this is where you, in addition to defining the shape of the
		// body
		// you also define it's properties like density, restitution and others
		// we will see shortly
		// If you are wondering, density and area are used to calculate over all
		// mass
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 3f;
		body.createFixture(fixtureDef);
	}

	public void draw(Batch batch) {

		update();
		//draw if within viewport.

		sprite.draw(batch);
	}

	public void update() {

		sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
		sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

		if (body.getPosition().y < -40) {
			body.applyForceToCenter(MathUtils.random(30), MathUtils.random(30), true);
			body.setLinearVelocity(0, MathUtils.random(30));
		}

	}

	public boolean isPhysics() {
		return isPhysics;
	}

	public void setPhysics(boolean isPhysics) {
		this.isPhysics = isPhysics;
	}

}
