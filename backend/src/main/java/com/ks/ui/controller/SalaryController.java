package com.ks.ui.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.ui.service.SalaryService;
import com.ks.ui.vo.Salary;

@RestController
@RequestMapping(path = "/api/salaries")
public class SalaryController {

    private final SalaryService salaryService;

    @Autowired
    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @PostMapping
    public void create(@RequestBody Salary salary) {
        salary.setCreatedDate(new Date());
        salaryService.create(salary);
    }

    @GetMapping("workers/{worker_id}")
    public Salary getById(@PathVariable("worker_id") int workerId) {
        return salaryService.getByWorkerId(workerId);
    }

    @DeleteMapping("workers/{worker_id}")
    public void deleteByWorkerId(@PathVariable("worker_id") int workerId){
        salaryService.deleteByWorkerId(workerId);
    }

    @PutMapping
    public void update(@RequestBody Salary salary) {
        salaryService.update(salary);
    }
}
