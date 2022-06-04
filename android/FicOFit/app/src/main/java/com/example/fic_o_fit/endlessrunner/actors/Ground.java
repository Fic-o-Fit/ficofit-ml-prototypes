package com.example.fic_o_fit.endlessrunner.actors;

import com.badlogic.gdx.physics.box2d.Body;
import com.example.fic_o_fit.endlessrunner.box2d.GroundUserData;

public class Ground extends GameActor {

    public Ground(Body body) {
        super(body);
    }

    @Override
    public GroundUserData getUserData() {
        return (GroundUserData) userData;
    }

}
