package com.es.phoneshop.web.order;

import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.enums.PaymentMethod;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.service.impl.DefaultOrderService;
import com.es.phoneshop.service.impl.HttpSessionCartService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CheckoutPageServlet extends HttpServlet {
    private CartService cartService;
    private OrderService orderService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = HttpSessionCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        request.setAttribute("order", orderService.getOrder(cart));
        request.setAttribute("paymentMethods", orderService.getPaymentMethods());
        request.getRequestDispatcher("/WEB-INF/pages/order/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        Order order = orderService.getOrder(cart);

        Map<String, String> errors = new HashMap<>();

        setRequiredParameter(request, "firstName", errors, order::setFirstName);
        setRequiredParameter(request, "lastName", errors, order::setLastName);
        setRequiredParameter(request, "phone", errors, order::setPhone);
        setDeliveryDate(request, errors, order);
        setRequiredParameter(request, "deliveryAddress", errors, order::setDeliveryAddress);
        setPaymentMethod(request, errors, order);

        try {
            sendPage(request, response, errors, order);
        } catch (NullValuePassedException e) {
            throw new RuntimeException("Null value passed", e);
        }
    }

    private void sendPage(HttpServletRequest request, HttpServletResponse response, Map<String, String> errors, Order order)
            throws IOException, ServletException, NullValuePassedException {
        if (errors.isEmpty()) {
            cartService.clearCart(request);
            orderService.placeOrder(order);
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getSecureId());
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }

    private void setRequiredParameter(HttpServletRequest request, String parameter, Map<String, String> errors,
                                      Consumer<String> consumer) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, "Value is required");
        } else {
            consumer.accept(value);
        }
    }

    private void setDeliveryDate(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter("deliveryDate");
        if (value == null || value.isEmpty()) {
            errors.put("deliveryDate", "Value is required");
        } else if (notIsValidDate(value)) {
            errors.put("deliveryDate", "Passed day was provided");
        } else {
            order.setDeliveryDate(LocalDate.parse(value));
        }
    }

    private boolean notIsValidDate(String value) {
        try {
            LocalDate date = LocalDate.parse(value);
            return date.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter("paymentMethod");
        if (value == null || value.isEmpty()) {
            errors.put("paymentMethod", "Value is required");
        } else {
            order.setPaymentMethod(PaymentMethod.valueOf(value));
        }
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}
