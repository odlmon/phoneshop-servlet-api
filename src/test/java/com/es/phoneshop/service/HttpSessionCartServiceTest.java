package com.es.phoneshop.service;

import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.impl.HttpSessionCartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class HttpSessionCartServiceTest {
    private HttpSessionCartService cartService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private ArrayListProductDao productDao;

    private void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        Field instance = HttpSessionCartService.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException, NullValuePassedException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(new Cart());
        resetSingleton();
        cartService = (HttpSessionCartService) HttpSessionCartService.getInstance();
        cartService.setProductDao(productDao);

        Long id = 1L;
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(id, "test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        when(productDao.getItem(id)).thenReturn(product);
        when(productDao.getItem(null)).thenThrow(NullValuePassedException.class);
    }

    @Test
    public void testGetInstance() {
        CartService cartService1 = HttpSessionCartService.getInstance();
        CartService cartService2 = HttpSessionCartService.getInstance();
        assertSame(cartService1, cartService2);
    }

    @Test
    public void testGetCart() {
        Cart cart = cartService.getCart(request);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void testAdd() throws OutOfStockException, NullValuePassedException {
        Cart cart = cartService.getCart(request);
        assertEquals(0, cart.getItems().size());
        cartService.add(cart, 1L, 1);
        assertEquals(1, cart.getItems().size());
        cartService.add(cart, 1L, 1);
        assertEquals(1, cart.getItems().size());
    }

    @Test(expected = OutOfStockException.class)
    public void testAddNonPresentProductWithOutOfStock() throws OutOfStockException, NullValuePassedException {
        Cart cart = cartService.getCart(request);
        cartService.add(cart, 1L, 101);
    }

    @Test(expected = OutOfStockException.class)
    public void testAddPresentProductWithOutOfStock() throws OutOfStockException, NullValuePassedException {
        Cart cart = cartService.getCart(request);
        cartService.add(cart, 1L, 1);
        cartService.add(cart, 1L, 100);
    }

    @Test(expected = NullValuePassedException.class)
    public void testAddWithNullValueIdPassed() throws OutOfStockException, NullValuePassedException {
        Cart cart = cartService.getCart(request);
        cartService.add(cart, null, 1);
    }

    @Test
    public void testUpdate() throws NullValuePassedException, OutOfStockException {
        Cart cart = cartService.getCart(request);
        cartService.update(cart, 1L, 1);
        assertEquals(1, cart.getItems().get(0).getQuantity());
        cartService.update(cart, 1L, 4);
        assertEquals(4, cart.getItems().get(0).getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void testUpdateNonPresentProductWithOutOfStock() throws OutOfStockException, NullValuePassedException {
        Cart cart = cartService.getCart(request);
        cartService.update(cart, 1L, 101);
    }

    @Test(expected = OutOfStockException.class)
    public void testUpdatePresentProductWithOutOfStock() throws OutOfStockException, NullValuePassedException {
        Cart cart = cartService.getCart(request);
        cartService.update(cart, 1L, 1);
        cartService.update(cart, 1L, 101);
    }

    @Test(expected = NullValuePassedException.class)
    public void testUpdateWithNullValueIdPassed() throws OutOfStockException, NullValuePassedException {
        Cart cart = cartService.getCart(request);
        cartService.update(cart, null, 1);
    }

    @Test
    public void testDelete() throws NullValuePassedException, OutOfStockException {
        Cart cart = cartService.getCart(request);
        cartService.update(cart, 1L, 1);
        assertEquals(1, cart.getItems().get(0).getQuantity());
        cartService.delete(cart, 1L);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void testDeleteWithNullValueIdPassed() throws NullValuePassedException {
        Cart cart = cartService.getCart(request);
        cartService.delete(cart, null);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void testDeleteNonPresentProduct() throws NullValuePassedException {
        Cart cart = cartService.getCart(request);
        cartService.delete(cart, 2L);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void testClearCart() {
        cartService.clearCart(request);
        verify(session).setAttribute(eq(HttpSessionCartService.CART_SESSION_ATTRIBUTE), any(Cart.class));
    }
}
