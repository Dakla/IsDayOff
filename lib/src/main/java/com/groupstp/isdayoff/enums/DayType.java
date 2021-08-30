package com.groupstp.isdayoff.enums;

import javax.annotation.Nullable;

public enum DayType {
    WORKING_DAY("0"),
    NOT_WORKING_DAY("1"),
    SHORT_DAY("2"),
    WORKING_DAY_COVID("4"),
    ERROR_DATE("100"),
    NOT_FOUND("101"),
    SERVER_ERROR("199");

    private String id;

    DayType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public Boolean isWorkingDay() {
        if (this.equals(WORKING_DAY) || this.equals(WORKING_DAY_COVID) || this.equals(SHORT_DAY)) {
            return true;
        } else if (this.equals(NOT_WORKING_DAY)) {
            return false;
        }
        return null;
    }

    @Nullable
    public static DayType fromId(String id) {
        for (DayType value : DayType.values()) {
            if (value.getId().equals(id)) {
                return value;
            }
        }
        return null;
    }
}
