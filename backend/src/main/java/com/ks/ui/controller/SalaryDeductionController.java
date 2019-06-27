package com.ks.ui.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks.ui.service.SalaryDeductionService;
import com.ks.ui.service.Utils;
import com.ks.ui.vo.SalaryDeduction;

@RestController
@RequestMapping(path = "/api/salariesDeduction")
public class SalaryDeductionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalaryDeductionController.class);

    private final SalaryDeductionService salaryDeductionService;

    @Autowired
    public SalaryDeductionController(SalaryDeductionService salaryDeductionService) {
        this.salaryDeductionService = salaryDeductionService;
    }

    @PostMapping
    public void create(@RequestBody SalaryDeduction salaryDeduction) {
        salaryDeductionService.create(salaryDeduction);
    }

    @GetMapping
    public List<SalaryDeduction> getById(@RequestParam(value = "worker", required = false) Integer workerId,
            @RequestParam("date") String date) throws ParseException {
        SimpleDateFormat sdf = Utils.getSdf();
        Date convertedDate = sdf.parse(date);
        LOGGER.info("getById : {}", convertedDate);
        if (workerId == null) {
            return salaryDeductionService.getAllByDate(convertedDate);
        }
        return salaryDeductionService.getByWorkerIdAndDate(new SalaryDeduction(workerId, convertedDate));
    }

    @DeleteMapping("workers/{worker_id}")
    public void deleteByWorkerId(@PathVariable("worker_id") int workerId){
        salaryDeductionService.deleteByWorkerId(workerId);
    }

    @PutMapping
    public void update(@RequestBody SalaryDeduction salaryDeduction) {
        salaryDeductionService.update(salaryDeduction);
    }
}
