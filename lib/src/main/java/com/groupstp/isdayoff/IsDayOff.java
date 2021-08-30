package com.groupstp.isdayoff;

import com.groupstp.isdayoff.enums.DayType;

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

public class IsDayOff {
    private final String baseUrl = "https://isdayoff.ru/api/";
    private String userAgent = "isdayoff-java-lib/";

    private final IsDayOffProps properties;
    private final IsDayOffCache cache;

    public static IsDayOffBuilder Builder() {
        return new IsDayOffBuilder();
    }

    public IsDayOff(IsDayOffBuilder builder) {
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

    public DayType todayType() {
        Calendar today = Calendar.getInstance();
        String response = getResponseByDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));
        return DayType.fromId(response);
    }

    public DayType tomorrowType() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        String response = getResponseByDate(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH) + 1, tomorrow.get(Calendar.DAY_OF_MONTH));
        return DayType.fromId(response);
    }

    public DayType dayType(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String response = getResponseByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        return DayType.fromId(response);
    }

    public List<IsDayOffDateType> daysTypeByMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String response = getResponseByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), null);
        return parseArrayResponseToList(response, calendar);
    }

    private Calendar buildCalendar(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar;
    }

    public List<IsDayOffDateType> daysTypeByYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 0);
        String response = getResponseByDate(calendar.get(Calendar.YEAR), null, null);
        return parseArrayResponseToList(response, calendar);
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

    public List<IsDayOffDateType> daysTypeByRage(Date startDate, Date endDate) {
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

    private String getResponseByRange(String startDate, String endDate) {
        StringBuilder url = new StringBuilder(baseUrl).append("getdata?");
        if (startDate == null || endDate == null) {
            //Вызывать исключение
            return "199";
        }
        url.append("?date1=").append(startDate).append("&date2=").append(endDate);
        appendProperties(url);

        return request(url.toString());
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
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
