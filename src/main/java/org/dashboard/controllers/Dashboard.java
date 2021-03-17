package org.dashboard.controllers;

import org.dashboard.dto.Build;
import org.dashboard.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class Dashboard {

    @Autowired
    private VersionService versionService;

    @GetMapping(path = "dashboard")
    public ModelAndView dashboard() throws ExecutionException, InterruptedException {
        final long startTimer = System.currentTimeMillis();
        Map<String, Build> modelMap = versionService.fetchData();
        final long endTimer = System.currentTimeMillis();
        System.out.printf("fetch lasted %s milli seconds%n", endTimer - startTimer);
        return new ModelAndView("dashboard", Map.of("hosts", modelMap));
    }
}
