package com.es.phoneshop.service;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.enums.PaymentMethod;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.impl.DefaultOrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderServiceTest {
    private DefaultOrderService orderService;
    @Mock
    private OrderDao orderDao;
    @Mock
    private Cart cart;
    @Mock
    private Order order;

    private void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        Field instance = DefaultOrderService.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException, NullValuePassedException {
        resetSingleton();

        orderService = (DefaultOrderService) DefaultOrderService.getInstance();
        orderService.setOrderDao(orderDao);

        Long id = 1L;
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(id, "test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        List<CartItem> items = new CopyOnWriteArrayList<>();
        items.add(new CartItem(product, 1));
        when(cart.getItems()).thenReturn(items);
        when(cart.getTotalCost()).thenReturn(new BigDecimal(100));
        when(cart.getTotalQuantity()).thenReturn(1);
    }

    @Test
    public void testGetOrder() {
        Order order = orderService.getOrder(cart);
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal(105), order.getTotalCost());
    }

    @Test
    public void testGetPaymentMethods() {
        List<PaymentMethod> paymentMethods = orderService.getPaymentMethods();
        assertTrue(paymentMethods.size() > 0);
    }

    @Test
    public void testPlaceOrder() throws NullValuePassedException {
        orderService.placeOrder(order);

        verify(order).setSecureId(anyString());
        verify(orderDao).save(order);
    }
}
