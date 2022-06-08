package com.example.fic_o_fit.endlessrunner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.example.fic_o_fit.endlessrunner.screens.GameScreen;
import com.example.fic_o_fit.endlessrunner.utils.Constants;

public class EndlessRunner extends Game {

	private GameScreen screen;
	private int totalPoints;
	private int currentPoints;
	private int highScore;
	private final int screenWidth;

	public EndlessRunner(int screenWidth){
		this.screenWidth = screenWidth;
	}

	@Override
	public void create () {
		totalPoints = 0;
		currentPoints = 0;
		highScore = 0;
		screen = new GameScreen(this);
		setScreen(screen);
	}

	public void triggerJump(){
		screen.stage.triggerJump();
	}


	public void setTotalPoints(int points){
		totalPoints = points;
	}

	public int getTotalPoints(){
		return totalPoints;
	}

	public void setCurrentPoints(int points){
		currentPoints = points;
	}

	public int getCurrentPoints(){
		return currentPoints;
	}

	public void updateHighScore(){
		if(currentPoints > highScore){
			highScore = currentPoints;
		}
	}

	public int getHighScore(){
		return highScore;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

}
