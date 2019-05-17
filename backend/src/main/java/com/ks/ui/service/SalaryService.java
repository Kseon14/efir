package com.ks.ui.service;

import com.ks.ui.vo.Salary;

public interface SalaryService {

    void create(Salary salary);

    void deleteByWorkerId(int workerId);

    Salary getByWorkerId(int workerId);

    void update(Salary salary);

}
