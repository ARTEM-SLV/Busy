package com.arty.busy.enums;

import com.arty.busy.R;

public enum Sex {
    sex(R.string.cd_sex), male(R.string.male), female(R.string.female);

    private final int resID;

    Sex(int resID) {
        this.resID = resID;
    }

    public int getResID(){
        return resID;
    }
}
