package com.arty.busy.enums;

import com.arty.busy.R;

public enum Sex {
    male(R.string.male), female(R.string.female);

    private int resID;

    Sex(int resID) {
        this.resID = resID;
    }

    public int getResID(){
        return resID;
    }
}
