package com.es.phoneshop.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataServletContextListenerTest {
    @Mock
    private ServletContextEvent event;
    @Mock
    private ServletContext servletContext;
    @InjectMocks
    private DemoDataServletContextListener listener = new DemoDataServletContextListener();

    @Before
    public void setup() {
        when(event.getServletContext()).thenReturn(servletContext);
        when(servletContext.getInitParameter("insertDemoData")).thenReturn(String.valueOf(true));
    }

    @Test
    public void testDoGet() {
        listener.contextInitialized(event);

        verify(event.getServletContext()).getInitParameter(eq("insertDemoData"));
    }
}
