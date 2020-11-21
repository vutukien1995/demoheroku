package com.kien.demoheroku.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class MainController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @GetMapping("/test")
    public ModelAndView test() {
        LOG.info("Test screen.");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Test");
        modelAndView.setViewName("test");
        return modelAndView;
    }

}
