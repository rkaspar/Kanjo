package com.kanjo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Hud implements Disposable {

	public Stage stage;
	private Viewport viewport;
	private Camera camera;
	//score && time tracking variables
	private Integer worldTimer;
	private float timeCount;
	private static Integer score;
	private boolean timeUp;
	int x, y;
	//Scene2D Widgets
	private Label countdownLabel, timeLabel, linkLabel;
	private static Label scoreLabel;
	private final Label mouseLabel;

	public Hud(SpriteBatch sb, OrthographicCamera camera) {
		//define tracking variables
		worldTimer = 250;
		timeCount = 0;
		score = 0;
		this.camera = camera;

		//setup the HUD viewport using a new camera seperate from gamecam
		//define stage using that viewport and games spritebatch
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera());
		stage = new Stage(viewport, sb);

		//define labels using the String, and a Label style consisting of a font and color
		countdownLabel = new Label("", new LabelStyle(new BitmapFont(), Color.WHITE));
		scoreLabel = new Label("", new LabelStyle(new BitmapFont(), Color.WHITE));
		timeLabel = new Label("World Position", new LabelStyle(new BitmapFont(), Color.WHITE));
		linkLabel = new Label("Screen Position", new LabelStyle(new BitmapFont(), Color.WHITE));
		mouseLabel = new Label("", new LabelStyle(new BitmapFont(), Color.WHITE));
		//define a table used to organize hud's labels
		Table table = new Table();
		table.top();
		table.setFillParent(true);

		//add labels to table, padding the top, and giving them all equal width with expandX
		table.add(linkLabel).expandX().padTop(10);
		table.add(timeLabel).expandX().padTop(10);
		table.row();
		table.add(mouseLabel).expandX();
		table.add(countdownLabel).expandX();


		//add table to the stage
		stage.addActor(table);
	}

	public void update(float dt) {

		x = Gdx.input.getX() - this.viewport.getScreenX();
		y = Gdx.input.getY() - this.viewport.getScreenY();

		mouseLabel.setText(Integer.toString(x) + ":" + Integer.toString(y));

		Vector3 v = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		countdownLabel.setText(v.x + ":" + v.y);

	}

	public static void addScore(int value) {
		score += value;

	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	public boolean isTimeUp() {
		return timeUp;
	}


	public static Label getScoreLabel() {
		return scoreLabel;
	}

	public static Integer getScore() {
		return score;
	}

}