package com.es.phoneshop.web.cart;

import com.es.phoneshop.exception.NullValuePassedException;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCartItemServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSessionCartService cartService;
    @InjectMocks
    private DeleteCartItemServlet servlet = new DeleteCartItemServlet();

    @Before
    public void setup() {
        Long id = 1L;
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(id, "test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        when(request.getPathInfo()).thenReturn("/" + id.toString());
        Cart cart = new Cart();
        cart.getItems().add(new CartItem(product, 1));
        when(cartService.getCart(request)).thenReturn(cart);
    }

    @Test
    public void testDoPost() throws IOException, NullValuePassedException {
        servlet.doPost(request, response);

        Cart cart = cartService.getCart(request);
        verify(cartService).delete(cart, 1L);
        verify(response).sendRedirect(anyString());
    }
}
