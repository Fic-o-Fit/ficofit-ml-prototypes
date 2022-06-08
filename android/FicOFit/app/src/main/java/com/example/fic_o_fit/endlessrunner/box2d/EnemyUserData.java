package com.example.fic_o_fit.endlessrunner.box2d;

import com.badlogic.gdx.math.Vector2;
import com.example.fic_o_fit.endlessrunner.enums.UserDataType;
import com.example.fic_o_fit.endlessrunner.utils.Constants;

public class EnemyUserData extends UserData {

    private Vector2 linearVelocity;
    private String filepath;

    public EnemyUserData(float width, float height, String filepath) {
        super(width, height);
        userDataType = UserDataType.ENEMY;
        linearVelocity = Constants.ENEMY_LINEAR_VELOCITY;
        this.filepath = filepath;
    }

    public void setLinearVelocity(Vector2 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public Vector2 getLinearVelocity() {
        return linearVelocity;
    }

    public String getFilepath() {
        return filepath;
    }

}
