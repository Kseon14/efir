package com.ks.efir.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks.efir.service.SalaryService;
import com.ks.efir.service.Utils;
import com.ks.efir.vo.Salary;

@RestController
@RequestMapping(path = "/api/salaries")
public class SalaryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalaryController.class);

    private final SalaryService salaryService;

    @Autowired
    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping
    public List<Salary> getById(@RequestParam(value = "worker", required = false) Integer workerId,
            @RequestParam("date") String date) throws ParseException {
        SimpleDateFormat sdf = Utils.getSdf();
        Date convertedDate = sdf.parse(date);
        LOGGER.info("getById : {}", convertedDate);
        if (workerId == null) {
            return salaryService.getAllByDate(convertedDate);
        }
        return salaryService.getByWorkerIdAndDate(workerId, convertedDate);
    }

}
