package com.ks.ui.service;

import com.ks.ui.rowmapper.WorkerRowMapper;
import com.ks.ui.vo.Salary;
import com.ks.ui.vo.Worker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import sun.jvm.hotspot.memory.LinearAllocBlock;

import java.util.List;
import java.util.Objects;

@Service
public class WorkerServiceImpl  implements WorkerService{

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerServiceImpl.class);

    private final
    JdbcTemplate jdbcTemplate;

    private final
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public WorkerServiceImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void create(Worker worker){
        jdbcTemplate.update("INSERT INTO WORKER (FIRST_NAME, LAST_NAME, BASE_SALARY, STATUS) " +
                "VALUES (?,?,?,?)", worker.getFirstName(), worker.getLastName(), worker.getBaseSalary(),
                worker.getStatus().name());
    }

    @Override
    public void delete(int id){
        jdbcTemplate.update("DELETE FROM WORKER WHERE ID=?", id);

    }

    @Override
    public Worker getById(int id){
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        List<Worker> workers = namedParameterJdbcTemplate.query(
                "SELECT ID, FIRST_NAME, LAST_NAME, BASE_SALARY, STATUS  FROM WORKER WHERE ID =:id",
                namedParameters, new WorkerRowMapper());
        return CollectionUtils.isEmpty(workers) ? null : workers.get(0);
    }

    @Override
    public List<Worker> getAll(){
        return jdbcTemplate.query(
                "SELECT ID, FIRST_NAME, LAST_NAME, BASE_SALARY, STATUS  FROM WORKER", new WorkerRowMapper());
    }

    @Override
    public void update(Worker worker){
        jdbcTemplate.update("UPDATE WORKER SET " +
                        "FIRST_NAME= coalesce(?, FIRST_NAME), " +
                        "LAST_NAME=coalesce(?, LAST_NAME), " +
                        "STATUS=coalesce(?, STATUS), " +
                        "BASE_SALARY=coalesce(?, BASE_SALARY) " +
                        "WHERE ID=?",
                worker.getFirstName(), worker.getLastName(), worker.getStatus().name(), worker.getBaseSalary(), worker.getId());
    }

}
