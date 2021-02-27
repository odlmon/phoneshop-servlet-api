package com.es.phoneshop.service;

import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.enums.PaymentMethod;
import com.es.phoneshop.model.order.Order;

import java.util.List;

public interface OrderService {
    Order getOrder(Cart cart);
    List<PaymentMethod> getPaymentMethods();
    void placeOrder(Order order) throws NullValuePassedException;
}
