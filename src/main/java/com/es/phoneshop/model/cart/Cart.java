package com.es.phoneshop.model.cart;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Cart implements Serializable {
    private List<CartItem> items = new CopyOnWriteArrayList<>();

    private AtomicInteger totalQuantity = new AtomicInteger(0);
    private AtomicReference<BigDecimal> totalCost = new AtomicReference<>(BigDecimal.ZERO);

    public List<CartItem> getItems() {
        return items;
    }

    public AtomicInteger getTotalQuantity() {
        return totalQuantity;
    }

    public AtomicReference<BigDecimal> getTotalCost() {
        return totalCost;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                '}';
    }
}
