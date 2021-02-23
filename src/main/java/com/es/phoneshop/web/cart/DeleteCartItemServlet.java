package com.es.phoneshop.web.cart;

import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.HttpSessionCartService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteCartItemServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = HttpSessionCartService.getInstance();
    }

    private Long getProductId(HttpServletRequest request) {
        return Long.valueOf(request.getPathInfo().substring(1));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long productId = getProductId(request);
            cartService.delete(cartService.getCart(request), productId);
        } catch (NullValuePassedException e) {
            throw new RuntimeException("Null value passed", e);
        }

        response.sendRedirect(request.getContextPath() + "/cart?message=Cart item removed successfully");
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}