package com.groupstp.isdayoff.enums;

import javax.annotation.Nullable;

public enum LocalesType {
    RUSSIA("ru"),
    UKRAINE("ua"),
    KAZAKHSTAN("kz"),
    BELARUS("by"),
    US("us");

    private String id;

    LocalesType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static LocalesType fromId(String id) {
        for (LocalesType value : LocalesType.values()) {
            if (value.getId().equals(id)) {
                return value;
            }
        }
        return null;
    }
}
