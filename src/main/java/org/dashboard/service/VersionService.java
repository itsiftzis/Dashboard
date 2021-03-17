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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

        List<CompletableFuture<Build>> futuresList = new ArrayList<>();

        for (Map.Entry<String, String> pair : mapping.entrySet()) {
            CompletableFuture<Build> build = CompletableFuture.supplyAsync(() -> fetchVersion(pair.getValue(), pair.getKey()));
            futuresList.add(build);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0]));
        CompletableFuture<List<Build>> allCompletableFuture = allFutures.thenApply(future -> futuresList.stream().map(CompletableFuture::join)
            .collect(Collectors.toList()));

        CompletableFuture<List<Build>> completableFuture = allCompletableFuture.toCompletableFuture();

        List<Build> sortedList = completableFuture.get();
        sortedList.sort(Comparator.comparing(Build::getName));

        return sortedList.stream().collect(Collectors.toMap(Build::getId, build -> build, (oldV, newV) -> newV, LinkedHashMap::new));

    }

    public Build fetchVersion(String value, String key) {
        Info info;
        try {
            Info infoObject = restTemplate.getForObject(value, Info.class);
            if (infoObject != null) {
                info = convertDate(infoObject, key);
            } else {
                info = Info.emptyInfo();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            info = Info.emptyInfo();
        }

        return info.getBuild();
    }

    private Info convertDate(Info info, String key) {
        Object time = info.getBuild().getTime();
        info.getBuild().setId(key);
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
