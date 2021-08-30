package com.groupstp.isdayoff;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IsDayOffCache {
    private final Boolean cache;
    private String cacheDir;
    private final Integer cacheStorageDays;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    public IsDayOffCache(IsDayOffBuilder builder) {
        cache = builder.getCache();
        cacheStorageDays = builder.getCacheStorageDays();
        cacheDir = builder.getCacheDir();
    }

    public void createCacheFile(String data, int year) {
        File cacheFile = getCacheFile(year);
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(cacheFile)){
            fileOutputStream.write((simpleDateFormat.format(new Date()) + "\r\n" + data).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getCacheFile(int year) {
        String fileName = "IsDayOffCache " + year;
        if (!cacheDir.endsWith("/") && !cacheDir.isEmpty()) {
            cacheDir += "/";
        }
        return new File(cacheDir + fileName + ".txt");
    }

    public boolean checkCacheFile(int year) {
        File cacheFile = getCacheFile(year);
        if (cacheFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile))) {
                String fileDateLine = reader.readLine();
                Date fileCreateDate = simpleDateFormat.parse(fileDateLine);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(fileCreateDate);
                calendar.add(Calendar.DAY_OF_YEAR, cacheStorageDays);
                fileCreateDate = calendar.getTime();
                return fileCreateDate.after(new Date());
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public String getCachedDay(Integer year, Integer month, Integer day) {
        Calendar calendar = Calendar.getInstance();
        Integer tempMonth = month;
        Integer tempDay = day;
        if (tempMonth == null) {
            tempMonth = 0;
        }
        if (tempDay == null) {
            tempDay = 1;
        }
        calendar.set(year, tempMonth, tempDay);
        File cacheFile = getCacheFile(calendar.get(Calendar.YEAR));
        if (cacheFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile))) {
                reader.readLine();
                if (day != null && month != null) {
                    char[] data = reader.readLine().toCharArray();
                    return String.valueOf(data[calendar.get(Calendar.DAY_OF_YEAR)]);
                } else {
                    if (month != null) {
                        int firstDay = calendar.get(Calendar.DAY_OF_YEAR) - 1;
                        int lastDay = firstDay + calendar.getMaximum(Calendar.DAY_OF_MONTH);
                        return reader.readLine().substring(firstDay, lastDay);
                    } else {
                        int firstDay = calendar.get(Calendar.DAY_OF_YEAR) - 1;
                        int lastDay = firstDay + calendar.getMaximum(Calendar.DAY_OF_YEAR) - 1;
                        return reader.readLine().substring(firstDay, lastDay);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getCachedDays(Calendar startDate, Calendar endDate) {
        File cacheFile = getCacheFile(startDate.get(Calendar.YEAR));
        if (cacheFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile))) {
                reader.readLine();
                String data = reader.readLine();
                int days = endDate.get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR);
                int day = startDate.get(Calendar.DAY_OF_YEAR);
                days += day;
                return data.substring(day, days);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public Boolean isCached() {
        return cache;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public Integer getCacheStorageDays() {
        return cacheStorageDays;
    }
}
