package com.es.phoneshop.web.order;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.dao.impl.ArrayListOrderDao;
import com.es.phoneshop.exception.NullValuePassedException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrderOverviewPageServlet extends HttpServlet {
    private OrderDao orderDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderDao = ArrayListOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("order", orderDao.getOrderBySecureId(getOrderSecureId(request)));
            request.getRequestDispatcher("/WEB-INF/pages/order/orderOverview.jsp").forward(request, response);
        } catch (NullValuePassedException e) {
            throw new RuntimeException("Null value passed", e);
        }
    }

    private String getOrderSecureId(HttpServletRequest request) {
        return request.getPathInfo().substring(1);
    }
}
