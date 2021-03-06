package com.ks.efir.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ks.efir.vo.Salary;

public interface SalaryService {

    Integer create(Salary salary);

    void deleteByWorkerId(int workerId);

    Salary getByWorkerId(int workerId);

    List<Salary> getByWorkerIdAndDate(int workerId, Date date);

    List<Salary> getAllByDate(Date date);

    void update(Salary salary);

    Salary getById(int id);

    void reduce(int workerId, Date date,  int shiftCount);

    void addAdjustment(int workerId, Date date, BigDecimal adjustment);

    BigDecimal getShiftCost(int workerId);

}
