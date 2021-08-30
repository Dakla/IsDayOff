package com.groupstp.isdayoff;

import com.groupstp.isdayoff.enums.DayType;

import java.util.Date;

public class IsDayOffDateType {
    private Date date;
    private DayType dayType;

    public IsDayOffDateType(Date date, DayType dayType) {
        this.date = date;
        this.dayType = dayType;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }
}
