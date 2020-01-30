package com.ks.efir.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import com.ks.efir.rowmapper.ShiftRowMapper;
import com.ks.efir.vo.Salary;
import com.ks.efir.vo.SalaryAdjustment;
import com.ks.efir.vo.Shift;
import com.ks.efir.vo.ShiftDTO;
import com.ks.efir.vo.State;

@Service
public class ShiftServiceImpl implements ShiftService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SalaryService salaryService;
    private final SalaryAdjustmentService salaryAdjustmentService;

    @Autowired
    public ShiftServiceImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            SalaryService salaryService, SalaryAdjustmentService salaryAdjustmentService) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.salaryService = salaryService;
        this.salaryAdjustmentService = salaryAdjustmentService;
    }

    @Override
    public void create(Shift shift){
        SimpleDateFormat sdf = Utils.getSdf();
        Shift dbShift = getByWorkerIdAndDate(shift.getWorker().getId(), shift.getShiftDate());
        if (dbShift != null){
            return;
        }
        int workerId = shift.getWorker().getId();
        String formattedDate = sdf.format(shift.getShiftDate());
        jdbcTemplate.update("INSERT INTO SHIFT (WORKER_ID, CREATED_DATE, SHIFT_DATE, SHIFT_STATE) VALUES (?,?,?,?)",
                workerId, shift.getCreatedDate(), formattedDate, State.UNPAID.name());
        salaryService.update(new Salary(workerId, shift.getShiftDate()));
    }

    @Override
    public void delete(int id){
        Shift shift = getById(id);
        jdbcTemplate.update("DELETE FROM SHIFT WHERE ID=?", id);
        salaryService.reduce(shift.getWorker().getId(), shift.getShiftDate(), 1);
    }

    @Override
    public void deleteByWorkerId(int workerId){
        jdbcTemplate.update("DELETE FROM SHIFT WHERE WORKER_ID=?", workerId);
    }

    @Override
    public List<Shift> getByWorkerId(int workerId){
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("workerId", workerId);
        return namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, CREATED_DATE, SHIFT_DATE, SHIFT_STATE FROM SHIFT WHERE WORKER_ID =:workerId",
                namedParameters, new ShiftRowMapper());
    }

    private Shift getById(int shiftId){
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("shiftId", shiftId);
        return namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, CREATED_DATE, SHIFT_DATE , null as FIRST_NAME, null as LAST_NAME, SHIFT_STATE FROM SHIFT WHERE ID =:shiftId",
                namedParameters, new ShiftRowMapper()).get(0);
    }

    @Override
    public List<ShiftDTO> getAll(){
        List<Shift> shifts = jdbcTemplate.query(
                "SELECT sh.ID, w.FIRST_NAME, w.LAST_NAME, w.ID as WORKER_ID, sh.CREATED_DATE, sh.SHIFT_DATE, sh.SHIFT_STATE FROM SHIFT sh "
                        + "LEFT JOIN WORKER w on sh.WORKER_ID = w.ID", new ShiftRowMapper());
      return convertToShiftDTOs(shifts);
    }

    @Override
    public List<ShiftDTO> getAllByMonth(Date date) {
        LOGGER.info("Date: {}", date);
        LocalDate localDate = Utils.getLocalDate(date);
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("year", year)
                .addValue("month", month);
        List<Shift> shifts = namedParameterJdbcTemplate.query(
                "SELECT sh.ID, w.FIRST_NAME, w.LAST_NAME, w.ID as WORKER_ID, sh.CREATED_DATE, sh.SHIFT_DATE, sh.SHIFT_STATE "
                        + "FROM SHIFT sh "
                        + "RIGHT JOIN WORKER w on w.ID = sh.WORKER_ID "
                        + "AND YEAR(sh.SHIFT_DATE) = :year "
                        + "AND MONTH(sh.SHIFT_DATE) = :month "
                        + "WHERE (MONTH(w.CREATE_DATE) <= :month "
                        + "AND YEAR(w.CREATE_DATE) <= :year) "
                        + "OR (MONTH(w.CREATE_DATE) > :month "
                        + "AND YEAR(w.CREATE_DATE) < :year)",
                namedParameters, new ShiftRowMapper());
        return convertToShiftDTOs(shifts);
    }

    @Override
    @Transactional
    public void closeShift(Shift shift) {
        int workerId = shift.getWorker().getId();
        Date date = shift.getShiftDate();
        List<Shift> shifts = getByWorkerIdAndDateAndState(workerId, date, State.UNPAID);
        updateState(shifts.stream().map(Shift::getId).collect(Collectors.toList()), State.PAID_OUT);
        salaryService.reduce(workerId, date, shifts.size());
        List<SalaryAdjustment> salaryAdjustments = salaryAdjustmentService.getByWorkerIdAndSateBeforeDate(date, workerId, State.UNPAID);
        if (CollectionUtils.isNotEmpty(salaryAdjustments)) {
            salaryAdjustmentService.updateState(salaryAdjustments.stream().map(SalaryAdjustment::getId).collect(Collectors.toList()), State.PAID_OUT);
            BigDecimal sum = salaryAdjustments.stream().map(SalaryAdjustment::getAdjustment).reduce(BigDecimal.ZERO,
                    BigDecimal::add);
            salaryService.addAdjustment(workerId, date, sum.negate());
        }
    }

    private void updateState(Collection<Integer> shiftIds, State state){
        if (CollectionUtils.isEmpty(shiftIds)) {
            return;
        }
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("state", state.name())
                .addValue("shiftIds", shiftIds);
        namedParameterJdbcTemplate.update(
                "UPDATE SHIFT SET "
                        + "SHIFT_STATE= :state "
                        + "WHERE ID IN (:shiftIds)",
                namedParameters);
    }

    private List<Shift> getByWorkerIdAndDateAndState(int workerId, Date date, State state){
        LOGGER.info("Date: {}", date);
        LocalDate localDate = Utils.getLocalDate(date);
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        int day = localDate.getDayOfMonth();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("year", year)
                .addValue("month", month)
                .addValue("day", day)
                .addValue("state", state.name())
                .addValue("workerId", workerId);
        return namedParameterJdbcTemplate.query(
                "SELECT sh.ID, w.ID as WORKER_ID, w.FIRST_NAME, w.LAST_NAME, sh.CREATED_DATE, sh.SHIFT_DATE, sh.SHIFT_STATE "
                        + "FROM SHIFT sh "
                        + "RIGHT JOIN WORKER w on w.ID = sh.WORKER_ID "
                        + "AND YEAR(sh.SHIFT_DATE) = :year "
                        + "AND MONTH(sh.SHIFT_DATE) = :month "
                        + "AND DAY(sh.SHIFT_DATE) <= :day "
                        + "WHERE sh.WORKER_ID = :workerId "
                        + "AND SHIFT_STATE = :state",
                namedParameters, new ShiftRowMapper());
    }

    private List<ShiftDTO> convertToShiftDTOs(Collection<Shift> shifts){
        List<ShiftDTO> shiftDTOS = new ArrayList<>();
        Map<Integer, List<Shift>> workerToShift = shifts.stream().collect(Collectors.groupingBy(s -> s.getWorker().getId()));
        for (Map.Entry<Integer, List<Shift>> entry: workerToShift.entrySet()) {
            ShiftDTO shiftDTO = new ShiftDTO();
            shiftDTO.setWorker(entry.getValue().get(0).getWorker());
            shiftDTO.setShifts(entry.getValue());
            shiftDTOS.add(shiftDTO);
        }
        return shiftDTOS.stream().sorted().collect(Collectors.toList());
    }

    private Shift getByWorkerIdAndDate(int workerId, Date date){
        LOGGER.info("Date: {}", date);
        LocalDate localDate = Utils.getLocalDate(date);
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        int day = localDate.getDayOfMonth();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("year", year)
                .addValue("month", month)
                .addValue("day", day)
                .addValue("workerId", workerId);
        List<Shift> shifts = namedParameterJdbcTemplate.query(
                "SELECT sh.ID, w.ID as WORKER_ID, w.FIRST_NAME, w.LAST_NAME, sh.CREATED_DATE, sh.SHIFT_DATE, sh.SHIFT_STATE "
                        + "FROM SHIFT sh "
                        + "RIGHT JOIN WORKER w on w.ID = sh.WORKER_ID "
                        + "AND YEAR(sh.SHIFT_DATE) = :year "
                        + "AND MONTH(sh.SHIFT_DATE) = :month "
                        + "AND DAY(sh.SHIFT_DATE) = :day "
                        + "WHERE sh.WORKER_ID = :workerId",
                namedParameters, new ShiftRowMapper());
        return CollectionUtils.isEmpty(shifts) ? null : shifts.get(0);
    }

}
