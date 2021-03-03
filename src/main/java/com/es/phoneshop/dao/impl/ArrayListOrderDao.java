package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.AbstractArrayListDao;
import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.model.order.Order;

public class ArrayListOrderDao extends AbstractArrayListDao<Order> implements OrderDao {
    private static volatile OrderDao instance;

    private ArrayListOrderDao() {
    }

    public static OrderDao getInstance() {
        if (instance == null) {
            synchronized (ArrayListOrderDao.class) {
                if (instance == null) {
                    instance = new ArrayListOrderDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Order getOrderBySecureId(String id) throws NullValuePassedException {
        if (id == null) {
            throw new NullValuePassedException();
        }
        return items.stream()
                .filter(item -> id.equals(item.getSecureId()))
                .findAny()
                .orElseThrow(ItemNotFoundException::new);
    }
}
