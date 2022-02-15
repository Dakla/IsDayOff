package com.groupstp.isdayoff;

import com.groupstp.isdayoff.enums.DayType;
import com.groupstp.isdayoff.enums.DirectionType;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Основной класс библиотеки
 * Позволяет получить тип дня по датам
 */
public class IsDayOff {
    private final String baseUrl = "https://isdayoff.ru/api/";
    private String userAgent = "isdayoff-java-lib/";

    private final IsDayOffProps properties;
    private final IsDayOffCache cache;

    public static void main(String[] args) {
        IsDayOff build = IsDayOff.Builder().build();
        Calendar instance = Calendar.getInstance();
        instance.set(2021, Calendar.JANUARY, 1);
        int firstDayByType = build.getCountDaysByType(instance.getTime(), DayType.NOT_WORKING_DAY, DirectionType.FUTURE);
        System.out.println(firstDayByType);
    }

    /**
     * Создание экземпляра IsDayOff
     * @return IsDayOffBuilder для указания параметров
     * @see com.groupstp.isdayoff.IsDayOffBuilder
     */
    public static IsDayOffBuilder Builder() {
        return new IsDayOffBuilder();
    }

    protected IsDayOff(IsDayOffBuilder builder) {
        properties = new IsDayOffProps(builder);
        cache = new IsDayOffCache(builder);

        String version = getClass().getPackage().getImplementationVersion();
        if (version == null) {
            version = "DEVELOP";
        }
        userAgent += version;

        Calendar calendar = Calendar.getInstance();
        if (cache.isCached() && !cache.checkCacheFile(calendar.get(Calendar.YEAR))) {
            String response = buildUrlAndSendRequest(calendar.get(Calendar.YEAR), null, null);
            cache.createCacheFile(response, calendar.get(Calendar.YEAR));
        }
    }

    /**
     * Тип сегодняшнего дня
     * @return Тип текущего дня
     * @see com.groupstp.isdayoff.enums.DayType
     */
    public DayType todayType() {
        Calendar today = Calendar.getInstance();
        String response = getResponseByDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        return DayType.fromId(response);
    }

