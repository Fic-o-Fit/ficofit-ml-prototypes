package com.example.fic_o_fit.endlessrunner.box2d;

import com.example.fic_o_fit.endlessrunner.enums.UserDataType;

public class GroundUserData extends UserData {

    public GroundUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.GROUND;
    }

}
