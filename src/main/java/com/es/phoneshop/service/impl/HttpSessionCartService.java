package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class HttpSessionCartService implements CartService {
    private static volatile CartService instance;

    public static final String CART_SESSION_ATTRIBUTE = HttpSessionCartService.class.getName() + ".cart";
    private ProductDao productDao;

    private HttpSessionCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public static CartService getInstance() {
        if (instance == null) {
            synchronized (HttpSessionCartService.class) {
                if (instance == null) {
                    instance = new HttpSessionCartService();
                }
            }
        }
        return instance;
    }

    @Override
    public Cart getCart(HttpServletRequest request) {
        return (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException, NullValuePassedException {
        if (cart == null) {
            throw new NullValuePassedException();
        }
        Product product = productDao.getProduct(productId);
        Optional<CartItem> optionalCartItem = cart.getItems().stream()
                .filter(cartItem -> productId.equals(cartItem.getProduct().getId()))
                .findFirst();
        if (optionalCartItem.isPresent()) {
            update(product, quantity, optionalCartItem.get());
        } else {
            addNew(cart, product, quantity);
        }
    }

    private void update(Product product, int quantity, CartItem additionalItem) throws OutOfStockException {
        int totalQuantity = quantity + additionalItem.getQuantity();
        checkQuantity(product, totalQuantity);
        additionalItem.setQuantity(totalQuantity);
    }

    private void addNew(Cart cart, Product product, int quantity) throws OutOfStockException {
        checkQuantity(product, quantity);
        cart.getItems().add(new CartItem(product, quantity));
    }

    private void checkQuantity(Product product, int requested) throws OutOfStockException {
        if (product.getStock() < requested) {
            throw new OutOfStockException(product, requested, product.getStock());
        }
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }
}
