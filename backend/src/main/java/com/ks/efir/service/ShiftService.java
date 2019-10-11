package com.ks.efir.service;

import java.util.Date;
import java.util.List;

import com.ks.efir.vo.Shift;
import com.ks.efir.vo.ShiftDTO;

public interface ShiftService {

    void create(Shift salary);

    void delete(int pk);

    void deleteByWorkerId(int workerId);

    List<Shift> getByWorkerId(int workerId);

    List<ShiftDTO> getAll();

    List<ShiftDTO> getAllByMonth(Date date);

    void closeShift(Shift shift);

}
