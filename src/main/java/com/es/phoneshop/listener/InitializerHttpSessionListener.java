package com.es.phoneshop.listener;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.RecentlyViewedProducts;
import com.es.phoneshop.service.impl.HttpSessionCartService;
import com.es.phoneshop.service.impl.HttpSessionRecentlyViewedProductsService;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class InitializerHttpSessionListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        httpSessionEvent.getSession().setAttribute(HttpSessionCartService.CART_SESSION_ATTRIBUTE, new Cart());
        httpSessionEvent.getSession().setAttribute(
                HttpSessionRecentlyViewedProductsService.RECENTLY_VIEWED_SESSION_ATTRIBUTE,
                new RecentlyViewedProducts());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

    }
}
