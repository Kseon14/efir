package com.ks.ui.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.ks.ui.rowmapper.SalaryDeductionRowMapper;
import com.ks.ui.vo.SalaryDeduction;

@Service
public class SalaryDeductionServiceImpl implements SalaryDeductionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalaryDeductionServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public SalaryDeductionServiceImpl(JdbcTemplate jdbcTemplate,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Integer create(SalaryDeduction salaryDeduction){
        salaryDeduction.setCreatedDate(new Date());
        List<SalaryDeduction> dbSalaryDeduction = getByWorkerIdAndDate(salaryDeduction);
        if (dbSalaryDeduction != null) {
            return dbSalaryDeduction.get(0).getId();
        }
        SimpleDateFormat sdf = Utils.getSdf();
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("workerId", salaryDeduction.getWorker().getId())
                .addValue("createdDate", sdf.format(new Date()))
                .addValue("deductionDate",  sdf.format(salaryDeduction.getDeductionDate()))
                .addValue("deduction",  new BigDecimal(0));
        namedParameterJdbcTemplate.update("INSERT INTO SALARY_DEDUCTION (WORKER_ID, DEDUCTION, CREATED_DATE, DEDUCTION_DATE) " +
                "VALUES (:workerId,:deduction,:createdDate,:deductionDate)", namedParameters, holder);
        return Objects.requireNonNull(holder.getKey()).intValue();
    }

    @Override
    public void deleteByWorkerId(int workerId){
        jdbcTemplate.update("DELETE FROM SALARY_DEDUCTION WHERE WORKER_ID=?", workerId);
    }

    @Override
    public List<SalaryDeduction> getByWorkerIdAndDate(SalaryDeduction salaryDeduction){
        LocalDate localDate = Utils.getLocalDate(salaryDeduction.getDeductionDate());
        LOGGER.info("getByWorkerIdAndDate : {}", salaryDeduction.getDeductionDate());
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("workerId", salaryDeduction.getWorker().getId())
                .addValue("year", year)
                .addValue("month", month);
        List<SalaryDeduction> salaryDeductions = namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, DEDUCTION, CREATED_DATE, DEDUCTION_DATE "
                        + "FROM SALARY_DEDUCTION "
                        + "WHERE WORKER_ID =:workerId "
                        + "AND YEAR(DEDUCTION_DATE) = :year "
                        + "AND MONTH(DEDUCTION_DATE) = :month" ,
                namedParameters, new SalaryDeductionRowMapper());
        return CollectionUtils.isEmpty(salaryDeductions) ? null : salaryDeductions;
    }

    @Override
    public List<SalaryDeduction> getAllByDate(Date date){
        LocalDate localDate = Utils.getLocalDate(date);
        LOGGER.info("getAllByDate : {}", date);
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("year", year)
                .addValue("month", month);
        List<SalaryDeduction> salaryDeductions = namedParameterJdbcTemplate.query(
                "SELECT sd.ID, w.ID as WORKER_ID, sd.DEDUCTION, sd.DEDUCTION_DATE "
                        + "FROM WORKER w "
                        + "LEFT JOIN SALARY_DEDUCTION sd ON w.ID = sd.WORKER_ID "
                        + "AND YEAR(sd.DEDUCTION_DATE) = :year "
                        + "AND MONTH(sd.DEDUCTION_DATE) = :month" ,
                namedParameters, new SalaryDeductionRowMapper());
        return CollectionUtils.isEmpty(salaryDeductions) ? null : salaryDeductions;
    }

    @Override
    public void update(SalaryDeduction salaryDeduction){
        int salaryId = create(salaryDeduction);
        jdbcTemplate.update("UPDATE SALARY_DEDUCTION SET " +
                        "DEDUCTION = coalesce(?, DEDUCTION) " +
                        "WHERE ID = ?",
                salaryDeduction.getDeduction(), salaryId);
    }

    @Override
    public SalaryDeduction getById(int id){
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        List<SalaryDeduction> salaryDeductions = namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, DEDUCTION, CREATED_DATE, DEDUCTION_DATE FROM SALARY_DEDUCTION WHERE ID =:id",
                namedParameters, new SalaryDeductionRowMapper());
        return CollectionUtils.isEmpty(salaryDeductions) ? null : salaryDeductions.get(0);
    }

}
