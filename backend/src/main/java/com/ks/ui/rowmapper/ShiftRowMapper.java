package com.ks.ui.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.jdbc.core.RowMapper;

import com.ks.ui.vo.Shift;
import com.ks.ui.vo.Worker;

public class ShiftRowMapper implements RowMapper<Shift> {

    @Override
    public Shift mapRow(ResultSet rs, int rowNum) throws SQLException {
        Shift shift = new Shift();
        shift.setId(rs.getInt("ID"));
        Worker worker = new Worker();
        worker.setId(rs.getInt("WORKER_ID"));
        worker.setFirstName(rs.getString("FIRST_NAME"));
        worker.setLastName(rs.getString("LAST_NAME"));
        shift.setWorker(worker);
        shift.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = rs.getString("SHIFT_DATE");
        if (date != null) {
            Date convertedDate;
            try {
                convertedDate = sdf.parse(rs.getString("SHIFT_DATE"));
            } catch (ParseException e) {
                return null;
            }

            shift.setShiftDate(convertedDate);
        }
        return shift;
    }
}
