package com.es.phoneshop.web.cart;

import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.HttpSessionCartService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MiniCartServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = HttpSessionCartService.getInstance();
    }

    private void includeMiniCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("cart", cartService.getCart(request));
        request.getRequestDispatcher("/WEB-INF/pages/cart/minicart.jsp").include(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        includeMiniCart(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        includeMiniCart(request, response);
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
