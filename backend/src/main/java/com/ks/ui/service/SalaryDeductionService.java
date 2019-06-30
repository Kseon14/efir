package com.ks.ui.service;

import java.util.Date;
import java.util.List;

import com.ks.ui.vo.SalaryDeduction;

public interface SalaryDeductionService {

    Integer create(SalaryDeduction salaryDeduction);

    void deleteByWorkerId(int workerId);

    void deleteById(int id);

    List<SalaryDeduction> getByWorkerIdAndDate(SalaryDeduction salaryDeduction);

    List<SalaryDeduction> getAllByDate(Date date);

    void update(SalaryDeduction salaryDeduction);

    SalaryDeduction getById(int id);

}
