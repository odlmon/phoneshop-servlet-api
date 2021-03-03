package com.es.phoneshop.dao;

import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.model.order.Order;

public interface OrderDao {
    Order getItem(Long id) throws ItemNotFoundException, NullValuePassedException;
    void save(Order Order) throws NullValuePassedException;
    Order getOrderBySecureId(String id) throws NullValuePassedException;
}
