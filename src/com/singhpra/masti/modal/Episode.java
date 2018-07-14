package com.singhpra.masti.modal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Episode implements Comparable<Episode> {

    private static final Map<String, Integer> MONTHS = new LinkedHashMap<>();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MM yyyy");
    static {
        String[] monthName = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
        int count = 0;
        for (String m : monthName)
            MONTHS.put(m, ++count);
    }

    private String serialKey;
    private String date;
    private String videoUrl;
    private String hash;

    public String getSerialKey() {
        return serialKey;
    }

    public void setSerialKey(String serialKey) {
        this.serialKey = serialKey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Date parseDate() throws ParseException {
        String date = this.date;
        int selectedMonth = -1;
        for (String month : MONTHS.keySet()) {
            if (date.contains(month)) {
                selectedMonth = MONTHS.get(month);
                date = date.replace(month, String.format("%02d", selectedMonth));
                break;
            }
        }
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        if (selectedMonth > (cal.get(Calendar.MONTH) + 1)) {
            year -= 1;
        }
        date += " " + year;

        return DATE_FORMAT.parse(date);
    }

    @Override
    public String toString() {
        return "[serialKey=" + serialKey + ", date=" + date + ", videoUrl=" + videoUrl + "]";
    }

    @Override
    public int compareTo(Episode that) {
        try {
            return -this.parseDate().compareTo(that.parseDate());
        } catch (ParseException e) {
            throw new RuntimeException("Couldn't parse the dates: " + this.date + ", " + that.date, e);
        }
    }

}
