package com.ks.ui.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ks.ui.vo.SalaryDeduction;
import com.ks.ui.vo.Worker;

public class SalaryDeductionRowMapper implements RowMapper<SalaryDeduction> {

    @Override
    public SalaryDeduction mapRow(ResultSet rs, int rowNum) throws SQLException {
        SalaryDeduction salaryDeduction = new SalaryDeduction();
        salaryDeduction.setId(rs.getInt("ID"));
        Worker worker = new Worker();
        worker.setId(rs.getInt("WORKER_ID"));
        salaryDeduction.setWorker(worker);
        salaryDeduction.setDeduction(rs.getBigDecimal("DEDUCTION"));
        salaryDeduction.setDeductionDate(rs.getTimestamp("DEDUCTION_DATE"));
        salaryDeduction.setDeductionNote(rs.getString("DEDUCTION_NOTE"));
        return salaryDeduction;
    }
}
