package com.es.phoneshop.service;

import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.model.product.RecentlyViewedProducts;

import javax.servlet.http.HttpServletRequest;

public interface RecentlyViewedProductsService {
    RecentlyViewedProducts getRecentlyViewedProducts(HttpServletRequest request);
    void add(RecentlyViewedProducts products, Long productId) throws NullValuePassedException;
}
