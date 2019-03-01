package com.ks.ui.rowmapper;


import com.ks.ui.vo.Salary;
import com.ks.ui.vo.Shift;
import com.ks.ui.vo.Worker;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShiftRowMapper implements RowMapper<Shift> {

    @Override
    public Shift mapRow(ResultSet rs, int rowNum) throws SQLException {
        Shift shift = new Shift();
        shift.setId(rs.getInt("ID"));
        Worker worker = new Worker();
        worker.setId(rs.getInt("WORKER_ID"));
        shift.setWorker(worker);
        shift.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
        shift.setShiftDate(rs.getDate("SHIFT_DATE"));
        return shift;
    }
}
