package com.ks.efir.service;

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

import com.ks.efir.rowmapper.SalaryRowMapper;
import com.ks.efir.vo.Salary;
import com.ks.efir.vo.Worker;

@Service
public class SalaryServiceImpl implements SalaryService{

    private static final Logger LOGGER = LoggerFactory.getLogger(SalaryServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final WorkerService workerService;
    private static final int SHIFTS_IN_MONTH = 15;

    @Autowired
    public SalaryServiceImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            WorkerService workerService) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.workerService = workerService;
    }

    @Override
    public Integer create(Salary salary){
        salary.setCreatedDate(new Date());
        List<Salary> dbSalary = getByWorkerIdAndDate(salary.getWorker().getId(), salary.getSalaryDate());
        if (!dbSalary.isEmpty()) {
            return dbSalary.get(0).getId();
        }
        SimpleDateFormat sdf = Utils.getSdf();
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("workerId", salary.getWorker().getId())
                .addValue("createdDate", sdf.format(new Date()))
                .addValue("salaryDate",  sdf.format(salary.getSalaryDate()))
                .addValue("salary",  new BigDecimal(0));
        namedParameterJdbcTemplate.update("INSERT INTO SALARY (WORKER_ID, SALARY, CREATED_DATE, SALARY_DATE) " +
                "VALUES (:workerId,:salary,:createdDate,:salaryDate)", namedParameters, holder);
        return Objects.requireNonNull(holder.getKey()).intValue();
    }

    @Override
    public void deleteByWorkerId(int workerId){
        jdbcTemplate.update("DELETE FROM SALARY WHERE WORKER_ID=?", workerId);
    }


    @Override
    public Salary getById(int id){
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        List<Salary> salaries = namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, SALARY, CREATED_DATE, SALARY_DATE FROM SALARY WHERE ID =:id",
                namedParameters, new SalaryRowMapper());
        return CollectionUtils.isEmpty(salaries) ? null : salaries.get(0);
    }

    @Override
    public Salary getByWorkerId(int workerId){
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("workerId", workerId);
        List<Salary> salaries = namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, SALARY, CREATED_DATE, SALARY_DATE FROM SALARY WHERE WORKER_ID =:workerId",
                namedParameters, new SalaryRowMapper());
        return CollectionUtils.isEmpty(salaries) ? null : salaries.get(0);
    }

    @Override
    public void update(Salary salary){
        int salaryId = create(salary);
        BigDecimal salaryPerDate = getShiftCost(salary.getWorker().getId());
        Salary dbSalary = getById(salaryId);

        jdbcTemplate.update("UPDATE SALARY SET " +
                        "SALARY= coalesce(?, SALARY) " +
                        "WHERE ID=?",
                salaryPerDate.add(dbSalary.getSalary()), salaryId);
    }

    @Override
    public void reduce(int workerId, Date date, int shiftCount) {
        BigDecimal salaryPerDate = getShiftCost(workerId);
        Salary dbSalary = getByWorkerIdAndDate(workerId, date).get(0);
        jdbcTemplate.update("UPDATE SALARY SET " +
                        "SALARY= coalesce(?, SALARY) " +
                        "WHERE ID=?",
                dbSalary.getSalary().subtract(salaryPerDate.multiply(BigDecimal.valueOf(shiftCount))), dbSalary.getId());
    }

    @Override
    public BigDecimal getShiftCost(int workerId) {
        Worker worker = workerService.getById(workerId);
        if (worker == null){
            LOGGER.info("worker by id {} not found", workerId);
            return null;
        }
        BigDecimal baseSalary =  worker.getBaseSalary();
        return baseSalary.divide(BigDecimal.valueOf(SHIFTS_IN_MONTH), BigDecimal.ROUND_UP);
    }

    @Override
    public void addAdjustment(int workerId, Date date, BigDecimal adjustment) {
        Worker worker = workerService.getById(workerId);
        if (worker == null){
            return;
        }
        List<Salary> dbSalary = getByWorkerIdAndDate(workerId, date);
        if (dbSalary.isEmpty()) {
            create(new Salary(workerId, date));
            dbSalary = getByWorkerIdAndDate(workerId, date);
        }
        jdbcTemplate.update("UPDATE SALARY SET " +
                        "SALARY= coalesce(?, SALARY) " +
                        "WHERE ID=?",
              dbSalary.get(0).getSalary().add(adjustment), dbSalary.get(0).getId());
    }

    @Override
    public List<Salary> getByWorkerIdAndDate(int workerId, Date date){
        LocalDate localDate = Utils.getLocalDate(date);
        LOGGER.info("getByWorkerIdAndDate : {}", date);
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("workerId", workerId)
                .addValue("year", year)
                .addValue("month", month);
        return namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, SALARY, CREATED_DATE, SALARY_DATE "
                        + "FROM SALARY "
                        + "WHERE WORKER_ID =:workerId "
                        + "AND YEAR(SALARY_DATE) = :year "
                        + "AND MONTH(SALARY_DATE) = :month" ,
                namedParameters, new SalaryRowMapper());
    }


    @Override
    public List<Salary> getAllByDate(Date date){
        LocalDate localDate = Utils.getLocalDate(date);
        LOGGER.info("getAllByDate : {}",date);
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("year", year)
                .addValue("month", month);
       return namedParameterJdbcTemplate.query(
                "SELECT s.ID, w.ID as WORKER_ID, s.SALARY, s.SALARY_DATE "
                        + "FROM WORKER w "
                        + "LEFT JOIN SALARY s ON w.ID = s.WORKER_ID "
                        + "AND YEAR(s.SALARY_DATE) = :year "
                        + "AND MONTH(s.SALARY_DATE) = :month" ,
                namedParameters, new SalaryRowMapper());
    }
}
