package com.ks.efir.service;

import java.util.Date;
import java.util.List;

import com.ks.efir.vo.SalaryAdjustment;
import com.ks.efir.vo.State;

public interface SalaryAdjustmentService {

    Integer create(SalaryAdjustment salaryAdjustment);

    void deleteByWorkerId(int workerId);

    void deleteById(int id);

    List<SalaryAdjustment> getByWorkerIdAndDate(SalaryAdjustment salaryAdjustment);

    List<SalaryAdjustment> getByWorkerIdAndExactDate(SalaryAdjustment salaryAdjustment);

    List<SalaryAdjustment> getByWorkerIdAndSateBeforeDate(Date date, int workerId, State state);

    List<SalaryAdjustment> getAllByDate(Date date);

    void update(SalaryAdjustment salaryAdjustment);

    SalaryAdjustment getById(int id);

    void updateState(List<Integer> ids, State state);

}
