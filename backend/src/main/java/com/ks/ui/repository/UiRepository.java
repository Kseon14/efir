package com.ks.ui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ks.ui.vo.Salary;
import com.ks.ui.vo.Shift;
import com.ks.ui.vo.Worker;

public class UiRepository {

    @Repository
    public interface SalaryRepository extends JpaRepository<Salary, Long> {

    }

    @Repository
    public interface ShiftRepository extends JpaRepository<Shift, Long> {

    }

    @Repository
    public interface WorkerRepository extends JpaRepository<Worker, Long> {

    }

}
