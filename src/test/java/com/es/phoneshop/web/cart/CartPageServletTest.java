package com.es.phoneshop.web.cart;

import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
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
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private HttpSessionCartService cartService;
    @InjectMocks
    private CartPageServlet servlet = new CartPageServlet();

    @Before
    public void setup() {
        when(cartService.getCart(request)).thenReturn(new Cart());
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        when(request.getLocale()).thenReturn(Locale.getDefault());
        Currency usd = Currency.getInstance("USD");
        Product product1 = new Product(1L, "test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        Product product2 = new Product(2L, "test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        when(request.getParameterValues("productId")).thenReturn(new String[]{"1", "2"});
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"1", "2"});
        Cart cart = new Cart();
        cart.getItems().add(new CartItem(product1, 1));
        cart.getItems().add(new CartItem(product2, 2));
        when(cartService.getCart(request)).thenReturn(cart);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        Cart cart = cartService.getCart(request);
        verify(request).setAttribute("cart", cart);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost() throws ServletException, IOException, NullValuePassedException, OutOfStockException {
        servlet.doPost(request, response);

        verify(request).getParameterValues("productId");
        verify(request).getParameterValues("quantity");
        Cart cart = cartService.getCart(request);
        verify(cartService).update(cart, 1L, 1);
        verify(cartService).update(cart, 2L, 2);
    }
}
