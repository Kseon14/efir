package com.ks.ui.rowmapper;


import com.ks.ui.vo.Worker;
import com.ks.ui.vo.WorkerStatus;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkerRowMapper implements RowMapper<Worker> {

    @Override
    public Worker mapRow(ResultSet rs, int rowNum) throws SQLException {
        Worker worker = new Worker();
        worker.setId(rs.getInt("ID"));
        worker.setFirstName(rs.getString("FIRST_NAME"));
        worker.setLastName(rs.getString("LAST_NAME"));
        worker.setBaseSalary(rs.getBigDecimal("BASE_SALARY"));
        worker.setStatus(EnumUtils.getEnum(WorkerStatus.class, rs.getString("STATUS")));
        return worker;
    }
}
