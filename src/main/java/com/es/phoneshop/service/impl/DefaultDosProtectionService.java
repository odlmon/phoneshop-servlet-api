package com.es.phoneshop.service.impl;

import com.es.phoneshop.service.DosProtectionService;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService {
    private static final long THRESHOLD = 20;

    private Map<String, Long> countMap = new ConcurrentHashMap<>();

    private static volatile DosProtectionService instance;

    private DefaultDosProtectionService() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                countMap.clear();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 60000);
    }

    public static DosProtectionService getInstance() {
        if (instance == null) {
            synchronized (DefaultDosProtectionService.class) {
                if (instance == null) {
                    instance = new DefaultDosProtectionService();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean isAllowed(String ip) {
        Long count = countMap.get(ip);
        if (count == null) {
            count = 1L;
        } else {
            count++;
            if (count > THRESHOLD) {
                return false;
            }
        }
        countMap.put(ip, count);
        return true;
    }
}
