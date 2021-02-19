package com.es.phoneshop.listener;

import com.es.phoneshop.service.impl.HttpSessionCartService;
import com.es.phoneshop.service.impl.HttpSessionRecentlyViewedProductsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InitializerHttpSessionListenerTest {
    @Mock
    private HttpSessionEvent event;
    @Mock
    private HttpSession session;

    private InitializerHttpSessionListener listener = new InitializerHttpSessionListener();

    @Before
    public void setup() {
        when(event.getSession()).thenReturn(session);
    }

    @Test
    public void testSessionCreated() {
        listener.sessionCreated(event);

        verify(session).setAttribute(eq(HttpSessionCartService.CART_SESSION_ATTRIBUTE), any());
        verify(session).setAttribute(eq(HttpSessionRecentlyViewedProductsService.RECENTLY_VIEWED_SESSION_ATTRIBUTE),
                any());
    }
}
