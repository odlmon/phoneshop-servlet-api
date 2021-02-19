package com.es.phoneshop.web.cart;

import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.HttpSessionCartService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartPageServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = HttpSessionCartService.getInstance();
    }

    private Long getProductIdByIndex(String[] productIds, int index) {
        return Long.valueOf(productIds[index]);
    }

    private int getQuantityByIndex(String[] quantities, int index, Locale locale) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.parse(quantities[index]).intValue();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("cart", cartService.getCart(request));
        request.getRequestDispatcher("/WEB-INF/pages/cart/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");

        Map<Long, String> errors = new HashMap<>();
        for (int i = 0; i < productIds.length; i++) {
            Long productId = getProductIdByIndex(productIds, i);
            try {
                int quantity = getQuantityByIndex(quantities, i, request.getLocale());
                if (quantity < 1) {
                    errors.put(productId, "Quantity can't be negative");
                    continue;
                }
                cartService.update(cartService.getCart(request), productId, quantity);
            } catch (ParseException e) {
                errors.put(productId, "Not a number");
            } catch (OutOfStockException e) {
                errors.put(productId, "Out of stock, max available " + e.getStockAvailable());
            } catch (NullValuePassedException e) {
                throw new RuntimeException("Null value passed", e);
            }
        }

        sendPage(request, response, errors);
    }

    private void sendPage(HttpServletRequest request, HttpServletResponse response, Map<Long, String> errors)
            throws IOException, ServletException {
        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
