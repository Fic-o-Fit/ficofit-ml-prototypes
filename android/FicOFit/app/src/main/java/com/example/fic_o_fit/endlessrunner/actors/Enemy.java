package com.example.fic_o_fit.endlessrunner.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.example.fic_o_fit.endlessrunner.box2d.EnemyUserData;
import com.example.fic_o_fit.endlessrunner.utils.Constants;

public class Enemy extends GameActor {

    private TextureRegion trEnemy;

    public Enemy(Body body) {
        super(body);
        trEnemy = new TextureRegion(new Texture(Gdx.files.internal(getUserData().getFilepath())));
    }

    @Override
    public EnemyUserData getUserData() {
        return (EnemyUserData) userData;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        body.setLinearVelocity(getUserData().getLinearVelocity());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(trEnemy, (screenRectangle.x - (screenRectangle.width * 0.1f)),
                screenRectangle.y, screenRectangle.width * 1.2f, screenRectangle.height * 1.1f);
    }

}
