package com.ks.ui.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import com.ks.ui.rowmapper.ShiftRowMapper;
import com.ks.ui.vo.Salary;
import com.ks.ui.vo.Shift;
import com.ks.ui.vo.ShiftDTO;

@Service
public class ShiftServiceImpl implements ShiftService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SalaryService salaryService;

    @Autowired
    public ShiftServiceImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            SalaryService salaryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.salaryService = salaryService;
    }

    @Override
    public void create(Shift shift){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        int workerId = shift.getWorker().getId();
        String formattedDate = sdf.format(shift.getShiftDate());
        jdbcTemplate.update("INSERT INTO SHIFT (WORKER_ID, CREATED_DATE, SHIFT_DATE) VALUES (?,?,?)",
                workerId, shift.getCreatedDate(), formattedDate);
        salaryService.update(new Salary(workerId, shift.getShiftDate()));
    }

    @Override
    public void delete(int id){
        Shift shift = getById(id);
        jdbcTemplate.update("DELETE FROM SHIFT WHERE ID=?", id);
        salaryService.reduce(new Salary(shift.getWorker().getId(), shift.getShiftDate()));
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

    private Shift getById(int shiftId){
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("shiftId", shiftId);
        return namedParameterJdbcTemplate.query(
                "SELECT ID, WORKER_ID, CREATED_DATE, SHIFT_DATE , null as FIRST_NAME, null as LAST_NAME FROM SHIFT WHERE ID =:shiftId",
                namedParameters, new ShiftRowMapper()).get(0);
    }

    @Override
    public List<ShiftDTO> getAll(){
        List<Shift> shifts = jdbcTemplate.query(
                "SELECT sh.ID, w.FIRST_NAME, w.LAST_NAME, w.ID as WORKER_ID, sh.CREATED_DATE, sh.SHIFT_DATE FROM SHIFT sh "
                        + "LEFT JOIN WORKER w on sh.WORKER_ID = w.ID", new ShiftRowMapper());
      return convertToShiftDTOs(shifts);
    }

    @Override
    public List<ShiftDTO> getAllByMonth(Date date) {
        LOGGER.info("Date: {}", date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month =  cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("year", year)
                .addValue("month", month);
        List<Shift> shifts = namedParameterJdbcTemplate.query(
                "SELECT sh.ID, w.FIRST_NAME, w.LAST_NAME, w.ID as WORKER_ID, sh.CREATED_DATE, sh.SHIFT_DATE "
                        + "FROM SHIFT sh "
                        + "RIGHT JOIN WORKER w on w.ID = sh.WORKER_ID "
                        + "AND YEAR(sh.SHIFT_DATE) = :year "
                        + "AND MONTH(sh.SHIFT_DATE) = :month ",
                namedParameters, new ShiftRowMapper());
        return convertToShiftDTOs(shifts);
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
        return shiftDTOS;
    }

}
