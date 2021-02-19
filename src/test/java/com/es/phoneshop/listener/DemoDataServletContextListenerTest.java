package com.es.phoneshop.listener;

import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.exception.NullValuePassedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataServletContextListenerTest {
    @Mock
    private ServletContextEvent event;
    @Mock
    private ServletContext servletContext;
    @Mock
    private ArrayListProductDao productDao;
    @InjectMocks
    private DemoDataServletContextListener listener = new DemoDataServletContextListener();

    @Before
    public void setup() {
        when(event.getServletContext()).thenReturn(servletContext);
    }

    @Test
    public void testContextInitializedWithTrueInitParam() throws NullValuePassedException {
        when(servletContext.getInitParameter("insertDemoData")).thenReturn(String.valueOf(true));

        listener.contextInitialized(event);

        verify(event.getServletContext()).getInitParameter(eq("insertDemoData"));
        verify(productDao, atLeast(1)).save(any());
    }

    @Test
    public void testContextInitializedWithFalseInitParam() throws NullValuePassedException {
        when(servletContext.getInitParameter("insertDemoData")).thenReturn(String.valueOf(false));

        listener.contextInitialized(event);

        verify(event.getServletContext()).getInitParameter(eq("insertDemoData"));
        verify(productDao, times(0)).save(any());
    }
}
