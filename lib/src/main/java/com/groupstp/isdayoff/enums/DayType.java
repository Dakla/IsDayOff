package com.groupstp.isdayoff.enums;

import com.groupstp.isdayoff.IsDayOffBuilder;

import javax.annotation.Nullable;

/**
 * Тип дня
 */
public enum DayType {
    /**
     * Рабочий день
     */
    WORKING_DAY("0"),
    /**
     * Выходной
     */
    NOT_WORKING_DAY("1"),
    /**
     * Сокращенный день
     * Появляется, если установить PreHolidaysDay
     * @see IsDayOffBuilder#addPreHolidaysDay()
     */
    SHORT_DAY("2"),
    /**
     * Рабочий день в пандемию
     * Появляется, если установить CovidWorkingDays
     * @see IsDayOffBuilder#addCovidWorkingDays()
     */
    WORKING_DAY_COVID("4"),
    /**
     * Ошибка в дате
     */
    ERROR_DATE("100"),
    /**
     * Данные не найдены
     */
    NOT_FOUND("101"),
    /**
     * Ошибка сервиса
     */
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
