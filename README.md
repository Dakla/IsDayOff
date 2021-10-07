# IsDayOff
Просто небольшая библиотека для работы с сервисом [IsDayOff](https://isdayoff.ru/)

## Возможности
- Проверка дня/месяца/года/отрезка дат на статус выходного/рабочего дня
- Проверка года на високосность
- Кэширование
- Получение данных с учётом предпраздничных дней
- Получение данных с учётом нерабочих дней во время пандемии COVID-19
- Получение данных с учётом шестидневной рабочей недели
- Поддерживаемые страны: Россия, Украина, США, Беларусь, Казахстан, Узбекистан, Турция
- Получение первого рабочего/выходного дня
- Вычисление кол-ва выходных/рабочих дней подряд(хорошо сочетается с предыдущим пунктом)

##  Примеры использования
Использование с базовыми настройками
```java
    IsDayOff isDayOff = IsDayOff.Builder().build();
    DayType todayType = isDayOff.todayType(); //Тип текущего дня в России
```
Указание страны
```java
    IsDayOff isDayOff = IsDayOff.Builder()
        .setLocale(LocalesType.UKRAINE)
        .build();
    List<IsDayOffDateType> todayType = isDayOff.daysTypeByMonth(new Date()); //Тип всех дней текущего месяца в Украине
```

Установка шестидневной рабочей недели и указание сокращенных рабочих дней
```java
    IsDayOff isDayOff = IsDayOff.Builder()
        .setSixDaysWorkWeek()
        .addPreHolidaysDay()
        .build();
    List<IsDayOffDateType> todayType = isDayOff.daysTypeByYear(new Date()); //Тип всех дней за год
```

## Кэширование
По умолчанию кэширование включено и данные сохраняются в рабочую папку проекта. Кэш сохраняется за текущий год и обновляется по умолчанию раз в 30 дней

Отключить кэширование можно следующим образом
```java
    IsDayOff isDayOff = IsDayOff.Builder()
                    .setCache(false)
                    .build();
```
Изменение папки для хранения кэша
```java
    IsDayOff isDayOff = IsDayOff.Builder()
                    .setCacheDir("/isDayOff/cache")
                    .build();
```

Изменение периода обновления кэша(в днях)
```java
    IsDayOff isDayOff = IsDayOff.Builder()
                    .setCacheStorageDays(123)
                    .build();
```
