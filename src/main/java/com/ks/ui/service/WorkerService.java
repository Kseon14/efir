package com.ks.ui.service;

import com.ks.ui.vo.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public interface WorkerService {

    void create(Worker worker);

    void delete(int pk);

    Worker getById(int id);

    List<Worker> getAll();

    void update(Worker worker);

}
