package com.es.phoneshop.web.filter;

import com.es.phoneshop.service.impl.DefaultDosProtectionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DosFilterTest {
    @Mock
    private DefaultDosProtectionService dosProtectionService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @InjectMocks
    private DosFilter dosFilter = new DosFilter();

    private static final String SAMPLE_IP = "127.0.0.1";

    @Before
    public void setup() {
        when(request.getRemoteAddr()).thenReturn(SAMPLE_IP);
    }

    private void setupForAllowedIp() {
        when(dosProtectionService.isAllowed(SAMPLE_IP)).thenReturn(true);
    }

    private void setupForNotAllowedIp() {
        when(dosProtectionService.isAllowed(SAMPLE_IP)).thenReturn(false);
    }

    @Test
    public void testDoFilterWithAllowedIp() throws IOException, ServletException {
        setupForAllowedIp();
        dosFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterWithNotAllowedIp() throws IOException, ServletException {
        setupForNotAllowedIp();
        dosFilter.doFilter(request, response, filterChain);
        verify(response).setStatus(eq(429));
    }
}
