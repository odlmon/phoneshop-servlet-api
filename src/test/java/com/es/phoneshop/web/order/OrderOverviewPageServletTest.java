package com.es.phoneshop.web.order;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.model.order.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private OrderDao orderDao;
    @InjectMocks
    private OrderOverviewPageServlet servlet = new OrderOverviewPageServlet();

    @Before
    public void setup() throws NullValuePassedException {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        when(request.getPathInfo()).thenReturn("/1");
        when(orderDao.getOrderBySecureId("1")).thenReturn(new Order());
    }

    @Test
    public void testDoGet() throws ServletException, IOException, NullValuePassedException {
        servlet.doGet(request, response);

        verify(request).setAttribute("order", orderDao.getOrderBySecureId("1"));
        verify(requestDispatcher).forward(request, response);
    }
}
