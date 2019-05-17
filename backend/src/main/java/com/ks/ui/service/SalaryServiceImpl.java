package com.ks.ui.service;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import com.ks.ui.rowmapper.SalaryRowMapper;
import com.ks.ui.vo.Salary;

@Service
public class SalaryServiceImpl implements SalaryService{

    private static final Logger LOGGER = LoggerFactory.getLogger(SalaryServiceImpl.class);

    private final
    JdbcTemplate jdbcTemplate;

    private final
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public SalaryServiceImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void create(Salary salary){
        Salary dbSalary = getByWorkerId(salary.getWorker().getId());
        if (dbSalary != null) {
            return;
        }
        jdbcTemplate.update("INSERT INTO SALARY (WORKER_ID, SALARY, CREATED_DATE) " +
                "VALUES (?,?,?)", salary.getWorker().getId(), salary.getSalary(), salary.getCreatedDate());
    }

    @Override
    public void deleteByWorkerId(int workerId){
        jdbcTemplate.update("DELETE FROM SALARY WHERE WORKER_ID=?", workerId);
    }

    @Override
    public Salary getByWorkerId(int workerId){
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("workerId", workerId);
        List<Salary> salaries = namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, SALARY, CREATED_DATE FROM SALARY WHERE WORKER_ID =:workerId",
                namedParameters, new SalaryRowMapper());
        return CollectionUtils.isEmpty(salaries) ? null : salaries.get(0);
    }

    @Override
    public void update(Salary salary){
        jdbcTemplate.update("UPDATE SALARY SET " +
                        "SALARY= coalesce(?, SALARY) " +
                        "WHERE WORKER_ID=?",
                salary.getSalary(), salary.getWorker().getId());
    }


}
