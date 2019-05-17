package com.ks.ui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.ui.service.WorkerService;
import com.ks.ui.vo.Worker;

@RestController
@RequestMapping(path = "/api/workers")
public class WorkerController {

    private final WorkerService workerService;

    @Autowired
    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PostMapping
    public void createWorker(@RequestBody Worker worker){
        workerService.create(worker);
    }

    @GetMapping("{id}")
    public Worker getById(@PathVariable("id") int id) {
        return workerService.getById(id);
    }

    @GetMapping
    public List<Worker> getAll() {
        return workerService.getAll();
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable("id") int id){
        workerService.delete(id);
    }

    @PutMapping
    public void update(@RequestBody Worker worker){
        workerService.update(worker);
    }
}
