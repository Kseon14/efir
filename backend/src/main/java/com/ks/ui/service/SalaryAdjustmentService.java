package com.ks.ui.service;

import java.util.Date;
import java.util.List;

import com.ks.ui.vo.SalaryAdjustment;

public interface SalaryAdjustmentService {

    Integer create(SalaryAdjustment salaryAdjustment);

    void deleteByWorkerId(int workerId);

    void deleteById(int id);

    List<SalaryAdjustment> getByWorkerIdAndDate(SalaryAdjustment salaryAdjustment);

    List<SalaryAdjustment> getAllByDate(Date date);

    void update(SalaryAdjustment salaryAdjustment);

    SalaryAdjustment getById(int id);

}
