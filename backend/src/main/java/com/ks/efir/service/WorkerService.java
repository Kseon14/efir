package com.ks.efir.service;

import java.util.List;

import com.ks.efir.vo.Worker;

public interface WorkerService {

    void create(Worker worker);

    void delete(int pk);

    Worker getById(int id);

    List<Worker> getAll();

    void update(Worker worker);

}
