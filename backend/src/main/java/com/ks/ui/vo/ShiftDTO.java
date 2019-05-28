package com.ks.ui.vo;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ShiftDTO {

    @Getter @Setter
    private Worker worker;

    @Getter @Setter
    private List<Date> shiftDates;
}
