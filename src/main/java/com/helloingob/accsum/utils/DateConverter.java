package com.helloingob.accsum.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateConverter {

    public static LocalDate convert(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
