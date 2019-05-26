package com.ks.ui.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import com.ks.ui.rowmapper.ShiftRowMapper;
import com.ks.ui.vo.Shift;

@Service
public class ShiftServiceImpl implements ShiftService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftServiceImpl.class);

    private final
    JdbcTemplate jdbcTemplate;

    private final
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ShiftServiceImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void create(Shift shift){
        jdbcTemplate.update("INSERT INTO SHIFT (WORKER_ID, CREATED_DATE, SHIFT_DATE) VALUES (?,?,?)",
                shift.getWorker().getId(), shift.getCreatedDate(), shift.getShiftDate());
    }

    @Override
    public void delete(int id){
        jdbcTemplate.update("DELETE FROM SHIFT WHERE ID=?", id);

    }

    @Override
    public void deleteByWorkerId(int workerId){
        jdbcTemplate.update("DELETE FROM SHIFT WHERE WORKER_ID=?", workerId);

    }

    @Override
    public List<Shift> getByWorkerId(int workerId){
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("workerId", workerId);
        return namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, CREATED_DATE, SHIFT_DATE FROM SHIFT WHERE WORKER_ID =:workerId",
                namedParameters, new ShiftRowMapper());
    }

    @Override
    public List<Shift> getAll(){
        return jdbcTemplate.query(
                "SELECT sh.ID, w.FIRST_NAME, w.LAST_NAME, w.ID as WORKER_ID, sh.CREATED_DATE, sh.SHIFT_DATE FROM SHIFT sh "
                        + "LEFT JOIN WORKER w on sh.WORKER_ID = w.ID", new ShiftRowMapper());
    }

    @Override
    public List<Shift> getAllByMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("date", sdf.format(date));
        return namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, CREATED_DATE, SHIFT_DATE FROM SHIFT WHERE SHIFT_DATE =:date",
                namedParameters, new ShiftRowMapper());
    }

}
