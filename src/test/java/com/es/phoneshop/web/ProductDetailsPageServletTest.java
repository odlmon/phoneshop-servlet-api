package com.es.phoneshop.web;

import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentlyViewedProducts;
import com.es.phoneshop.service.impl.HttpSessionCartService;
import com.es.phoneshop.service.impl.HttpSessionRecentlyViewedProductsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ArrayListProductDao productDao;
    @Mock
    private HttpSessionCartService cartService;
    @Mock
    private HttpSessionRecentlyViewedProductsService recentlyViewedProductsService;
    @InjectMocks
    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void setup() throws NullValuePassedException {
        Long id = 1L;
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getPathInfo()).thenReturn("/" + id.toString());
        when(request.getParameter("quantity")).thenReturn("1");
        when(request.getLocale()).thenReturn(Locale.getDefault());

        Currency usd = Currency.getInstance("USD");
        Product product = new Product(id, "test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        when(productDao.getProduct(id)).thenReturn(product);

        Cart cart = new Cart();
        cart.getItems().add(new CartItem(product, 1));
        when(cartService.getCart(request)).thenReturn(cart);

        RecentlyViewedProducts products = new RecentlyViewedProducts();
        products.getItems().offer(product);
        when(recentlyViewedProductsService.getRecentlyViewedProducts(request)).thenReturn(products);
    }

    @Test
    public void testDoGet() throws ServletException, IOException, NullValuePassedException {
        servlet.doGet(request, response);

        verify(request).getPathInfo();

        verify(requestDispatcher).forward(request, response);

        Product product = productDao.getProduct(1L);
        verify(request).setAttribute("product", product);
        Cart cart = cartService.getCart(request);
        verify(request).setAttribute("cart", cart);
        RecentlyViewedProducts products = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        verify(request).setAttribute("recentlyViewed", products);

        verify(recentlyViewedProductsService).add(products, 1L);
    }

    @Test
    public void testDoPost() throws ServletException, IOException, OutOfStockException, NullValuePassedException {
        servlet.doPost(request, response);

        verify(request).getPathInfo();
        verify(request).getParameter(eq("quantity"));

        Cart cart = cartService.getCart(request);
        verify(cartService).add(cart, 1L, 1);

        verify(response).sendRedirect(anyString());
    }
}
