package com.ks.ui.service;

import java.util.Date;
import java.util.List;

import com.ks.ui.vo.Salary;

public interface SalaryService {

    Integer create(Salary salary);

    void deleteByWorkerId(int workerId);

    Salary getByWorkerId(int workerId);

    List<Salary> getByWorkerIdAndDate(Salary salary);

    List<Salary> getAllByDate(Date date);

    void update(Salary salary);

    Salary getById(int id);

    void reduce(Salary salary);

}
