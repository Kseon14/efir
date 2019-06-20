package com.ks.ui.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

public class Utils {

    public static LocalDate getLocalDate(Date date) {
        return date.toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
    }

}
