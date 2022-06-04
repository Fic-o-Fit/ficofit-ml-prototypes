package com.example.fic_o_fit.endlessrunner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.example.fic_o_fit.endlessrunner.screens.GameScreen;

public class EndlessRunner extends Game {

	private GameScreen screen;
	public int totalPoints;
	public int currentPoints;
	public int highScore;

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

	public void updateHighScore(){
		if(currentPoints > highScore){
			highScore = currentPoints;
		}
	}

//	public void addTotalPoints(int points){
//		totalPoints += points;
//	}
//
//	public int getTotalPoints(){
//		return totalPoints;
//	}

}
