package com.ks.ui.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

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

import com.ks.ui.rowmapper.SalaryRowMapper;
import com.ks.ui.vo.Salary;
import com.ks.ui.vo.Worker;

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
        List<Salary> dbSalary = getByWorkerIdAndDate(salary);
        if (dbSalary != null) {
            return dbSalary.get(0).getId();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
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
        Worker worker = workerService.getById(salary.getWorker().getId());
        BigDecimal baseSalary =  worker.getBaseSalary();
        BigDecimal salaryPerDate = baseSalary.divide(BigDecimal.valueOf(SHIFTS_IN_MONTH), BigDecimal.ROUND_UP);
        Salary dbSalary = getById(salaryId);

        jdbcTemplate.update("UPDATE SALARY SET " +
                        "SALARY= coalesce(?, SALARY) " +
                        "WHERE ID=?",
                salaryPerDate.add(dbSalary.getSalary()), salaryId);
    }

    @Override
    public void reduce(Salary salary) {
        Worker worker = workerService.getById(salary.getWorker().getId());
        BigDecimal baseSalary =  worker.getBaseSalary();
        BigDecimal salaryPerDate = baseSalary.divide(BigDecimal.valueOf(SHIFTS_IN_MONTH), BigDecimal.ROUND_UP);
        Salary dbSalary = getByWorkerIdAndDate(salary).get(0);

        jdbcTemplate.update("UPDATE SALARY SET " +
                        "SALARY= coalesce(?, SALARY) " +
                        "WHERE ID=?",
                dbSalary.getSalary().subtract(salaryPerDate), dbSalary.getId());
    }

    @Override
    public List<Salary> getByWorkerIdAndDate(Salary salary){
        LocalDate localDate = Utils.getLocalDate(salary.getSalaryDate());
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("workerId", salary.getWorker().getId())
                .addValue("year", year)
                .addValue("month", month);
        List<Salary> salaries = namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, SALARY, CREATED_DATE, SALARY_DATE "
                        + "FROM SALARY "
                        + "WHERE WORKER_ID =:workerId "
                        + "AND YEAR(SALARY_DATE) = :year "
                        + "AND MONTH(SALARY_DATE) = :month" ,
                namedParameters, new SalaryRowMapper());
        return CollectionUtils.isEmpty(salaries) ? null : salaries;
    }


    @Override
    public List<Salary> getAllByDate(Date date){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("year", year)
                .addValue("month", month);
        List<Salary> salaries = namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, SALARY, CREATED_DATE, SALARY_DATE "
                        + "FROM SALARY "
                        + "WHERE YEAR(SALARY_DATE) = :year "
                        + "AND MONTH(SALARY_DATE) = :month" ,
                namedParameters, new SalaryRowMapper());
        return CollectionUtils.isEmpty(salaries) ? null : salaries;
    }


}
