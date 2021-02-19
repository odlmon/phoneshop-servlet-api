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
import java.math.BigDecimal;
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
        Product product = productDao.getProduct(productId);
        Optional<CartItem> optionalCartItem = findCartItem(cart, productId);
        if (optionalCartItem.isPresent()) {
            addToPresent(product, quantity, optionalCartItem.get());
        } else {
            addNew(cart, product, quantity);
        }
        recalculateCart(cart);
    }

    @Override
    public void update(Cart cart, Long productId, int quantity) throws OutOfStockException, NullValuePassedException {
        Product product = productDao.getProduct(productId);
        Optional<CartItem> optionalCartItem = findCartItem(cart, productId);
        if (optionalCartItem.isPresent()) {
            updatePresent(product, quantity, optionalCartItem.get());
        } else {
            addNew(cart, product, quantity);
        }
        recalculateCart(cart);
    }

    @Override
    public void delete(Cart cart, Long productId) throws NullValuePassedException {
        if (cart == null) {
            throw new NullValuePassedException();
        }
        cart.getItems().removeIf(item -> productId.equals(item.getProduct().getId()));
        recalculateCart(cart);
    }

    private void recalculateCart(Cart cart) {
        while (!(recalculateTotalQuantity(cart) && recalculateTotalCost(cart))) ;
    }

    private boolean recalculateTotalQuantity(Cart cart) {
        int currentTotalQuantity = cart.getTotalQuantity().get();
        int newTotalQuantity = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        return cart.getTotalQuantity().compareAndSet(currentTotalQuantity, newTotalQuantity);
    }

    private boolean recalculateTotalCost(Cart cart) {
        BigDecimal currentTotalCost = cart.getTotalCost().get();
        BigDecimal newTotalCost = cart.getItems().stream()
                .map(this::getCartItemCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return cart.getTotalCost().compareAndSet(currentTotalCost, newTotalCost);
    }

    private BigDecimal getCartItemCost(CartItem cartItem) {
        BigDecimal price = cartItem.getProduct().getPrice();
        BigDecimal quantity = new BigDecimal(cartItem.getQuantity());
        return price.multiply(quantity);
    }

    private Optional<CartItem> findCartItem(Cart cart, Long productId) throws NullValuePassedException {
        if (cart == null) {
            throw new NullValuePassedException();
        }
        return cart.getItems().stream()
                .filter(cartItem -> productId.equals(cartItem.getProduct().getId()))
                .findFirst();
    }

    private void addToPresent(Product product, int quantity, CartItem additionalItem) throws OutOfStockException {
        int totalQuantity = quantity + additionalItem.getQuantity();
        checkQuantity(product, totalQuantity);
        additionalItem.setQuantity(totalQuantity);
    }

    private void addNew(Cart cart, Product product, int quantity) throws OutOfStockException {
        checkQuantity(product, quantity);
        cart.getItems().add(new CartItem(product, quantity));
    }

    private void updatePresent(Product product, int quantity, CartItem updatingItem) throws OutOfStockException {
        checkQuantity(product, quantity);
        updatingItem.setQuantity(quantity);
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
