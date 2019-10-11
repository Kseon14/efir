package com.ks.efir.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    static LocalDate getLocalDate(Date date) {
        return date.toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
    }

    public static SimpleDateFormat getSdf(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf;
    }

}
