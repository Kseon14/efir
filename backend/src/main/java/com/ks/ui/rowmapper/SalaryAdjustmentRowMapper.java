package com.ks.ui.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ks.ui.vo.SalaryAdjustment;
import com.ks.ui.vo.Worker;

public class SalaryAdjustmentRowMapper implements RowMapper<SalaryAdjustment> {

    @Override
    public SalaryAdjustment mapRow(ResultSet rs, int rowNum) throws SQLException {
        SalaryAdjustment salaryAdjustment = new SalaryAdjustment();
        salaryAdjustment.setId(rs.getInt("ID"));
        Worker worker = new Worker();
        worker.setId(rs.getInt("WORKER_ID"));
        salaryAdjustment.setWorker(worker);
        salaryAdjustment.setAdjustment(rs.getBigDecimal("ADJUSTMENT"));
        salaryAdjustment.setAdjustmentDate(rs.getTimestamp("ADJUSTMENT_DATE"));
        salaryAdjustment.setAdjustmentNote(rs.getString("ADJUSTMENT_NOTE"));
        return salaryAdjustment;
    }
}
