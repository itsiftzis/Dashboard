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
        Map<String, Build> modelMap = versionService.fetchData();
        return new ModelAndView("dashboard", Map.of("hosts", modelMap));
    }
}
