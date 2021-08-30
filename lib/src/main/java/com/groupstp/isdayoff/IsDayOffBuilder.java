package com.groupstp.isdayoff;

import com.groupstp.isdayoff.enums.LocalesType;

/**
 * Builder для установки параметров
 */
public class IsDayOffBuilder {
    /**
     * Установка кэша
     * По умолчанию включен
     * Кэширует весь текущий год
     */
    private Boolean cache;
    /**
     * Страна, для которой проводить проверку
     * По умолчанию - Россия
     */
    private LocalesType locale;
    /**
     * Путь, по которому следует сохранять кэш дат
     * По умолчанию рабочая папка проекта
     */
    private String cacheDir;
    /**
     * Время хранения кэша в днях
     * По умолчанию 30
     */
    private Integer cacheStorageDays;
    /**
     * Получение данных с учётом предпраздничных дней
     * По умолчанию отключено
     */
    private Integer preHolidaysDay;
    /**
     * Получение данных с учётом шестидневной рабочей недели
     * По умолчанию отключено
     */
    private Integer sixDaysWorkWeek;
    /**
     * Получение данных с учётом нерабочих дней во время пандемии COVID-19
     * По умолчанию отключено
     */
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

    /**
     * Включение/отключение кэша
     * @param cache true - кэш включен, false - кэш выключен
     */
    public IsDayOffBuilder setCache(Boolean cache) {
        this.cache = cache;
        return this;
    }

    /**
     * Указание старны, для которой проводить проверку
     * @param locale Страна из перечисления
     * @see com.groupstp.isdayoff.enums.LocalesType
     */
    public IsDayOffBuilder setLocale(LocalesType locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Установка пути для хранения кэша
     * @param cacheDir Путь
     */
    public IsDayOffBuilder setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
        return this;
    }

    /**
     * Кол-во дней, через которые следует обновлять данные кэша
     * @param cacheStorageDays Кл-во дней
     */
    public IsDayOffBuilder setCacheStorageDays(Integer cacheStorageDays) {
        this.cacheStorageDays = cacheStorageDays;
        return this;
    }

    /**
     * Добавить предпраздничные(сокращенные) дни
     */
    public IsDayOffBuilder addPreHolidaysDay() {
        this.preHolidaysDay = 1;
        return this;
    }

    /**
     * Установить шестидневную рабочую неделю
     */
    public IsDayOffBuilder setSixDaysWorkWeek() {
        this.sixDaysWorkWeek = 1;
        return this;
    }

    /**
     * Игнорировать нерабочий дни в связи с пандемией COVID19
     * @return
     */
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
