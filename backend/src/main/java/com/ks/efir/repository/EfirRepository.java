package com.ks.efir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ks.efir.vo.Salary;
import com.ks.efir.vo.Shift;
import com.ks.efir.vo.Worker;

public class EfirRepository {

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
