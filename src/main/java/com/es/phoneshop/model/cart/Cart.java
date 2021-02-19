package com.es.phoneshop.model.cart;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cart {
    private List<CartItem> items = new CopyOnWriteArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                '}';
    }
}
