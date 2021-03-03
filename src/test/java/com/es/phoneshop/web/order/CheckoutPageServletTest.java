package com.es.phoneshop.web.order;

import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.service.impl.DefaultOrderService;
import com.es.phoneshop.service.impl.HttpSessionCartService;
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
import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private HttpSessionCartService cartService;
    @Mock
    private DefaultOrderService orderService;
    @InjectMocks
    private CheckoutPageServlet servlet = new CheckoutPageServlet();

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        Cart cart = new Cart();
        when(cartService.getCart(request)).thenReturn(cart);

        Order order = new Order();
        when(orderService.getOrder(cart)).thenReturn(order);

        when(orderService.getPaymentMethods()).thenReturn(new ArrayList<>());
    }

    private void setupValidForm() {
        when(request.getParameter("firstName")).thenReturn("a");
        when(request.getParameter("lastName")).thenReturn("b");
        when(request.getParameter("phone")).thenReturn("c");
        when(request.getParameter("deliveryDate")).thenReturn(LocalDate.now().toString());
        when(request.getParameter("deliveryAddress")).thenReturn("d");
        when(request.getParameter("paymentMethod")).thenReturn("CACHE");
    }

    private void setupInvalidForm() {
        when(request.getParameter("firstName")).thenReturn("");
        when(request.getParameter("lastName")).thenReturn("");
        when(request.getParameter("phone")).thenReturn("");
        when(request.getParameter("deliveryDate")).thenReturn("");
        when(request.getParameter("deliveryAddress")).thenReturn("");
        when(request.getParameter("paymentMethod")).thenReturn("");
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(cartService).getCart(request);
        verify(request).setAttribute("order", orderService.getOrder(cartService.getCart(request)));
        verify(request).setAttribute("paymentMethods", orderService.getPaymentMethods());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostWithValidForm() throws ServletException, IOException, NullValuePassedException {
        setupValidForm();

        servlet.doPost(request, response);

        verify(cartService).getCart(request);
        verify(orderService).getOrder(cartService.getCart(request));

        verifyGetFormParameters();

        verify(cartService).clearCart(request);
        verify(orderService).placeOrder(orderService.getOrder(cartService.getCart(request)));
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoPostWithInvalidForm() throws ServletException, IOException {
        setupInvalidForm();

        servlet.doPost(request, response);

        verify(cartService, times(2)).getCart(request);
        verify(orderService, times(2)).getOrder(cartService.getCart(request));

        verifyGetFormParameters();
    }

    private void verifyGetFormParameters() {
        verify(request).getParameter("firstName");
        verify(request).getParameter("lastName");
        verify(request).getParameter("phone");
        verify(request).getParameter("deliveryDate");
        verify(request).getParameter("deliveryAddress");
        verify(request).getParameter("paymentMethod");
    }
}
