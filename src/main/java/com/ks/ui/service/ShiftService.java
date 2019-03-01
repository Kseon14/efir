package com.ks.ui.service;

import com.ks.ui.vo.Salary;
import com.ks.ui.vo.Shift;
import javafx.scene.chart.PieChart;

import java.util.Date;
import java.util.List;

public interface ShiftService {

    void create(Shift salary);

    void delete(int pk);

    void deleteByWorkerId(int workerId);

    List<Shift> getByWorkerId(int workerId);

    List<Shift> getAll();

    List<Shift> getAllByMonth(Date date);

}
