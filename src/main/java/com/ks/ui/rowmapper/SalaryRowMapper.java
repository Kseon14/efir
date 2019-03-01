package com.ks.ui.rowmapper;


import com.ks.ui.vo.Salary;
import com.ks.ui.vo.Worker;
import com.ks.ui.vo.WorkerStatus;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SalaryRowMapper implements RowMapper<Salary> {

    @Override
    public Salary mapRow(ResultSet rs, int rowNum) throws SQLException {
        Salary salary = new Salary();
        salary.setId(rs.getInt("ID"));
        Worker worker = new Worker();
        worker.setId(rs.getInt("WORKER_ID"));
        salary.setWorker(worker);
        salary.setSalary(rs.getBigDecimal("SALARY"));
        salary.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
        return salary;
    }
}
