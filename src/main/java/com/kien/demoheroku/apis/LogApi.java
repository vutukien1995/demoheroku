package com.kien.demoheroku.apis;

import com.kien.demoheroku.entities.Contribute;
import com.kien.demoheroku.entities.Log;
import com.kien.demoheroku.repositories.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/log")
public class LogApi {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final LogRepository logRepository;

    public LogApi(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @GetMapping("/getAll")
    public List<Log> getAll() {
        LOG.info("Log get all.");
        return logRepository.findAll();
    }
}
