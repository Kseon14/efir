package com.ks.ui.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.ui.service.ShiftService;
import com.ks.ui.vo.Shift;
import com.ks.ui.vo.ShiftDTO;

@RestController
@RequestMapping(path = "/api/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    public void create(@RequestBody Shift shift) {
        shift.setCreatedDate(new Date());
        shiftService.create(shift);
    }

    @GetMapping("workers/{worker_id}")
    public List<Shift> getByWorkerId(@PathVariable("worker_id") int workerId) {
        return shiftService.getByWorkerId(workerId);
    }

    @GetMapping
    public List<ShiftDTO> getAll() {
        return shiftService.getAll();
    }

    @GetMapping("{date}")
    public List<ShiftDTO> getAllByMonth(@PathVariable("date") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)  Date date) {
        return shiftService.getAllByMonth(date);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable("id") int id){
        shiftService.delete(id);
    }

    @DeleteMapping("workers/{worker_id}")
    public void deleteByWorkerId(@PathVariable("worker_id") int workerId) {
        shiftService.deleteByWorkerId(workerId);
    }
}
