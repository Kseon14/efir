package com.ks.efir.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
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

import com.ks.efir.rowmapper.SalaryAdjustmentRowMapper;
import com.ks.efir.vo.SalaryAdjustment;
import com.ks.efir.vo.State;

@Service
public class SalaryAdjustmentServiceImpl implements SalaryAdjustmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalaryAdjustmentServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SalaryService salaryService;

    @Autowired
    public SalaryAdjustmentServiceImpl(JdbcTemplate jdbcTemplate,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            SalaryService salaryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.salaryService = salaryService;
    }

    @Override
    public Integer create(SalaryAdjustment salaryAdjustment){
        if (salaryAdjustment.getAdjustment().equals(BigDecimal.ZERO)) {
            return 0;
        }
        salaryAdjustment.setCreatedDate(new Date());
        List<SalaryAdjustment> dbSalaryAdjustment = getByWorkerIdAndExactDate(salaryAdjustment);
        if (CollectionUtils.isNotEmpty(dbSalaryAdjustment)) {
            return dbSalaryAdjustment.get(0).getId();
        }
        SimpleDateFormat sdf = Utils.getSdf();
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("workerId", salaryAdjustment.getWorker().getId())
                .addValue("createdDate", sdf.format(new Date()))
                .addValue("adjustmentState", State.UNPAID.name())
                .addValue("adjustmentDate",  sdf.format(salaryAdjustment.getAdjustmentDate()))
                .addValue("adjustment", ObjectUtils.defaultIfNull(salaryAdjustment.getAdjustment(), new BigDecimal(0)))
                .addValue("adjustmentNote",  salaryAdjustment.getAdjustmentNote());
        namedParameterJdbcTemplate.update(
                "INSERT INTO SALARY_ADJUSTMENT (WORKER_ID, ADJUSTMENT, CREATED_DATE, ADJUSTMENT_DATE, ADJUSTMENT_NOTE, ADJUSTMENT_STATE) " +
                "VALUES (:workerId, :adjustment, :createdDate, :adjustmentDate, :adjustmentNote, :adjustmentState)", namedParameters, holder);
        salaryService.addAdjustment(salaryAdjustment.getWorker().getId(), salaryAdjustment.getAdjustmentDate(),
                salaryAdjustment.getAdjustment());
        return Objects.requireNonNull(holder.getKey()).intValue();
    }

    @Override
    public void deleteByWorkerId(int workerId){
        jdbcTemplate.update("DELETE FROM SALARY_ADJUSTMENT WHERE WORKER_ID=?", workerId);
    }

    @Override
    public void deleteById(int id){
        SalaryAdjustment salaryAdjustment = getById(id);
        jdbcTemplate.update("DELETE FROM SALARY_ADJUSTMENT WHERE ID=?", id);
        salaryService.addAdjustment(salaryAdjustment.getWorker().getId(), salaryAdjustment.getAdjustmentDate(),
                salaryAdjustment.getAdjustment().negate());
    }

    @Override
    public List<SalaryAdjustment> getByWorkerIdAndExactDate(SalaryAdjustment salaryAdjustment){
        LocalDate localDate = Utils.getLocalDate(salaryAdjustment.getAdjustmentDate());
        LOGGER.info("getByWorkerIdAndDate : {}", salaryAdjustment.getAdjustmentDate());
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        int day = localDate.getDayOfMonth();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("workerId", salaryAdjustment.getWorker().getId())
                .addValue("year", year)
                .addValue("month", month)
                .addValue("day", day);
        List<SalaryAdjustment> salaryAdjustments = namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, ADJUSTMENT, CREATED_DATE, ADJUSTMENT_DATE, ADJUSTMENT_NOTE, ADJUSTMENT_STATE "
                        + "FROM SALARY_ADJUSTMENT "
                        + "WHERE WORKER_ID =:workerId "
                        + "AND YEAR(ADJUSTMENT_DATE) = :year "
                        + "AND MONTH(ADJUSTMENT_DATE) = :month "
                        + "AND DAY(ADJUSTMENT_DATE) = :day" ,
                namedParameters, new SalaryAdjustmentRowMapper());
        return salaryAdjustments;
    }

    @Override
    public List<SalaryAdjustment> getByWorkerIdAndSateBeforeDate(Date date, int workerId, State state){
        LocalDate localDate = Utils.getLocalDate(date);
        LOGGER.info("getByWorkerIdAndDate : {}", date);
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        int day = localDate.getDayOfMonth();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("workerId", workerId)
                .addValue("year", year)
                .addValue("month", month)
                .addValue("state", state.name())
                .addValue("day", day);
        List<SalaryAdjustment> salaryAdjustments = namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, ADJUSTMENT, CREATED_DATE, ADJUSTMENT_DATE, ADJUSTMENT_NOTE, ADJUSTMENT_STATE "
                        + "FROM SALARY_ADJUSTMENT "
                        + "WHERE WORKER_ID =:workerId "
                        + "AND YEAR(ADJUSTMENT_DATE) = :year "
                        + "AND MONTH(ADJUSTMENT_DATE) = :month "
                        + "AND DAY(ADJUSTMENT_DATE) <= :day "
                        + "AND ADJUSTMENT_STATE = :state " ,
                namedParameters, new SalaryAdjustmentRowMapper());
        return salaryAdjustments;
    }

    @Override
    public List<SalaryAdjustment> getByWorkerIdAndDate(SalaryAdjustment salaryAdjustment){
        LocalDate localDate = Utils.getLocalDate(salaryAdjustment.getAdjustmentDate());
        LOGGER.info("getByWorkerIdAndDate : {}", salaryAdjustment.getAdjustmentDate());
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("workerId", salaryAdjustment.getWorker().getId())
                .addValue("year", year)
                .addValue("month", month);
        List<SalaryAdjustment> salaryAdjustments = namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, ADJUSTMENT, CREATED_DATE, ADJUSTMENT_DATE, ADJUSTMENT_NOTE, ADJUSTMENT_STATE "
                        + "FROM SALARY_ADJUSTMENT "
                        + "WHERE WORKER_ID =:workerId "
                        + "AND YEAR(ADJUSTMENT_DATE) = :year "
                        + "AND MONTH(ADJUSTMENT_DATE) = :month ",
                namedParameters, new SalaryAdjustmentRowMapper());
        return salaryAdjustments;
    }

    @Override
    public List<SalaryAdjustment> getAllByDate(Date date){
        LocalDate localDate = Utils.getLocalDate(date);
        LOGGER.info("getAllByDate : {}", date);
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("year", year)
                .addValue("month", month);
        List<SalaryAdjustment> salaryAdjustments = namedParameterJdbcTemplate.query(
                "SELECT sd.ID, w.ID as WORKER_ID, sd.ADJUSTMENT, sd.ADJUSTMENT_DATE, sd.ADJUSTMENT_NOTE, sd.ADJUSTMENT_STATE "
                        + "FROM WORKER w "
                        + "LEFT JOIN SALARY_ADJUSTMENT sd ON w.ID = sd.WORKER_ID "
                        + "AND YEAR(sd.ADJUSTMENT_DATE) = :year "
                        + "AND MONTH(sd.ADJUSTMENT_DATE) = :month" ,
                namedParameters, new SalaryAdjustmentRowMapper());
        return salaryAdjustments;
    }

    @Override
    public void update(SalaryAdjustment salaryAdjustment){
        int salaryId = create(salaryAdjustment);
        jdbcTemplate.update("UPDATE SALARY_ADJUSTMENT SET " +
                        "ADJUSTMENT = coalesce(?, ADJUSTMENT), ADJUSTMENT_NOTE = ? " +
                        "WHERE ID = ?",
                salaryAdjustment.getAdjustment(), salaryId, salaryAdjustment.getAdjustmentNote());
    }

    @Override
    public void updateState(List<Integer> ids, State state) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("ids", ids)
                .addValue("state", state.name());
        namedParameterJdbcTemplate.update("UPDATE SALARY_ADJUSTMENT SET ADJUSTMENT_STATE = :state WHERE ID IN (:ids)", namedParameters);
    }

    @Override
    public SalaryAdjustment getById(int id){
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        List<SalaryAdjustment> salaryAdjustments = namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, ADJUSTMENT, CREATED_DATE, ADJUSTMENT_DATE, ADJUSTMENT_NOTE, ADJUSTMENT_STATE"
                        + " FROM SALARY_ADJUSTMENT WHERE ID =:id",
                namedParameters, new SalaryAdjustmentRowMapper());
        return CollectionUtils.isEmpty(salaryAdjustments) ? null : salaryAdjustments.get(0);
    }

}
