package com.es.phoneshop.web.cart;

import com.es.phoneshop.model.cart.Cart;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MiniCartServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private HttpSessionCartService cartService;
    @InjectMocks
    private MiniCartServlet servlet = new MiniCartServlet();

    @Before
    public void setup() {
        when(cartService.getCart(request)).thenReturn(new Cart());
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    private void verifyIncludeCart() throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        verify(request).setAttribute("cart", cart);
        verify(requestDispatcher).include(request, response);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verifyIncludeCart();
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        servlet.doPost(request, response);

        verifyIncludeCart();
    }
}
