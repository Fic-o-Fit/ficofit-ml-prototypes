package com.example.fic_o_fit.endlessrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.example.fic_o_fit.endlessrunner.EndlessRunner;
import com.example.fic_o_fit.endlessrunner.stages.GameStage;

public class GameScreen implements Screen {

    public GameStage stage;

    public GameScreen(EndlessRunner endlessRunner) {
        stage = new GameStage(endlessRunner);
    }

    @Override
    public void render(float delta) {
//        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);

        //Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Update the stage
        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

}