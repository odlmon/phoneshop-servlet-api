package com.es.phoneshop.model.product;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RecentlyViewedProducts {
    private Queue<Product> items;

    public RecentlyViewedProducts() {
        this.items = new ConcurrentLinkedQueue<>();
    }

    public Queue<Product> getItems() {
        return items;
    }
}
