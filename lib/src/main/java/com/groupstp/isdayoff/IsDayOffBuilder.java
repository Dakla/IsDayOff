package com.groupstp.isdayoff;

import com.groupstp.isdayoff.enums.LocalesType;

import java.io.IOException;

public class IsDayOffBuilder {
    private Boolean cache;
    private LocalesType locale;
    private String cacheDir;
    private Integer cacheStorageDays;
    private Integer preHolidaysDay;
    private Integer sixDaysWorkWeek;
    private Integer covidWorkingDays;

    public IsDayOffBuilder() {
        cache = true;
        locale = LocalesType.RUSSIA;
        cacheDir = "";
        cacheStorageDays = 30;
        preHolidaysDay = 0;
        sixDaysWorkWeek = 0;
        covidWorkingDays = 0;
    }

    public IsDayOffBuilder setCache(Boolean cache) {
        this.cache = cache;
        return this;
    }

    public IsDayOffBuilder setLocale(LocalesType locale) {
        this.locale = locale;
        return this;
    }

    public IsDayOffBuilder setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
        return this;
    }

    public IsDayOffBuilder setCacheStorageDays(Integer cacheStorageDays) {
        this.cacheStorageDays = cacheStorageDays;
        return this;
    }

    public IsDayOffBuilder addPreHolidaysDay() {
        this.preHolidaysDay = 1;
        return this;
    }

    public IsDayOffBuilder setSixDaysWorkWeek() {
        this.sixDaysWorkWeek = 1;
        return this;
    }

    public IsDayOffBuilder addCovidWorkingDays() {
        this.covidWorkingDays = 1;
        return this;
    }


    public IsDayOff build() {
        return new IsDayOff(this);
    }

    public Boolean getCache() {
        return cache;
    }

    public LocalesType getLocale() {
        return locale;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public Integer getCacheStorageDays() {
        return cacheStorageDays;
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
