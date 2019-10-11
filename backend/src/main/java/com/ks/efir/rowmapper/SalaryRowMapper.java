package com.ks.efir.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ks.efir.vo.Salary;
import com.ks.efir.vo.Worker;

public class SalaryRowMapper implements RowMapper<Salary> {

    @Override
    public Salary mapRow(ResultSet rs, int rowNum) throws SQLException {
        Salary salary = new Salary();
        salary.setId(rs.getInt("ID"));
        Worker worker = new Worker();
        worker.setId(rs.getInt("WORKER_ID"));
        salary.setWorker(worker);
        salary.setSalary(rs.getBigDecimal("SALARY"));
        salary.setSalaryDate(rs.getTimestamp("SALARY_DATE"));
        return salary;
    }
}
