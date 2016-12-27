package com.kanjo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class gameCamera extends OrthographicCamera {

	public gameCamera(int width, int height) {

		this.view.setToOrtho2D(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, width, height);
		this.translate(this.viewportWidth / 2, this.viewportHeight / 2);
	}

	@Override
	public void update() {

		if (Gdx.input.isButtonPressed(Keys.D)) {
			this.translate(20f, 0, 0);
		}
		if (Gdx.input.isButtonPressed(Keys.A)) {
			this.translate(-20f, 0, 0);
		}
		super.update();
	}

	@Override
	public void update(boolean updateFrustum) {
		// TODO Auto-generated method stub

	}

}
