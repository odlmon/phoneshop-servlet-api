package com.es.phoneshop.web.product;

import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.RecentlyViewedProducts;
import com.es.phoneshop.model.cart.Cart;
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

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;
    private CartService cartService;
    private RecentlyViewedProductsService recentlyViewedProductsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = HttpSessionCartService.getInstance();
        recentlyViewedProductsService = HttpSessionRecentlyViewedProductsService.getInstance();
    }

    private Long getProductId(HttpServletRequest request) {
        return Long.valueOf(request.getPathInfo().substring(1));
    }

    private int getQuantity(HttpServletRequest request) throws ParseException {
        String quantityString = request.getParameter("quantity");
        NumberFormat format = NumberFormat.getInstance(request.getLocale());
        return format.parse(quantityString).intValue();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = getProductId(request);
        RecentlyViewedProducts products = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        try {
            request.setAttribute("product", productDao.getItem(productId));
            request.setAttribute("cart", cartService.getCart(request));
            request.setAttribute("recentlyViewed", products);
            request.getRequestDispatcher("/WEB-INF/pages/product/productDetails.jsp").forward(request, response);
            recentlyViewedProductsService.add(products, productId);
        } catch (NullValuePassedException e) {
            throw new RuntimeException("Null value passed", e);
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        doGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long productId = getProductId(request);
            int quantity = getQuantity(request);
            if (quantity < 1) {
                handleError(request, response, "Quantity can't be negative or zero");
                return;
            }
            Cart cart = cartService.getCart(request);
            cartService.add(cart, productId, quantity);
            response.sendRedirect(request.getContextPath() + "/products/" + productId +
                    "?message=Product added to cart");
        } catch (ParseException e) {
            handleError(request, response, "Not a number");
        } catch (OutOfStockException e) {
            handleError(request, response, "Out of stock, max available " + e.getStockAvailable());
        } catch (NullValuePassedException e) {
            throw new RuntimeException("Null value passed", e);
        }
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setRecentlyViewedProductsService(RecentlyViewedProductsService recentlyViewedProductsService) {
        this.recentlyViewedProductsService = recentlyViewedProductsService;
    }
}
