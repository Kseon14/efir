package com.ks.ui.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

public class Utils {

    public static LocalDate getLocalDate(Date date) {
        LocalDate localDate = date.toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        return localDate;
    }

}
