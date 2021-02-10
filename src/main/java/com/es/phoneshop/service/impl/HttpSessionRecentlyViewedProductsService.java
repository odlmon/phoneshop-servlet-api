package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.model.product.RecentlyViewedProducts;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.RecentlyViewedProductsService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class HttpSessionRecentlyViewedProductsService implements RecentlyViewedProductsService {
    private static volatile RecentlyViewedProductsService instance;
    public static final String RECENTLY_VIEWED_SESSION_ATTRIBUTE =
            HttpSessionRecentlyViewedProductsService.class.getName() + ".cart";
    public static final int RECENTLY_VIEWED_PRODUCTS_AMOUNT = 3;
    private ProductDao productDao;

    private HttpSessionRecentlyViewedProductsService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public static RecentlyViewedProductsService getInstance() {
        if (instance == null) {
            synchronized (HttpSessionRecentlyViewedProductsService.class) {
                if (instance == null) {
                    instance = new HttpSessionRecentlyViewedProductsService();
                }
            }
        }
        return instance;
    }

    @Override
    public RecentlyViewedProducts getRecentlyViewedProducts(HttpServletRequest request) {
        return (RecentlyViewedProducts) request.getSession().getAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE);
    }

    @Override
    public synchronized void add(RecentlyViewedProducts products, Long productId) throws NullValuePassedException {
        Optional<Product> optionalProduct = products.getItems().stream()
                .filter(product -> productId.equals(product.getId()))
                .findFirst();
        if (!optionalProduct.isPresent()) {
            Product product = productDao.getProduct(productId);
            if (products.getItems().size() == RECENTLY_VIEWED_PRODUCTS_AMOUNT) {
                products.getItems().poll();
            }
            products.getItems().offer(product);
        }
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }
}
