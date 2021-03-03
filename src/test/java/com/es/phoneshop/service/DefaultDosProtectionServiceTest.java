package com.es.phoneshop.service;

import com.es.phoneshop.service.impl.DefaultDosProtectionService;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class DefaultDosProtectionServiceTest {
    private DefaultDosProtectionService dosProtectionService;

    private void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        Field instance = DefaultDosProtectionService.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        resetSingleton();

        dosProtectionService = (DefaultDosProtectionService) DefaultDosProtectionService.getInstance();
    }

    @Test
    public void testGetInstance() {
        DefaultDosProtectionService dosProtectionService1 = (DefaultDosProtectionService) DefaultDosProtectionService.getInstance();
        DefaultDosProtectionService dosProtectionService2 = (DefaultDosProtectionService) DefaultDosProtectionService.getInstance();
        assertSame(dosProtectionService1, dosProtectionService2);
    }

    @Test
    public void testIsAllowedUnderThreshold() {
        assertTrue(dosProtectionService.isAllowed("127.0.0.1"));
    }

    @Test
    public void testIsAllowedOverThreshold() {
        for (int i = 0; i < 20; i++) {
            assertTrue(dosProtectionService.isAllowed("127.0.0.1"));
        }
        assertFalse(dosProtectionService.isAllowed("127.0.0.1"));
    }

    @Test
    public void testIsAllowedAfterPeriod() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            assertTrue(dosProtectionService.isAllowed("127.0.0.1"));
        }
        assertFalse(dosProtectionService.isAllowed("127.0.0.1"));
        Thread.sleep(60000);
        assertTrue(dosProtectionService.isAllowed("127.0.0.1"));
    }
}
