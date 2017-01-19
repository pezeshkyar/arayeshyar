package com.example.doctorsbuilding.nav;

public enum LviActionType {
    select(0),
    remove(1),
    edit(2);
    private final int lviActiontype;

    LviActionType(int actiontype) {
        this.lviActiontype = actiontype;
    }

    public int getActiontype() {
        return this.lviActiontype;
    }
}