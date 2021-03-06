package com.ks.efir.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.jdbc.core.RowMapper;

import com.ks.efir.vo.Worker;

public class WorkerRowMapper implements RowMapper<Worker> {

    @Override
    public Worker mapRow(ResultSet rs, int rowNum) throws SQLException {
        Worker worker = new Worker();
        worker.setId(rs.getInt("ID"));
        worker.setFirstName(rs.getString("FIRST_NAME"));
        worker.setLastName(rs.getString("LAST_NAME"));
        worker.setBaseSalary(rs.getBigDecimal("BASE_SALARY"));
        worker.setStatus(EnumUtils.getEnum(Worker.WorkerStatus.class, rs.getString("STATUS")));
        return worker;
    }
}
