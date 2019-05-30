package com.ks.ui.service;

import java.util.Date;
import java.util.List;

import com.ks.ui.vo.Shift;
import com.ks.ui.vo.ShiftDTO;

public interface ShiftService {

    void create(Shift salary);

    void delete(int pk);

    void deleteByWorkerId(int workerId);

    List<Shift> getByWorkerId(int workerId);

    List<ShiftDTO> getAll();

    List<ShiftDTO> getAllByMonth(Date date);

}
