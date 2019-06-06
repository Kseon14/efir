package com.ks.ui.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShiftDTO {

    @Getter @Setter
    private Worker worker;

    @Getter @Setter
    private List<Date> shiftDates;
}
