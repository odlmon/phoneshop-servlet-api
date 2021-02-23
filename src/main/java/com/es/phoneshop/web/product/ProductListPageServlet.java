package com.es.phoneshop.web.product;

import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.RecentlyViewedProducts;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.HttpSessionCartService;
import com.es.phoneshop.service.impl.HttpSessionRecentlyViewedProductsService;
import com.es.phoneshop.service.RecentlyViewedProductsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {
    private ProductDao productDao;
    private RecentlyViewedProductsService recentlyViewedProductsService;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        recentlyViewedProductsService = HttpSessionRecentlyViewedProductsService.getInstance();
        cartService = HttpSessionCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String sortField = request.getParameter("sort");
        String sortOrder = request.getParameter("order");
        request.setAttribute("products", productDao.findProducts(query,
                Optional.ofNullable(sortField).map(SortField::valueOf).orElse(null),
                Optional.ofNullable(sortOrder).map(SortOrder::valueOf).orElse(null)
        ));
        RecentlyViewedProducts products = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        request.setAttribute("recentlyViewed", products);
        request.getRequestDispatcher("/WEB-INF/pages/product/productList.jsp").forward(request, response);
    }

    private Long getProductId(HttpServletRequest request) {
        return Long.valueOf(request.getParameter("productId"));
    }

    private int getQuantity(HttpServletRequest request) throws ParseException {
        String quantityString = request.getParameter("quantity");
        NumberFormat format = NumberFormat.getInstance(request.getLocale());
        return format.parse(quantityString).intValue();
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        doGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long productId = getProductId(request);
            int quantity = getQuantity(request);
            if (quantity < 1) {
                handleError(request, response, "Quantity can't be negative or zero");
                return;
            }
            Cart cart = cartService.getCart(request);
            cartService.add(cart, productId, quantity);
            response.sendRedirect(request.getContextPath() + "/products?message=Product added to cart");
        } catch (ParseException e) {
            handleError(request, response, "Not a number");
        }catch (OutOfStockException e) {
            handleError(request, response, "Out of stock, max available " + e.getStockAvailable());
        } catch (NullValuePassedException e) {
            throw new RuntimeException("Null value passed", e);
        }
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void setRecentlyViewedProductsService(RecentlyViewedProductsService recentlyViewedProductsService) {
        this.recentlyViewedProductsService = recentlyViewedProductsService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
