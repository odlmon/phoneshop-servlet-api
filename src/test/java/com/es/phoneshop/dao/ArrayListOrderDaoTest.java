package com.es.phoneshop.dao;

import com.es.phoneshop.dao.impl.ArrayListOrderDao;
import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.model.order.Order;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertSame;

public class ArrayListOrderDaoTest {
    ArrayListOrderDao orderDao;

    private void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        Field instance = ArrayListOrderDao.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        Order order = new Order();
        order.setSecureId("a");
        resetSingleton();
        orderDao = (ArrayListOrderDao) ArrayListOrderDao.getInstance();
        try {
            orderDao.save(order);
        } catch (NullValuePassedException e) {
            System.out.println("Null value passed");
        }
    }

    @Test
    public void testGetInstance() {
        OrderDao orderDao1 = ArrayListOrderDao.getInstance();
        OrderDao orderDao2 = ArrayListOrderDao.getInstance();
        assertSame(orderDao1, orderDao2);
    }

    @Test
    public void testGetOrderBySecureId() throws NullValuePassedException {
        Order order = orderDao.getOrderBySecureId("a");
        assertSame(1L, order.getId());
    }

    @Test(expected = NullValuePassedException.class)
    public void testGetOrderBySecureIdWithNullValuePassed() throws NullValuePassedException {
        orderDao.getOrderBySecureId(null);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testGetOrderBySecureIdWhichIsMissing() throws NullValuePassedException {
        orderDao.getOrderBySecureId("b");
    }
}
