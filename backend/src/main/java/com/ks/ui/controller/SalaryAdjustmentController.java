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

import com.ks.ui.service.SalaryAdjustmentService;
import com.ks.ui.service.Utils;
import com.ks.ui.vo.SalaryAdjustment;

@RestController
@RequestMapping(path = "/api/salaries_adjustment")
public class SalaryAdjustmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalaryAdjustmentController.class);

    private final SalaryAdjustmentService salaryAdjustmentService;

    @Autowired
    public SalaryAdjustmentController(SalaryAdjustmentService salaryAdjustmentService) {
        this.salaryAdjustmentService = salaryAdjustmentService;
    }

    @PostMapping
    public void create(@RequestBody SalaryAdjustment salaryAdjustment) {
        salaryAdjustmentService.create(salaryAdjustment);
    }

    @GetMapping
    public List<SalaryAdjustment> getById(@RequestParam(value = "worker", required = false) Integer workerId,
            @RequestParam("date") String date) throws ParseException {
        SimpleDateFormat sdf = Utils.getSdf();
        Date convertedDate = sdf.parse(date);
        LOGGER.info("getById : {}", convertedDate);
        if (workerId == null) {
            return salaryAdjustmentService.getAllByDate(convertedDate);
        }
        return salaryAdjustmentService.getByWorkerIdAndDate(new SalaryAdjustment(workerId, convertedDate));
    }

    @DeleteMapping("workers/{worker_id}")
    public void deleteByWorkerId(@PathVariable("worker_id") int workerId){
        salaryAdjustmentService.deleteByWorkerId(workerId);
    }

    @DeleteMapping("{adjustment_id}")
    public void deleteById(@PathVariable("adjustment_id") int adjustmentId){
        salaryAdjustmentService.deleteById(adjustmentId);
    }

    @PutMapping
    public void update(@RequestBody SalaryAdjustment salaryAdjustment) {
        salaryAdjustmentService.update(salaryAdjustment);
    }
}
