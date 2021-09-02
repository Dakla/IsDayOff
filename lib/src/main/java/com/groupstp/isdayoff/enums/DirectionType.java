package com.groupstp.isdayoff.enums;

import javax.annotation.Nullable;

public enum DirectionType {
    PAST(0),
    FUTURE(1);

    private int id;

    DirectionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