    /**
     * Тип завтрашнего дня
     * @return Тип завтрашнего дня
     * @see com.groupstp.isdayoff.enums.DayType
     */
    public DayType tomorrowType() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        String response = getResponseByDate(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DAY_OF_MONTH));
        return DayType.fromId(response);
    }

    /**
     * Тип конкретного дня
     * @param date день, который нужно проверить
     * @return Тип этого дня
     * @see com.groupstp.isdayoff.enums.DayType
     */
    public DayType dayType(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String response = getResponseByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        return DayType.fromId(response);
    }

    /**
     * Тип всех дней конкретного месяца
     * @param date месяц, который нужно проверить
     * @return Массив IsDayOffDateType с датой и типом для каждого дня месяца
     * @see com.groupstp.isdayoff.IsDayOffDateType
     */
    public List<IsDayOffDateType> daysTypeByMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String response = getResponseByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), null);
        return parseArrayResponseToList(response, calendar);
    }

    /**
     * Тип всех дней конкретного года
     * @param date год, для которого нужно провести проверку
     * @return Массив IsDayOffDateType с датой и типом для каждого дня года
     * @see com.groupstp.isdayoff.IsDayOffDateType
     */
    public List<IsDayOffDateType> daysTypeByYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 0);
        String response = getResponseByDate(calendar.get(Calendar.YEAR), null, null);
        return parseArrayResponseToList(response, calendar);
    }

    /**
     * Проверка года на високосность
     * @param date год
     * @return true, если год високосный и false - если нет
     */
    @Nullable
    public Boolean checkIsLeap(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String response = request(baseUrl + "isleap?year=" + calendar.get(Calendar.YEAR));
        if (response == null || response.equals("0")) {
            return false;
        }
        if (response.equals("1")) {
            return true;
        }
        return null;
    }

    /**
     * Проверка отрезка дат
     * @param startDate  Начало отрезка
     * @param endDate Конец
     * @return Массив IsDayOffDateType с датой и типом для каждого дня отрезка
     * @see com.groupstp.isdayoff.IsDayOffDateType
     */
    public List<IsDayOffDateType> daysTypeByRange(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            //Искл
            return null;
        }

        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diffInDays > 365) {
            return null;
        }

        Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.setTime(startDate);
        Calendar calendarEndDate = Calendar.getInstance();
        calendarEndDate.setTime(endDate);

        if (cache.isCached() &&
                calendarStartDate.get(Calendar.YEAR) == calendarEndDate.get(Calendar.YEAR) &&
                cache.checkCacheFile(calendarStartDate.get(Calendar.YEAR))) {
            String cachedDays = cache.getCachedDays(calendarStartDate, calendarEndDate);
            if (!cachedDays.equals("")) {
                return parseArrayResponseToList(cachedDays, calendarStartDate);
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String startDateStr = simpleDateFormat.format(startDate);
        String endDateStr = simpleDateFormat.format(endDate);
        String response = getResponseByRange(startDateStr, endDateStr);

        return parseArrayResponseToList(response, calendarStartDate);
    }

    /**
     * Получить первый день по типу
     * @param date День, относительно которого начинать отсчет
     * @param dayType Тип дня, который нужно получить
     * @param directionType Направление(Искать в прошлом или будущем)
     * @return Первый день, подходящий под условие
     */
    public Date getFirstDayByType(Date date, DayType dayType, DirectionType directionType) {
        int direction = 0;
        switch (directionType) {
            case PAST: direction = -1; break;
            case FUTURE: direction = 1; break;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        while (!dayType(calendar.getTime()).equals(dayType)) {
            calendar.add(Calendar.DAY_OF_YEAR, direction);
        }
        return calendar.getTime();
    }

    /**
     * Количество дней подряд по типу
     * @param date День, относительно которого начинать отсчет
     * @param dayType Тип дня
     * @param directionType Направление(Искать в прошлом или будущем)
     * @return Кол-во дней, включая день отсчета
     */
    public int getCountDaysByType(Date date, DayType dayType, DirectionType directionType) {
        int direction = 0;
        switch (directionType) {
            case PAST: direction = -1; break;
            case FUTURE: direction = 1; break;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int countDaysByType = 0;
        while (dayType(calendar.getTime()).equals(dayType)) {
            calendar.add(Calendar.DAY_OF_YEAR, direction);
            countDaysByType++;
        }
        return countDaysByType;
    }

    private List<IsDayOffDateType> parseArrayResponseToList(String response, Calendar startDate) {
        List<IsDayOffDateType> result = new ArrayList<>();
        char[] days = response.toCharArray();
        for (char day : days) {
            DayType dayType = DayType.fromId(String.valueOf(day));
            result.add(new IsDayOffDateType(startDate.getTime(), dayType));
            startDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }

    private String getResponseByRange(String startDate, String endDate) {
        StringBuilder url = new StringBuilder(baseUrl).append("getdata?");
        if (startDate == null || endDate == null) {
            //Вызывать исключение
            return "199";
        }
        url.append("date1=").append(startDate).append("&date2=").append(endDate).append("&");
        appendProperties(url);

        return request(url.toString());
    }

    private Calendar buildCalendar(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar;
    }

    private String getResponseByDate(Integer year, Integer month, Integer day) {
        if (cache.isCached()) {
            if (!cache.checkCacheFile(year)) {
                String response = buildUrlAndSendRequest(year, null, null);
                DayType dayType = DayType.fromId(response);
                if (DayType.ERROR_DATE.equals(dayType) || DayType.NOT_FOUND.equals(dayType) || DayType.SERVER_ERROR.equals(dayType)) {
                    return response;
                }
                cache.createCacheFile(response, year);
            }
            return cache.getCachedDay(year, month, day);
        }

        return buildUrlAndSendRequest(year, month + 1, day);
    }

    private String buildUrlAndSendRequest(Integer year, Integer month, Integer day) {
        StringBuilder url = new StringBuilder(baseUrl).append("getdata?");
        if (year != null) {
            url.append("year=").append(year).append("&");
        } else {
            //Вызывать исключение
            return "199";
        }
        if (month != null) {
            url.append("month=").append(month).append("&");
        }
        if (day != null) {
            url.append("day=").append(day).append("&");
        }
        appendProperties(url);

        return request(url.toString());
    }

    private void appendProperties(StringBuilder url) {
        url
                .append("cc=").append(properties.getLocale().getId()).append("&")
                .append("pre=").append(properties.getPreHolidaysDay()).append("&")
                .append("covid=").append(properties.getCovidWorkingDays()).append("&")
                .append("sd=").append(properties.getSixDaysWorkWeek());
    }


    private String request(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", userAgent)
                .build();
        try {
                HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String result = null;
            	if(response.statusCode() == 200) 
            		result = response.body().toString();
            		
            	return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
