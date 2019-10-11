package com.ks.efir.vo;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShiftDTO implements Comparable<ShiftDTO>{

    @Getter @Setter
    private Worker worker;

    @Getter @Setter
    private List<Shift> shifts;

    @Override public int compareTo(ShiftDTO o) {
        return ObjectUtils.compare(this.worker.getId(), o.worker.getId());
    }
}
