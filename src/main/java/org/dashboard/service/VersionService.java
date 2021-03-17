package org.dashboard.service;

import org.dashboard.configuration.ConfigProperties;
import org.dashboard.dto.Build;
import org.dashboard.dto.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class VersionService {

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Build> fetchData() throws ExecutionException, InterruptedException {

        Map<String, String> mapping = configProperties.getMapping();
        Map<String, Build> buildInfo = new HashMap<>();

        for (Map.Entry<String, String> pair : mapping.entrySet()) {
            Build build = fetchVersion(pair.getValue()).get();
            buildInfo.put(pair.getKey(), build);
            System.out.printf("added %s ", pair.getKey());
        }

        return buildInfo.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    @Async
    public CompletableFuture<Build> fetchVersion(String value) {
        Info info;
        try {
            info = convertDate(restTemplate.getForObject(value, Info.class));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            info = new Info();
            info.setBuild(new Build());
        }

        return CompletableFuture.completedFuture(info.getBuild());
    }

    private Info convertDate(Info info) {
        Object time = info.getBuild().getTime();
        if (time instanceof String) {
            info.getBuild().setTime(time);
        } else if (time instanceof Double) {
            info.getBuild().setTime(LocalDateTime.ofEpochSecond(((Double) time).longValue(), 0, ZoneOffset.UTC).toString());
        } else if (time instanceof Map) {
            Integer timeEpoch = (Integer) ((Map) time).get("epochSecond");
            info.getBuild().setTime(LocalDateTime.ofEpochSecond(timeEpoch, 0, ZoneOffset.UTC));
        }
        return info;
    }
}
