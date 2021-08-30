package com.groupstp.isdayoff;

import com.groupstp.isdayoff.enums.LocalesType;

public class IsDayOffProps {
    private final LocalesType locale;
    private final Integer preHolidaysDay;
    private final Integer sixDaysWorkWeek;
    private final Integer covidWorkingDays;

    public IsDayOffProps(IsDayOffBuilder builder) {
        locale = builder.getLocale();
        preHolidaysDay = builder.getPreHolidaysDay();
        sixDaysWorkWeek = builder.getSixDaysWorkWeek();
        covidWorkingDays = builder.getCovidWorkingDays();
    }

    public LocalesType getLocale() {
        return locale;
    }

    public Integer getPreHolidaysDay() {
        return preHolidaysDay;
    }

    public Integer getSixDaysWorkWeek() {
        return sixDaysWorkWeek;
    }

    public Integer getCovidWorkingDays() {
        return covidWorkingDays;
    }
}
